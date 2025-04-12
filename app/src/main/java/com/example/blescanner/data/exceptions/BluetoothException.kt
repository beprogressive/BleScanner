package com.example.blescanner.data.exceptions

class BluetoothNotAvailableException : Exception("Bluetooth is not available")
class ScanFailedException(errorCode: Int) : Exception("Scan failed, errorCode: $errorCode")