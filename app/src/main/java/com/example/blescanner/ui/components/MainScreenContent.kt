package com.example.blescanner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.blescanner.R
import com.example.blescanner.domain.models.Device

@Composable
fun MainScreenContent(isScanning: Boolean, deviceList: List<Device>, onScanClick: () -> Unit) {
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

        DeviceList(deviceList = deviceList)

        SimpleButton(
            text = if (isScanning) "Stop Scan" else "Scan",
            onClick = onScanClick,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}