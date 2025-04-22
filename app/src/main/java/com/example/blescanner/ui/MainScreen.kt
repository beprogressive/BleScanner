package com.example.blescanner.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.blescanner.ui.components.MainScreenContent
import com.example.blescanner.ui.components.RequestPermissions
import com.example.blescanner.utils.Permissions.bluetoothPermissions

/**
 * Main screen of the application displaying BLE device scanning functionality.
 * Handles permission requests and displays scan results.
 *
 * @param viewModel ViewModel providing scan state and device data
 */

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val scanState by viewModel.scanState.collectAsStateWithLifecycle()
    val deviceList by viewModel.devices.collectAsStateWithLifecycle()

    var requestPermissionsTrigger by remember { mutableStateOf(false) }

    MainScreenContent(
        scanState = scanState,
        deviceList = deviceList,
        modifier = modifier,
        onScanClick = {
            when (scanState) {
                is ScanState.Scanning -> {
                    viewModel.stopScan()
                }

                else -> {
                    requestPermissionsTrigger = true
                }
            }
        }
    )

    if (requestPermissionsTrigger) {
        RequestPermissions(
            permissions = bluetoothPermissions,
            onGranted = {
                viewModel.startScan()
                requestPermissionsTrigger = false
            },
            onRejected = {
                requestPermissionsTrigger = false
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewMainScreen() {
    MainScreen()
}