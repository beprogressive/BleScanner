package com.example.blescanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blescanner.domain.models.Device
import com.example.blescanner.domain.result.Result
import com.example.blescanner.domain.usecases.GetBleDevices
import com.example.blescanner.domain.usecases.StopScan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBleDevices: GetBleDevices,
    private val stopScan: StopScan
) : ViewModel() {

    private val _state: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.Loading)
    val state = _state.asStateFlow()

    init {
        Timber.d("MainViewModel initialized")
    }

    fun onStartScanClick() {
        Timber.d("Button clicked")
        _state.value = ScreenState.Loading
        viewModelScope.launch {
            getBleDevices()
                .map { result ->
                    if (result is Result.Success) {
                        Result.Success(result.data.sortedBy {
                            it.rssi
                        }.toSet())
                    } else
                        result
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            Timber.d("Success: ${result.data}")
                            _state.value = ScreenState.Success(result.data)
                        }

                        is Result.Error -> {
                            Timber.e("Error: ${result.error}")
                            _state.value =
                                ScreenState.Error(result.error.message ?: "Unknown error")
                        }
                    }
                }

            // Stop scanning after 10 seconds
            delay(10_000)
            stopScan()
        }
    }
}

sealed class ScreenState {
    object Loading : ScreenState()
    data class Success(val devices: Set<Device>) : ScreenState()
    data class Error(val message: String) : ScreenState()
}