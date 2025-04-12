package com.example.blescanner.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.blescanner.data.repositories.BleRepoImpl
import com.example.blescanner.domain.repositories.BleRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BleModule {

    @Provides
    fun provideBluetoothManager(
        @ApplicationContext context: Context
    ): BluetoothManager {
        return context.getSystemService(BluetoothManager::class.java)
            ?: throw IllegalStateException("BluetoothManager not available")
    }

    @Provides
    fun provideBluetoothAdapter(
        bluetoothManager: BluetoothManager
    ): BluetoothAdapter {
        return bluetoothManager.adapter
            ?: throw IllegalStateException("BluetoothAdapter not available")
    }

    @Provides
    fun provideBleRepo(
        bluetoothAdapter: BluetoothAdapter
    ): BleRepo {
        return BleRepoImpl(bluetoothAdapter)
    }
}