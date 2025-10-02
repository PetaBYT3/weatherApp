package com.weatherapp.utilities

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.net.toUri

fun intentApp(
    webUrl: String,
    appPackage: String,
    context: Context

) {
    val appIntent = Intent(Intent.ACTION_VIEW, webUrl.toUri())
    appIntent.setPackage(appPackage)

    val packageManager = context.packageManager
    val resolvedApp = packageManager.resolveActivity(appIntent, 0)

    if (resolvedApp != null) {
        context.startActivity(appIntent)
    } else {
        context.startActivity(Intent(Intent.ACTION_VIEW, webUrl.toUri()))
    }
}

fun copyToClipboard(
    label: String,
    text: String,
    toast: String,
    context: Context
) {
    val clipBoardManager = context.getSystemService(
        Context.CLIPBOARD_SERVICE
    ) as android.content.ClipboardManager

    val clipData = ClipData.newPlainText(
        label,
        text
    )

    clipBoardManager.setPrimaryClip(clipData)

    Toast.makeText(context, toast, Toast.LENGTH_LONG).show()
}