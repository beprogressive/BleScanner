package com.example.blescanner.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.blescanner.data.repositories.BleRepositoryImpl
import com.example.blescanner.domain.repositories.BleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides Bluetooth-related dependencies for the application.
 * Configures how Bluetooth services and adapters are injected.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BleModule {

    @Binds
    @Singleton
    abstract fun bindBleRepo(
        bleRepoImpl: BleRepositoryImpl
    ): BleRepository

    companion object {
        @Singleton
        @Provides
        fun provideBluetoothManager(
            @ApplicationContext context: Context
        ): BluetoothManager? {
            return context.getSystemService(BluetoothManager::class.java)
        }

        @Singleton
        @Provides
        fun provideBluetoothAdapter(
            bluetoothManager: BluetoothManager?
        ): BluetoothAdapter? {
            return bluetoothManager?.adapter
        }
    }
}