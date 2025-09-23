package com.weatherapp.page

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.intent.HomeAction
import com.weatherapp.viewmodel.HomeViewModel

@Composable
fun HomePage(
//    vmWeather: ViewModelWeather = viewModel(),
//    vmSettings: ViewModelSettings = viewModel(),
    viewModel: HomeViewModel = hiltViewModel()
    ) {
//    val weatherData by vmWeather.weatherData.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.onAction(HomeAction.FetchWeather)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState, true)
            .padding(horizontal = 15.dp, vertical = 0.dp)
    ) {
        if (state.weatherData != null) {
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "${state.weatherData!!.current.temp_c}°",
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
                Text(text = state.weatherData!!.location.country)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = state.weatherData!!.location.region)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = state.weatherData!!.location.name)
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
                Text(text = state.weatherData!!.current.condition.text)
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
                Text(text = "${state.weatherData!!.current.wind_mph} MPH")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${state.weatherData!!.current.wind_kph} KPH")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${state.weatherData!!.current.wind_degree}°")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = state.weatherData!!.current.wind_dir)
            }
        }
    }
}