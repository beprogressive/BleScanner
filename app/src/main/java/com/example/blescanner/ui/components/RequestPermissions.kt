package com.example.blescanner.ui.components

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermissions(
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