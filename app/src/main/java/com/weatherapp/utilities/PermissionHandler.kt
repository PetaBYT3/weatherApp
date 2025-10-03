package com.weatherapp.utilities

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.weatherapp.intent.LocationAction

fun notificationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionAsked: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onPermissionAsked
    } else {
        onPermissionGranted()
    }
}
