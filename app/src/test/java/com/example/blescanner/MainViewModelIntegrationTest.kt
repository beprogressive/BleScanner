package com.example.blescanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import app.cash.turbine.test
import com.example.blescanner.data.repositories.BleRepositoryImpl
import com.example.blescanner.domain.usecases.GetBleDevicesUseCase
import com.example.blescanner.ui.MainViewModel
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelIntegrationTest {

    private val bluetoothAdapter = mockk<BluetoothAdapter>()
    private val bluetoothLeScanner = mockk<BluetoothLeScanner>()
    private lateinit var repository: BleRepositoryImpl
    private lateinit var useCase: GetBleDevicesUseCase
    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        repository = BleRepositoryImpl(bluetoothAdapter)
        useCase = GetBleDevicesUseCase(repository)
        viewModel = MainViewModel(useCase)

        every { bluetoothAdapter.isEnabled } returns true
        every { bluetoothAdapter.bluetoothLeScanner } returns bluetoothLeScanner
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `scan devices successfully updates ViewModel devices`() = runTest {
        val mockScanResult = mockk<ScanResult>()
        val mockDevice = mockk<BluetoothDevice>()

        every { mockDevice.address } returns "AA:BB:CC:DD:EE:FF"
        every { mockDevice.name } returns "DeviceOne"
        every { mockScanResult.device } returns mockDevice
        every { mockScanResult.rssi } returns -50

        val callbackSlot = slot<ScanCallback>()

        every { bluetoothLeScanner.startScan(capture(callbackSlot)) } answers {
            callbackSlot.captured.onScanResult(
                ScanSettings.CALLBACK_TYPE_FIRST_MATCH,
                mockScanResult
            )
        }

        every { bluetoothLeScanner.stopScan(any<ScanCallback>()) } just Runs

        viewModel.startScan()

        advanceUntilIdle()

        viewModel.devices.test {
            val devices = awaitItem()
            assertEquals(1, devices.size)
            assertEquals("DeviceOne", devices[0].name)
            assertEquals("AA:BB:CC:DD:EE:FF", devices[0].address)
            assertEquals(-50, devices[0].rssi)
        }
    }
}