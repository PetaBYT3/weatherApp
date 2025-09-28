package com.weatherapp.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class PermissionIntent {

    fun openAppSettings(context: Context) {
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        settingsIntent.data = uri
        context.startActivity(settingsIntent)
    }
}