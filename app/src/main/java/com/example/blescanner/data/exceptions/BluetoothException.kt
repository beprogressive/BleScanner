package com.example.blescanner.data.exceptions

class BluetoothNotAvailableException(message: String) : Exception(message)
class BluetoothNotEnabledException(message: String) : Exception(message)
class ScanFailedException(errorCode: Int) : Exception("Scan failed, errorCode: $errorCode")