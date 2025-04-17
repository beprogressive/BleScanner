package com.example.blescanner.domain.error

import com.example.blescanner.data.exceptions.BluetoothNotAvailableException
import com.example.blescanner.data.exceptions.BluetoothNotEnabledException
import com.example.blescanner.data.exceptions.ScanFailedException

/**
 * Represents exceptions specific to the application domain.
 * Provides structured error handling for UI and business logic.
 */
sealed class AppException : Exception() {
    /**
     * Indicates that Bluetooth is not available on the device.
     * This can occur on devices without Bluetooth hardware or
     * when the Bluetooth adapter cannot be accessed.
     */
    object BluetoothUnavailable : AppException()
    /**
     * Indicates that Bluetooth is available but not enabled.
     * The user should be prompted to enable Bluetooth in device settings.
     */
    object BluetoothNotEnabled : AppException()
    /**
     * Indicates that a BLE scan operation failed.
     * This can happen due to hardware issues, insufficient permissions,
     * or other system-level problems with the Bluetooth adapter.
     */
    object ScanFailed : AppException()
    /**
     * Represents unspecified exception that occurred.
     * Contains the original exception for debugging purposes.
     *
     * @property exception The original exception that was caught
     */
    data class Unknown(val exception: Exception) : AppException()
}

/**
 * Extension function that converts general exceptions to domain-specific [AppException] types.
 */
fun Exception.toAppException(): AppException {
    return when (this) {
        is BluetoothNotEnabledException -> AppException.BluetoothNotEnabled
        is BluetoothNotAvailableException -> AppException.BluetoothUnavailable
        is ScanFailedException -> AppException.ScanFailed
        else -> AppException.Unknown(this)
    }
}