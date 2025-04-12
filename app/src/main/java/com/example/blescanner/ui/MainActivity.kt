package com.example.blescanner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.blescanner.ui.theme.AppTheme
import com.example.blescanner.utils.logd

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logd("MainActivity onCreate called")

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }
}