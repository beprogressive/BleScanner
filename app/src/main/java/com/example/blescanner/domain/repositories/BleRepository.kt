package com.example.blescanner.domain.repositories

import androidx.annotation.RequiresPermission
import com.example.blescanner.domain.models.Device
import kotlinx.coroutines.flow.Flow

/**
 * Repository for interacting with Bluetooth Low Energy devices.
 * Provides methods for scanning, connecting, and interacting with BLE devices.
 */
interface BleRepository {
    /**
     * Starts scanning for BLE devices in the vicinity.
     *
     * @return Flow of [Device] objects representing discovered BLE devices.
     * Each device emitted may be a new discovery or an update to an existing device.
     *
     * @throws BluetoothNotAvailableException if Bluetooth is not supported on this device
     * @throws BluetoothNotEnabledException if Bluetooth is not enabled
     * @throws ScanFailedException if scanning fails for any reason
     */
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun scanBleDevices(): Flow<Device>

    /**
     * Stops an ongoing BLE scan operation.
     * If no scan is in progress, this method does nothing.
     */
    fun stopScan()

    /**
     * Connects to a specified BLE device.
     *
     * @param device The [Device] object representing the BLE device to connect to.
     * @return A Flow that emits the connection status or any errors encountered during the process.
     */
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: Device): Flow<Result<Unit>>

    /**
     * Disconnects from a specified BLE device.
     *
     * @param device The [Device] object representing the BLE device to disconnect from.
     * @return A Flow that emits the disconnection status or any errors encountered during the process.
     */
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnectFromDevice(device: Device): Flow<Result<Unit>>

    /**
     * Reads data from a specified characteristic of a BLE device.
     *
     * @param device The [Device] object representing the BLE device to read from.
     * @param characteristicUuid The UUID of the characteristic to read from.
     * @return A Flow that emits the read data or any errors encountered during the process.
     */
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun readCharacteristic(device: Device, characteristicUuid: String): Flow<Result<ByteArray>>

    /**
     * Reads all characteristics from a specified BLE device.
     *
     * @param device The [Device] object representing the BLE device to read from.
     * @return A Flow that emits a map of characteristic UUIDs to their corresponding data,
     * or any errors encountered during the process.
     */
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun readAllCharacteristics(device: Device): Flow<Result<Map<String, ByteArray>>>

    /**
     * Writes data to a specified characteristic of a BLE device.
     *
     * @param device The [Device] object representing the BLE device to write to.
     * @param characteristicUuid The UUID of the characteristic to write to.
     * @param data The data to write to the characteristic.
     * @return A Flow that emits the write status or any errors encountered during the process.
     */
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun writeCharacteristic(device: Device, characteristicUuid: String, data: ByteArray): Flow<Result<Unit>>
}