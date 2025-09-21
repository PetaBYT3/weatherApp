package com.weatherapp.dialog

import android.widget.EditText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weatherapp.roomdata.event.LocationEvent
import com.weatherapp.roomdata.state.LocationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInputDialog(
    state: LocationState,
    onEvent: (LocationEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = Modifier,
        onDismissRequest = {
            onEvent(LocationEvent.HideDialog)
        },
        title = { Text(text = "Add New Location") },
        text = {
            Column {
                TextField(
                    value = state.locationName,
                    onValueChange = {
                        onEvent(LocationEvent.SetLocationName(it))
                    },
                    placeholder = {
                        Text(text = "City Name")
                    }
                )
            }
        },
        confirmButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        onEvent(LocationEvent.SaveLocation)
                    }
                ) {
                    Text(text = "Save City")
                }
            }
        }
    )
}