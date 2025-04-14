package com.example.blescanner.data.repositories

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.annotation.RequiresPermission
import com.example.blescanner.data.exceptions.BluetoothNotAvailableException
import com.example.blescanner.data.exceptions.BluetoothNotEnabledException
import com.example.blescanner.data.exceptions.ScanFailedException
import com.example.blescanner.domain.models.Device
import com.example.blescanner.domain.repositories.BleRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleRepositoryImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?,
) : BleRepository {

    var scanner: BluetoothLeScanner? = null
    private var scanCallback: ScanCallback? = null

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    override fun scanBleDevices(): Flow<Device> = callbackFlow {
        Timber.d("scanBleDevices")

        bluetoothAdapter
            ?: throw BluetoothNotAvailableException("BluetoothAdapter is not available")

        if (bluetoothAdapter.isEnabled != true) {
            throw BluetoothNotEnabledException("Bluetooth is not enabled")
        }

        scanner = bluetoothAdapter.bluetoothLeScanner
            ?: throw BluetoothNotAvailableException("BluetoothLeScanner is not available")

        scanCallback = object : ScanCallback() {
            @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.device?.let { device ->
                    trySend(Device(device.address, device.name ?: "", result.rssi))
                }
            }

            @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
            override fun onBatchScanResults(results: List<ScanResult?>?) {
                results?.forEach { result ->
                    onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, result)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                close(ScanFailedException(errorCode))
            }
        }

        scanner?.startScan(scanCallback)

        awaitClose {
            scanner?.stopScan(scanCallback)
        }
    }

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        Timber.d("stopScan")
        scanner?.stopScan(scanCallback)
        scanCallback = null
    }
}
