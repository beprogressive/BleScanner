package com.example.blescanner.domain.usecases

import androidx.annotation.RequiresPermission
import com.example.blescanner.domain.error.toAppException
import com.example.blescanner.domain.models.Device
import com.example.blescanner.domain.repositories.BleRepository
import com.example.blescanner.domain.result.ScanResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


/**
 * Use case for scanning BLE devices.
 *
 * @property repository The repository to interact with BLE devices.
 */
class GetBleDevicesUseCase @Inject constructor(
    private val repository: BleRepository
) {

    private companion object {
        const val SCAN_TIMEOUT = 10_000L
    }

    private var scanJob: Job? = null
    private var autoStopJob: Job? = null


    /**
     * Starts scanning for BLE devices.
     *
     * @param timeout The duration to scan for devices in milliseconds. Default is 10 seconds.
     * @return A flow of [ScanResult] containing the scanned devices or errors.
     */
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(timeout: Long = SCAN_TIMEOUT): Flow<ScanResult> = channelFlow {
        Timber.d("startScan")
        val devices = mutableListOf<Device>()

        scanJob = launch {
            try {
                repository.scanBleDevices().collect { device ->
                    val existing = devices.find { it.address == device.address }
                    if (existing != null) {
                        existing.rssi = device.rssi
                        if (device.name.isNotEmpty()) existing.name = device.name
                    } else {
                        if (device.name.isEmpty()) device.name = "Unknown"
                        devices.add(device)
                    }
                    devices.sortByDescending { it.rssi }
                    send(ScanResult.Success(devices.toList()))
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Timber.d("Use case: Scan collection cancelled.")
                } else {
                    Timber.e(e, "Use case: Scan failed")
                    send(ScanResult.Error(e.toAppException()))
                }
            }
        }

        autoStopJob = launch {
            delay(timeout)
            Timber.d("Auto-stop after $timeout ms")
            if (isActive) {
                send(ScanResult.Finished)
                close()
            }
        }

        awaitClose {
            autoStopJob?.cancel()
            repository.stopScan()
        }
    }

    /**
     * Stops scanning for BLE devices and cancels any ongoing scan jobs.
     */
    fun stopScan() {
        scanJob?.cancel()
        autoStopJob?.cancel()
        repository.stopScan()
    }
}