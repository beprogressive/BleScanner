package com.example.blescanner.data.repositories

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.annotation.RequiresPermission
import com.example.blescanner.data.exceptions.BluetoothNotAvailableException
import com.example.blescanner.data.exceptions.ScanFailedException
import com.example.blescanner.domain.error.toAppException
import com.example.blescanner.domain.models.Device
import com.example.blescanner.domain.repositories.BleRepo
import com.example.blescanner.domain.result.Result
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BleRepoImpl @Inject constructor(private val bluetoothAdapter: BluetoothAdapter) : BleRepo {

    private var callback: ScanCallback? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startScan() = callbackFlow {
        val foundDevices = mutableSetOf<Device>()
        val bleScanner = bluetoothAdapter.bluetoothLeScanner

        if (bleScanner == null) {
            cancel("BLE scanner unavailable")
            trySend(Result.Error(BluetoothNotAvailableException().toAppException()))
        }

        callback?.let {
            bleScanner.stopScan(callback)
        }

        callback = object : ScanCallback() {

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                handleResult(result)
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                results?.forEach {
                    handleResult(it)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                callback = null
                cancel("Scan failed, errorCode: $errorCode")
                trySend(Result.Error(ScanFailedException(errorCode).toAppException()))
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            fun handleResult(result: ScanResult) {
                Device(
                    address = result.device.address,
                    name = result.device?.name ?: "Unknown",
                    rssi = result.rssi
                ).let { device ->
                    if (foundDevices.none { it.address == device.address }) {
                        foundDevices.add(device)
                        launch { trySend(Result.Success(foundDevices.toSet())) }
                    } else {
                        foundDevices.first { it.address == device.address }.let {
                            if (device.name != "Unknown" && it.name == "Unknown") {
                                val updatedDevice = it.copy(name = device.name)
                                foundDevices.remove(it)
                                foundDevices.add(updatedDevice)
                                launch { trySend(Result.Success(foundDevices.toSet())) }
                            }
                        }
                    }
                }
            }
        }

        val filters = listOf<ScanFilter>()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bleScanner.startScan(filters, settings, callback)

        invokeOnClose {
            bleScanner.stopScan(callback)
            callback = null
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        callback?.let {
            bluetoothAdapter.bluetoothLeScanner?.stopScan(it)
            callback = null
        }
    }
}