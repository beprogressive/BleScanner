package com.example.blescanner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class for the BLE Scanner app.
 * Handles global initialization including Timber logging and Hilt.
 */
@HiltAndroidApp
class BleScannerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}