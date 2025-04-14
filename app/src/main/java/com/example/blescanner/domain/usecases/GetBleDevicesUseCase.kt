package com.example.blescanner.domain.usecases

import androidx.annotation.RequiresPermission
import com.example.blescanner.domain.error.toAppException
import com.example.blescanner.domain.models.Device
import com.example.blescanner.domain.repositories.BleRepository
import com.example.blescanner.domain.result.ScanResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class GetBleDevicesUseCase @Inject constructor(
    private val repository: BleRepository
) {
    private var scanJob: Job? = null
    private var autoStopJob: Job? = null

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(timeout: Long = 10_000L): Flow<ScanResult> = channelFlow {
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
                send(ScanResult.Error(e.toAppException()))
                cancel()
            }
        }

        autoStopJob = launch {
            delay(timeout)
            Timber.d("Auto-stop after $timeout ms")
            send(ScanResult.Finished)
            close()
        }

        awaitClose {
            Timber.d("awaitClose")
            scanJob?.cancel()
            autoStopJob?.cancel()
            repository.stopScan()
        }
    }

    fun stopScan() {
        Timber.d("stopScan")
        scanJob?.cancel()
        autoStopJob?.cancel()
        repository.stopScan()
    }
}