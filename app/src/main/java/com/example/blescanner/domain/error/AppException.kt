package com.example.blescanner.domain.error

import com.example.blescanner.data.exceptions.BluetoothNotAvailableException
import com.example.blescanner.data.exceptions.BluetoothNotEnabledException
import com.example.blescanner.data.exceptions.ScanFailedException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

sealed class AppException : Exception() {
    object NoInternet : AppException()
    object Timeout : AppException()
    object BluetoothUnavailable : AppException()
    object BluetoothNotEnabled : AppException()
    object ScanFailed : AppException()
    data class Unknown(val exception: Exception) : AppException()
}

fun Exception.toAppException(): AppException {
    return when (this) {
        is UnknownHostException -> AppException.NoInternet
        is TimeoutException -> AppException.Timeout
        is BluetoothNotEnabledException -> AppException.BluetoothNotEnabled
        is BluetoothNotAvailableException -> AppException.BluetoothUnavailable
        is ScanFailedException -> AppException.ScanFailed
        else -> AppException.Unknown(this)
    }
}