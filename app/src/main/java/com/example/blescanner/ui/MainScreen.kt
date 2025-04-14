package com.example.blescanner.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.blescanner.R
import com.example.blescanner.ui.components.SimpleButton

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.scanState.collectAsStateWithLifecycle()
    val deviceList = viewModel.devices.collectAsStateWithLifecycle()
    var isScanning by remember { mutableStateOf(false) }
    val startScanning = remember { mutableStateOf(false) }
//    val shouldRequestPermissions = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val permissions = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.app_name),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        if (deviceList.value.isEmpty()) Row(
            modifier = Modifier.weight(1F), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "No devices",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
        else LazyColumn(
            modifier = Modifier.weight(1f), state = listState
        ) {
            items(deviceList.value.size) { index ->
                val device = deviceList.value[index]
                Text(
                    text = "${device.name} - ${device.rssi}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        SimpleButton(
            text = if (isScanning) "Stop Scan" else "Scan", onClick = {
                if (isScanning) {
                    viewModel.stopScan()
                } else {
                    startScanning.value = true
                }
            }, modifier = Modifier.padding(top = 16.dp)
        )
    }

    if (startScanning.value) {
        RequestBluetoothPermissions(permissions = permissions, onGranted = {
            viewModel.startScan()
            startScanning.value = false
        }, onRejected = {
            startScanning.value = false
        })
    }

}

@Composable
fun RequestBluetoothPermissions(
    permissions: List<String>, onGranted: () -> Unit, onRejected: () -> Unit
) {
    val context = LocalContext.current

    val permissionStates = remember { mutableStateMapOf<String, Boolean>() }
    var checked by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionStates.putAll(results)
        checked = true
    }

    LaunchedEffect(Unit) {
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            launcher.launch(notGranted.toTypedArray())
        } else {
            permissions.forEach { permissionStates[it] = true }
            checked = true
        }
    }

    when {
        !checked -> Unit
        permissions.all { permissionStates[it] == true } -> {
            onGranted()
        }

        else -> {
            onRejected()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewMainScreen() {
    MainScreen()
}