package com.example.blescanner.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.blescanner.domain.models.Device

/**
 * Displays a list of BLE devices with their names and signal strength.
 *
 * @param deviceList List of devices to display
 */
@Composable
fun ColumnScope.DeviceList(deviceList: List<Device>) {

    val listState = rememberLazyListState()

    if (deviceList.isEmpty()) Row(
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
        items(deviceList.size) { index ->
            val device = deviceList[index]
            Text(
                text = "${device.name} - ${device.rssi}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}