package com.example.blescanner.domain.error

import com.example.blescanner.data.exceptions.BluetoothNotAvailableException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

sealed class AppException : Exception() {
    object NoInternet : AppException() {
        fun readResolve(): Any = NoInternet
    }

    object Timeout : AppException() {
        fun readResolve(): Any = Timeout
    }

    object BluetoothUnavailable  : AppException() {
        fun readResolve(): Any = BluetoothUnavailable
    }

    data class Unknown(override val cause: Throwable) : AppException()
}

fun Exception.toAppException(): AppException {
    return when (this) {
        is UnknownHostException -> AppException.NoInternet
        is TimeoutException -> AppException.Timeout
        is BluetoothNotAvailableException -> AppException.BluetoothUnavailable
        else -> AppException.Unknown(this)
    }
}