package com.weatherapp.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.valentinilk.shimmer.shimmer
import com.weatherapp.intent.LocationAction
import com.weatherapp.module.capitalizeWords
import com.weatherapp.roomdata.dataclass.Location
import com.weatherapp.settings.PermissionIntent
import com.weatherapp.state.LocationState
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                MainScreenCore()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenCore(
    viewModel: LocationViewModel = hiltViewModel()
) {
    val uiState: LocationState by viewModel.uiState.collectAsStateWithLifecycle()
    MainScreen(uiState = uiState, onAction = viewModel::onAction)

    if (uiState.bottomSheetInsert) {
        BottomSheetInput(state = uiState, onAction = viewModel::onAction)
    }

    if (uiState.bottomSheetDelete) {
        BottomSheetDelete(uiState = uiState, onAction = viewModel::onAction)
    }

    if (uiState.bottomSheetSelect) {
        BottomSheetSelectLocation(uiState = uiState, onAction = viewModel::onAction)
    }

    if (uiState.bottomSheetPermissionLocation) {
        BottomSheetLocationPermission(state = uiState, onAction = viewModel::onAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    uiState: LocationState,
    onAction: (LocationAction) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Location") },
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
                            onAction(LocationAction.OpenBottomSheetInput)
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
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            onAction = onAction
        )
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    uiState: LocationState,
    onAction: (LocationAction) -> Unit,

) {
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    Column(
        modifier = modifier
            .padding(start = 15.dp, end = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Get Location From GPS",
                modifier = Modifier
                    .weight(1f)
            )
            Switch(
                checked = uiState.gpsSettings,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        when {
                            locationPermissionState.status.isGranted -> {
                                onAction(LocationAction.GpsSettings(true))
                            }
                            locationPermissionState.status.shouldShowRationale -> {
                                onAction(LocationAction.ActionBottomSheetPermissionLocation(true))
                            }
                            !locationPermissionState.status.shouldShowRationale -> {
                                locationPermissionState.launchPermissionRequest()
                            }
                        }
                    } else {
                        onAction(LocationAction.GpsSettings(false))
                    }
                }
            )
        }
        Box() {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding()
            ) {
                items(uiState.locationWithWeatherList) { locationList ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(20.dp)),
                        onClick = {
                            onAction(LocationAction.OpenBottomSheetSelect(locationList))
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(15.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.width(20.dp).height(20.dp),
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                if (locationList.location.uId == uiState.selectedLocation) {
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                                .padding(5.dp),
                                            text = locationList.location.locationName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.surface
                                        )
                                    }
                                } else {
                                    Text(
                                        modifier = Modifier.padding(5.dp),
                                        text = locationList.location.locationName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                if (locationList.location.uId == uiState.selectedLocation) {
                                    Icon(
                                        modifier = Modifier.width(20.dp).height(20.dp),
                                        imageVector = Icons.Rounded.PushPin,
                                        contentDescription = null
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        onAction(LocationAction.OpenBottomSheetDelete(locationList))
                                    }
                                ) {
                                    Text(text = "Delete")
                                }
                                Spacer(Modifier.weight(1f))
                                onAction(LocationAction.GetWeatherResponse(locationList.location))
                                if (locationList.weatherResponse != null) {
                                    Text(
                                        text =  "${locationList.weatherResponse.current.temp_c}°C",
                                        fontSize = 30.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    AsyncImage(
                                        modifier = Modifier.size(50.dp),
                                        model = "https:${locationList.weatherResponse.current.condition.icon}",
                                        contentDescription = null
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.shimmer()
                                    ) {
                                        Text(
                                            text =  "???°C",
                                            fontSize = 30.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Box(
                                        modifier = Modifier.shimmer().clip(RoundedCornerShape(20.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .background(Color.LightGray)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.locationWithWeatherList.isEmpty()) {
                EmptyLocation()
            }

            if (uiState.gpsSettings) {
                GpsEnabled()
            }
        }
    }
}

@Composable
private fun GpsEnabled() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Rounded.Lock,
                contentDescription = null
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "GPS Enabled !",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun EmptyLocation() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Rounded.Warning,
                contentDescription = null
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "No Location Data Available",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetInput(
    state: LocationState,
    onAction: (LocationAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var locationName by remember {
        mutableStateOf("")
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(LocationAction.DismissBottomSheetInput)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Add Location",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = locationName.capitalizeWords(),
                onValueChange = {
                    locationName = it
                },
                label = {
                    Text(text = "City Name")
                },
                singleLine = true
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    val locationData = Location(
                        locationName = locationName
                    )
                    onAction(LocationAction.ConfirmInsertLocation(locationData))
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(LocationAction.DismissBottomSheetInput)
                    }
                }
            ) {
                Text(text = "Add")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetDelete(
    uiState: LocationState,
    onAction: (LocationAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(LocationAction.DismissBottomSheetDelete)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 0.dp),
        ) {
            Text(
                text = "Delete Location",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.width(20.dp).height(20.dp),
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            modifier = Modifier.weight(1f),
                            text = uiState.locationToDelete!!.location.locationName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.weight(1f))
                        val locationToDelete = uiState.locationToDelete!!
                        if (locationToDelete.isWeatherFetched) {
                            Text(
                                text =  "${locationToDelete.weatherResponse!!.current.temp_c}°C",
                                fontSize = 30.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            AsyncImage(
                                modifier = Modifier.size(50.dp),
                                model = "https:${locationToDelete.weatherResponse.current.condition.icon}",
                                contentDescription = null
                            )
                        } else {
                            Box(
                                modifier = Modifier.shimmer()
                            ) {
                                Text(
                                    text =  "???°C",
                                    fontSize = 30.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier.shimmer().clip(RoundedCornerShape(20.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(Color.LightGray)
                                )
                            }
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(LocationAction.ConfirmDeleteLocation)
                    }
                }
            ) {
                Text(text = "Delete")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetSelectLocation(
    uiState: LocationState,
    onAction: (LocationAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(LocationAction.DismissBottomSheetSelect)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 0.dp),
        ) {
            Text(
                text = "Use This Location",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.width(20.dp).height(20.dp),
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            modifier = Modifier.weight(1f),
                            text = uiState.locationToSelect!!.location.locationName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.weight(1f))
                        val locationToSelect = uiState.locationToSelect!!
                        if (locationToSelect.isWeatherFetched) {
                            Text(
                                text =  "${locationToSelect.weatherResponse!!.current.temp_c}°C",
                                fontSize = 30.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            AsyncImage(
                                modifier = Modifier.size(50.dp),
                                model = "https:${locationToSelect.weatherResponse.current.condition.icon}",
                                contentDescription = null
                            )
                        } else {
                            Box(
                                modifier = Modifier.shimmer()
                            ) {
                                Text(
                                    text =  "???°C",
                                    fontSize = 30.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier.shimmer().clip(RoundedCornerShape(20.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(Color.LightGray)
                                )
                            }
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(LocationAction.ConfirmSelectLocation)
                    }
                }
            ) {
                Text(text = "Select Location")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetLocationPermission(
    state: LocationState,
    onAction: (LocationAction) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(LocationAction.ActionBottomSheetPermissionLocation(false))
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 0.dp),
        ) {
            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(50.dp),
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            text = "Location permission is required to get weather data from your location by collecting your current location coordinate from GPS.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Open App Settings",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            modifier = Modifier.size(15.dp),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                        Text(
                            text = "Permission",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            modifier = Modifier.size(15.dp),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                        Text(
                            text = "Location",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            modifier = Modifier.size(15.dp),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                        Text(
                            text = "Allow Or Allow While Using The App",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                onClick = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onAction(LocationAction.ActionBottomSheetPermissionLocation(false))
                        PermissionIntent().openAppSettings(context)
                    }
                }
            ) {
                Text(text = "Open App Settings")
            }
        }
    }
}

