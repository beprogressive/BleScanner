package com.example.blescanner.domain.repositories

import com.example.blescanner.domain.models.Device
import com.example.blescanner.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface BleRepo {
    fun startScan(): Flow<Result<Set<Device>>>
    fun stopScan()
}