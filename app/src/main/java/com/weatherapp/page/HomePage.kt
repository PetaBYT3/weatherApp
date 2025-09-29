package com.weatherapp.page

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WindPower
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.valentinilk.shimmer.shimmer
import com.weatherapp.intent.HomeAction
import com.weatherapp.state.HomeState
import com.weatherapp.utilities.CustomProgressBar
import com.weatherapp.utilities.VerticalProgressBar
import com.weatherapp.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomePageCore(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomePage(uiState = uiState, onAction = viewModel::onAction)
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HomePage(
    uiState: HomeState,
    onAction: (HomeAction) -> Unit
    ) {
    val permissionState = rememberPermissionState(
        permission = ACCESS_FINE_LOCATION
    )
    val scrollState = rememberScrollState()

    LaunchedEffect(permissionState) {
        if (permissionState.status.isGranted) {
            onAction(HomeAction.GetCoordinate)
        }
    }

    onAction(HomeAction.GetLocation)

    LaunchedEffect(Unit) {
        while (true) {
            onAction(HomeAction.GetWeatherData)
            onAction(HomeAction.GetWeatherDataDelay(true))
            delay(5000)
            onAction(HomeAction.GetWeatherDataDelay(false))
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState, true)
            .padding(horizontal = 15.dp, vertical = 0.dp)
    ) {
        Card(
            modifier = Modifier.clip(RoundedCornerShape(50)),
        ) {
            Text(
                modifier = Modifier.padding(15.dp),
                text = "Current Location",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Row(
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
                HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                CustomProgressBar(uiState = uiState, onAction = onAction)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (uiState.weatherData != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                ) {
                    when (uiState.degreeFormat) {
                        "Celcius" -> Text(
                            modifier = Modifier.padding(15.dp),
                            text = "${uiState.weatherData.current.temp_c}°C",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        "Fahrenheit" -> Text(
                            modifier = Modifier.padding(15.dp),
                            text = "${uiState.weatherData.current.temp_f}°F",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
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
                                text = "Location",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
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
                                imageVector = Icons.Rounded.Cloud,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Condition",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = uiState.weatherData.current.condition.text,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            AsyncImage(
                                modifier = Modifier.size(50.dp),
                                model = "https:${uiState.weatherData.current.condition.icon}",
                                contentDescription = null
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(15.dp),
                    ) {
                        Row(
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
                        HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                        Text(
                            text = "${uiState.weatherData.current.wind_mph} MPH",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${uiState.weatherData.current.wind_kph} KPH",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
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
                            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
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
                                modifier = Modifier.padding(5.dp),
                                text = locationList.location.locationName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))
                        onAction(HomeAction.GetWeatherResponse(locationList.location))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
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
        } else {
            ShimmerPlaceHolder()
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}