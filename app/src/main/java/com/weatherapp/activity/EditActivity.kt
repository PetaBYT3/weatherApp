package com.weatherapp.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.weatherapp.dialog.BottomSheetTextField
import com.weatherapp.roomdata.dataclass.Location
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.viewmodel.LocationViewModel
import com.weatherapp.viewmodel.ViewModelSettings


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
private fun MainScreen(
    vmSettings: ViewModelSettings = viewModel()
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    BottomSheetTextField(isSheetOpen)
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
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val dummyLocation = Location(
                                locationName = "Serang"
                            )
                            vmSettings.insertLocation(dummyLocation)
                            isSheetOpen = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
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
    val locationList by vmSettings.allLocation.collectAsState(emptyList())

    val gpsPrefs by vmSettings.gpsSettings.collectAsStateWithLifecycle(false)
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(locationList) { location->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = location.locationName)
                        Text(text = location.uId.toString())
                    }
                    IconButton(
                        onClick = {
                            vmSettings.deleteLocation(location)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete Location"
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MainScreenPreview() {
    WeatherAppTheme {
    }
}

