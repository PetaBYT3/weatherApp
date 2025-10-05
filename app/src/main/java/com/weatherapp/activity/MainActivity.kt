package com.weatherapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WindPower
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.valentinilk.shimmer.shimmer
import com.weatherapp.R
import com.weatherapp.intent.HomeAction
import com.weatherapp.intent.SettingsAction
import com.weatherapp.ui.theme.WeatherAppTheme
import com.weatherapp.utilities.ShimmerPlaceHolder
import com.weatherapp.state.HomeState
import com.weatherapp.utilities.CustomProgressBar
import com.weatherapp.utilities.TextContent
import com.weatherapp.utilities.TextTitle
import com.weatherapp.utilities.VerticalProgressBar
import com.weatherapp.viewmodel.HomeViewModel
import com.weatherapp.worker.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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

@Composable
private fun MainScreenCore(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MainScreen(uiState = uiState, onAction = viewModel::onAction)

    if (uiState.bottomSheetGemini) {
        BottomSheetGemini(uiState = uiState, onAction = viewModel::onAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    uiState: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Home") },
                actions = {
                    IconButton(
                        onClick = {
                            val intentEdit = Intent(context, LocationActivity::class.java)
                            context.startActivity(intentEdit)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                        )
                    }
                    IconButton(
                        onClick = {
                            val intentSettings = Intent(context, SettingsActivity::class.java)
                            context.startActivity(intentSettings)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
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
    uiState: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val scrollState = rememberScrollState()
    val locationPermission = rememberPermissionState(
        permission = android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    onAction(HomeAction.GetLocation)

    LaunchedEffect(locationPermission, uiState.gpsSettings) {
        if (uiState.gpsSettings && locationPermission.status.isGranted) {
            onAction(HomeAction.GetCoordinate)
        }
    }

    LaunchedEffect(uiState.refreshWeatherCountDown) {
        if (uiState.refreshWeatherCountDown != null) {
            val countDown = uiState.refreshWeatherCountDown.times(1000)
            while (true) {
                onAction(HomeAction.GetWeatherData)
                onAction(HomeAction.GetWeatherDataDelay(true))
                delay(countDown.toLong())
                onAction(HomeAction.GetWeatherDataDelay(false))
                delay(5000)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState, true)
            .padding(start = 15.dp, end = 15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Text(text = uiState.test.toString())
            Row {
                Card(
                    modifier = Modifier.clip(RoundedCornerShape(50)),
                ) {
                    Text(
                        modifier = Modifier.padding(15.dp),
                        text = "Used Location",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                if (uiState.gpsSettings) {
                    Card(
                        modifier = Modifier.clip(RoundedCornerShape(50)),
                    ) {
                        Text(
                            modifier = Modifier.padding(15.dp),
                            text = "From GPS",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.clip(RoundedCornerShape(50)),
                    ) {
                        Text(
                            modifier = Modifier.padding(15.dp),
                            text = "From Location List",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).animateContentSize(),
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.width(20.dp).height(20.dp),
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Requesting Latest Weather Data",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row {
                        CustomProgressBar(uiState = uiState, onAction = onAction)
                        Spacer(modifier = Modifier.width(10.dp))
                        if (uiState.isCountDownStart == false) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(15.dp),
                            )
                        }
                    }
                    if (uiState.isCountDownStart == false) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp).shimmer(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextContent("Fetching Weather Data")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (uiState.locationToFetch != null || uiState.coordinateToFetch != null) {
                if (uiState.weatherData != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(15.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(bottom = 15.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier.width(20.dp).height(20.dp),
                                        imageVector = Icons.Rounded.LocationOn,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Location",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Text(
                                    text = uiState.weatherData.location.country,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = uiState.weatherData.location.region,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = uiState.weatherData.location.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                        ) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(15.dp).fillMaxSize()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(bottom = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier.width(20.dp).height(20.dp),
                                            imageVector = Icons.Rounded.AcUnit,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Degree",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        when (uiState.degreeFormat) {
                                            "Celcius" -> Text(
                                                text = "${uiState.weatherData.current.temp_c}°C",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontSize = 35.sp
                                            )
                                            "Fahrenheit" -> Text(
                                                text = "${uiState.weatherData.current.temp_f}°F",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontSize = 35.sp
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Card(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(15.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(bottom = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier.width(20.dp).height(20.dp),
                                            imageVector = Icons.Rounded.WindPower,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Wind",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    when (uiState.windFormat) {
                                        "MPH" -> Text(
                                            text = "${uiState.weatherData.current.wind_mph} MPH",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        "KPH" -> Text(
                                            text = "${uiState.weatherData.current.wind_kph} KPH",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Text(
                                        text = "${uiState.weatherData.current.wind_degree}°",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = uiState.weatherData.current.wind_dir,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(15.dp).height(IntrinsicSize.Min),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxHeight().weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier.width(20.dp).height(20.dp),
                                            imageVector = Icons.Rounded.Cloud,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Condition",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = uiState.weatherData.current.condition.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                AsyncImage(
                                    modifier = Modifier.size(100.dp),
                                    model = "https:${uiState.weatherData.current.condition.icon}",
                                    contentDescription = null
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Card(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(15.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(bottom = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier.width(20.dp).height(20.dp),
                                            imageVector = Icons.Rounded.Compress,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Pressure",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    Text(
                                        text = "${uiState.weatherData.current.pressure_in} inHg",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Card(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(15.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(bottom = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier.width(20.dp).height(20.dp),
                                            imageVector = Icons.Rounded.WaterDrop,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Humidity",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {
                                        Text(
                                            text = "${uiState.weatherData.current.humidity}%",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        VerticalProgressBar(uiState = uiState, onAction = onAction)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    ShimmerPlaceHolder()
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .animateContentSize()
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(15.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextContent("Cant get weather data because no location selected. Please enable GPS or select location from your list")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onAction(HomeAction.OpenBottomSheetGemini(true))
            },
            enabled = uiState.weatherData != null
        ) {
            Text(text = "Summarize With Gemini AI")
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                painter = painterResource(R.drawable.gemini),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.clip(RoundedCornerShape(50)),
        ) {
            Text(
                modifier = Modifier.padding(15.dp),
                text = "Your Location List",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (uiState.locationWithWeatherList.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
            ) {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    initialPageOffsetFraction = 0f,
                    pageCount = { uiState.locationWithWeatherList.size },
                )
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(5000L)
                        val nextPage = (pagerState.currentPage + 1) % (pagerState.pageCount)
                        pagerState.animateScrollToPage(
                            page = nextPage,
                        )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    key = { uiState.locationWithWeatherList[it].location.uId },
                ) { index ->
                    val locationList = uiState.locationWithWeatherList[index]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(15.dp).height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
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
                                modifier = Modifier.padding(5.dp),
                                text = locationList.location.locationName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        onAction(HomeAction.GetWeatherResponse(locationList.location))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            if (locationList.weatherResponse != null) {
                                when (uiState.degreeFormat) {
                                    "Celcius" ->
                                        Text(
                                            text = "${locationList.weatherResponse.current.temp_c}°C",
                                            fontSize = 30.sp
                                        )

                                    "Fahrenheit" ->
                                        Text(
                                            text = "${locationList.weatherResponse.current.temp_f}°F",
                                            fontSize = 30.sp
                                        )
                                }
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
                                        text = "???°C",
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
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TextContent(text = "Your Location List Is Empty")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetGemini(
    uiState: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onAction(HomeAction.OpenBottomSheetGemini(false))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 0.dp)
                .animateContentSize()
        ) {
            Text(
                text = "Gemini AI",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            TextTitle(text = "Response")
            Spacer(Modifier.height(10.dp))
            Box() {
                if (uiState.initialBottomSheetGemini) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .animateContentSize()
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(15.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextContent("Response Will Appear Here")
                        }
                    }
                } else {
                    if (uiState.geminiResponse != null) {
                        val scrollState = rememberScrollState()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .clip(RoundedCornerShape(20.dp))
                                .verticalScroll(scrollState, true),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(15.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TextContent(uiState.geminiResponse)
                            }
                        }
                    } else {
                        ShimmerPlaceHolder()
                    }
                }
            }
                Spacer(Modifier.height(20.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(HomeAction.GetGeminiResponse)
                    onAction(HomeAction.InitialBottomSheetGemini(false))
                }
            ) {
                Text(text = "Generate Summarize Response")
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}