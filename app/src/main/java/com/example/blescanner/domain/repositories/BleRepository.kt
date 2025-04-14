package com.example.blescanner.domain.repositories

import androidx.annotation.RequiresPermission
import com.example.blescanner.domain.models.Device
import kotlinx.coroutines.flow.Flow

interface BleRepository {
//    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun scanBleDevices(): Flow<Device>
    fun stopScan()
}