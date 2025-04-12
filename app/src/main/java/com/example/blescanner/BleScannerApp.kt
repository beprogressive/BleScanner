package com.example.blescanner

import android.app.Application
import timber.log.Timber

class BleScannerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}