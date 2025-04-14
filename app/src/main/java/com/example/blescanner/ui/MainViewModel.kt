package com.example.blescanner.ui

import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blescanner.domain.models.Device
import com.example.blescanner.domain.result.ScanResult
import com.example.blescanner.domain.usecases.GetBleDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBleDevicesUseCase: GetBleDevicesUseCase,
) : ViewModel() {

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState = _scanState.asStateFlow()

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        _devices.value = emptyList()
        _scanState.value = ScanState.Scanning
        viewModelScope.launch {
            getBleDevicesUseCase.startScan().collect { result ->
                when (result) {
                    is ScanResult.Success -> {
                        _devices.value = result.devices
                        _scanState.value = ScanState.Scanning
                    }

                    is ScanResult.Error -> {
                        _scanState.value = ScanState.Error(result.error.message ?: "Unknown error")
                    }

                    is ScanResult.Finished -> {
                        _scanState.value = ScanState.Finished
                    }
                }
            }
        }
    }

    fun stopScan() {
        getBleDevicesUseCase.stopScan()
    }
}

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    object Finished : ScanState()
    data class Error(val message: String) : ScanState()
}