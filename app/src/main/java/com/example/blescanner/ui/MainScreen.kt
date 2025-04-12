package com.example.blescanner.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.blescanner.ui.components.SimpleButton

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val isScanning = remember { mutableStateOf(false) }
    val buttonText = remember { mutableStateOf("Scan") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(
            text = "Ble Scan",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge
        )
        SimpleButton(
            text = buttonText.value,
            onClick = viewModel::onStartScanClick,
            modifier = Modifier.padding(top = 16.dp)
        )
    }

    if (isScanning.value) {
        buttonText.value = "Stop Scan"
        viewModel.onStartScanClick()
    } else {
        buttonText.value = "Scan"
    }

}

@Composable
@Preview(showBackground = true)
fun PreviewMainScreen() {
    MainScreen()
}