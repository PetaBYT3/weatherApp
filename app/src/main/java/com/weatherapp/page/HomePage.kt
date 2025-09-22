package com.weatherapp.page

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.weatherapp.viewmodel.ViewModelSettings
import com.weatherapp.viewmodel.ViewModelWeather

@Composable
fun HomePage(
    vmWeather: ViewModelWeather = viewModel(),
    vmSettings: ViewModelSettings = viewModel()
    ) {

    val weatherData by vmWeather.weatherData.collectAsStateWithLifecycle()
    val gpsSettings by vmSettings.gpsSettings.collectAsStateWithLifecycle(false)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState, true)
            .padding(horizontal = 15.dp, vertical = 0.dp)
    ) {
        if (weatherData != null) {
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "${weatherData!!.current.temp_c}°",
                fontSize = 125.sp,
            )
            Spacer(modifier = Modifier.height(25.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Location",
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = weatherData!!.location.country)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = weatherData!!.location.region)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = weatherData!!.location.name)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Condition",
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = weatherData!!.current.condition.text)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Wind",
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "${weatherData!!.current.wind_mph} MPH")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${weatherData!!.current.wind_kph} KPH")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${weatherData!!.current.wind_degree}°")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = weatherData!!.current.wind_dir)
            }
        }
    }
}