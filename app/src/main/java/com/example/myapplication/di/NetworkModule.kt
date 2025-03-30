package com.example.myapplication.di

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleService
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.api.network.AuthInterceptor
import com.example.myapplication.domain.Repository
import com.example.myapplication.data.api.network.RepositoryImpl
import com.example.myapplication.data.api.network.TokenProviderImpl
import com.example.myapplication.domain.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideTokenProvider(): TokenProvider =
        TokenProviderImpl()

    @Provides
    fun provideAuthInterceptor(tokenProvider: TokenProvider): AuthInterceptor =
        AuthInterceptor(tokenProvider)

    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService): Repository =
        RepositoryImpl(apiService)

    @Provides
    fun provideUUID(): UUID = UUID.randomUUID()
}

@Module
@InstallIn(SingletonComponent::class)
object BleModule {

    @Provides
    fun provideBluetoothManager(
        @ApplicationContext context: Context
    ): BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    @Provides
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager): BluetoothAdapter =
        bluetoothManager.adapter

    @Provides
    fun provideBleScanner(
        bluetoothAdapter: BluetoothAdapter
    ): BleScanner = BleScannerImpl(bluetoothAdapter)
}

interface BleScanner {
    fun startScan(timeOut: Long): Flow<List<BluetoothDevice>>
    fun stopScan()
}

class BleScannerImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter
) : BleScanner {

    private val foundDevices = mutableSetOf<BluetoothDevice>()
    private val _devicesFlow = MutableSharedFlow<List<BluetoothDevice>>(replay = 1)

    private val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                if (foundDevices.add(device)) {
                    _devicesFlow.tryEmit(foundDevices.toList())
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { result ->
                result.device?.let { device ->
                    if (foundDevices.add(device)) {
                        _devicesFlow.tryEmit(foundDevices.toList())
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startScan(timeOut: Long): Flow<List<BluetoothDevice>> = channelFlow {
        foundDevices.clear()

        val scanner = bluetoothAdapter.bluetoothLeScanner
        val filters = emptyList<ScanFilter>() // или добавь фильтры, если надо
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(filters, settings, callback)

        val job = launch {
            _devicesFlow.collect {
                send(it) // пушим наружу через channelFlow
            }
        }

        delay(timeOut)

        scanner.stopScan(callback)
        job.cancel()

        close() // закрываем поток
    }.flowOn(Dispatchers.Main) // BLE-скан должен идти в main thread

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        bluetoothAdapter.bluetoothLeScanner.stopScan(callback)
    }
}
