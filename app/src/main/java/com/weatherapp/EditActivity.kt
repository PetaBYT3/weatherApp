package com.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.viewmodel.ViewModelSettings

@ExperimentalMaterial3Api
class EditActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen() {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Location Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            (context as? ComponentActivity)?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        ContentScreen(Modifier.padding(innerPadding))
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    vmSettings: ViewModelSettings = viewModel()
) {
    val gpsPrefs by vmSettings.gpsSettings.collectAsStateWithLifecycle(false)
    val locationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    Column(
        modifier = modifier
            .padding(horizontal = 15.dp, vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Get Location From GPS",
                modifier = Modifier
                    .weight(1f)
            )
            Switch(
                checked = gpsPrefs,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        if (locationPermissionState.status.isGranted) {
                            vmSettings.setGpsPrefs(true)
                        } else {
                            locationPermissionState.launchPermissionRequest()
                        }
                    } else {
                        vmSettings.setGpsPrefs(false)
                    }
                }
            )
        }
        Text(text = gpsPrefs.toString())
    }
}

@Composable
@Preview(showBackground = true)
private fun MainScreenPreview() {
    WeatherAppTheme {
    }
}

