package com.example.blescanner.domain.result

import com.example.blescanner.domain.models.Device

sealed class ScanResult {
    data class Success(val devices: List<Device>) : ScanResult()
    object Finished : ScanResult()
    data class Error(val error: Throwable) : ScanResult()
}
