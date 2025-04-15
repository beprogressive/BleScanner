package com.example.blescanner.repository

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import app.cash.turbine.test
import com.example.blescanner.data.exceptions.BluetoothNotAvailableException
import com.example.blescanner.data.exceptions.BluetoothNotEnabledException
import com.example.blescanner.data.repositories.BleRepositoryImpl
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Before
import org.junit.Test

class BleRepositoryImplTest {

    val bluetoothAdapter = mockk<BluetoothAdapter>()
    lateinit var repository: BleRepositoryImpl

    @Before
    fun setup() {
        repository = BleRepositoryImpl(bluetoothAdapter)
    }

    @Test(expected = BluetoothNotAvailableException::class)
    fun `should throw exception when BluetoothAdapter is null`() = runTest {
        val repo = BleRepositoryImpl(null)
        repo.scanBleDevices().first()
    }

    @Test(expected = BluetoothNotEnabledException::class)
    fun `should throw exception when Bluetooth is disabled`() = runTest {
        every { bluetoothAdapter.isEnabled } returns false
        repository.scanBleDevices().first()
    }

    @Test
    fun `should emit device when onScanResult is called`() = runTest {
        every { bluetoothAdapter.isEnabled } returns true

        val mockScanner = mockk<BluetoothLeScanner>()
        every { bluetoothAdapter.bluetoothLeScanner } returns mockScanner

        val device = mockk<BluetoothDevice>()
        every { device.address } returns "11:22:33:44:55:66"
        every { device.name } returns "BLE Tester"

        val scanResult = mockk<ScanResult>()
        every { scanResult.device } returns device
        every { scanResult.rssi } returns -42

        val callbackSlot = slot<ScanCallback>()
        every { mockScanner.startScan(capture(callbackSlot)) } just Runs
        every { mockScanner.stopScan(any<ScanCallback>()) } just Runs

        val flow = repository.scanBleDevices()

        val job = launch {
            flow.test {
                val result = awaitItem()
                assertEquals("11:22:33:44:55:66", result.address)
                assertEquals("BLE Tester", result.name)
                assertEquals(-42, result.rssi)
                cancelAndIgnoreRemainingEvents()
            }
        }
        
        yield()
        callbackSlot.captured.onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, scanResult)

        job.join()
    }
}