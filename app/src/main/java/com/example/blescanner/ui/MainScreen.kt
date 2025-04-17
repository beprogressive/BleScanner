package com.example.blescanner.ui

import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.blescanner.ui.components.MainScreenContent
import com.example.blescanner.ui.components.RequestPermissions
import com.example.blescanner.utils.Permissions.bluetoothPermissions

@RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.scanState.collectAsStateWithLifecycle()
    val deviceList = viewModel.devices.collectAsStateWithLifecycle()
    var isScanning by remember { mutableStateOf(false) }
    val startScanning = remember { mutableStateOf(false) }

    when (state.value) {
        is ScanState.Error -> {
            isScanning = false
        }

        is ScanState.Scanning -> {
            isScanning = true
        }

        is ScanState.Finished -> {
            isScanning = false
        }

        ScanState.Idle -> Unit
    }

    MainScreenContent(
        isScanning = isScanning,
        deviceList = deviceList.value,
        onScanClick = {
            if (isScanning) {
                viewModel.stopScan()
            } else {
                startScanning.value = true
            }
        }
    )

    if (startScanning.value) {
        RequestPermissions(permissions = bluetoothPermissions, onGranted = {
            viewModel.startScan()
            startScanning.value = false
        }, onRejected = {
            startScanning.value = false
        })
    }

}

@RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
@Composable
@Preview(showBackground = true)
fun PreviewMainScreen() {
    MainScreen()
}