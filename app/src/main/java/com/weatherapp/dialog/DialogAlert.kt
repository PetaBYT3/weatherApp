package com.weatherapp.dialog

import android.widget.EditText
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInputDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var locationName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Masukkan Nama Lokasi")
        },
        text = {
            Column {
                TextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Nama Lokasi") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(locationName)
                    onDismissRequest()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Batal")
            }
        }
    )
}

@Preview
@Composable
fun LocationInputDialogPreview() {
    // Fungsi preview, tidak akan melakukan apa-apa saat tombol diklik
    // Hanya untuk melihat tampilannya
    LocationInputDialog(
        onDismissRequest = {},
        onConfirm = {}
    )
}