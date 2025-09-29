package com.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.dataclass.LocationWithWeather
import com.weatherapp.intent.HomeAction
import com.weatherapp.repository.LocationRepository
import com.weatherapp.repository.SettingsRepository
import com.weatherapp.repository.WeatherRepository
import com.weatherapp.roomdata.dataclass.Location
import com.weatherapp.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.refreshCountDown.collect { countDown ->
                _uiState.update { it.copy(refreshWeatherCountDown = countDown) }
            }
        }

        viewModelScope.launch {
            locationRepository.selectedLocation.collect { selectedLocation ->
                _uiState.update { it.copy(selectedLocationUid = selectedLocation) }
            }
        }

        viewModelScope.launch {
            locationRepository.gpsSettings.collect { gpsSettings ->
                _uiState.update { it.copy(gpsSettings = gpsSettings) }
            }
        }

        viewModelScope.launch {
            locationRepository.allLocationData.collect { locationData ->
                val locationList = locationData.map {
                    LocationWithWeather(
                        location = it,
                        weatherResponse = null,
                        isWeatherFetched = false
                    )
                }
                _uiState.update { it.copy(locationWithWeatherList = locationList) }
            }
        }

        viewModelScope.launch {
            settingsRepository.degree.collect { degree ->
                _uiState.update { it.copy(degreeFormat = degree) }
            }
        }
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.GetCoordinate -> {
                viewModelScope.launch {
                    val newCoordinate = locationRepository.fetchCoordinate()
                    _uiState.update { it.copy(coordinateToFetch = newCoordinate) }
                }
            }
            HomeAction.GetLocation -> {
                viewModelScope.launch {
                    val locationUid = uiState.value.selectedLocationUid
                    if (locationUid != null) {
                        val locationByUid = locationRepository.fetchLocationByUid(locationUid).filterNotNull().first()
                        _uiState.update { it.copy(locationToFetch = locationByUid.locationName) }
                    }
                }
            }
            HomeAction.GetWeatherData -> {
                viewModelScope.launch {
                    val gpsSettings = _uiState.value.gpsSettings
                    val locationToFetch = _uiState.value.locationToFetch
                    val coordinateToFetch = _uiState.value.coordinateToFetch

                    if (gpsSettings) {
                        if (coordinateToFetch != null) {
                            val weatherResponse = weatherRepository.fetchWeatherData(coordinateToFetch)
                            if (weatherResponse != null) {
                                _uiState.update { it.copy(weatherData = weatherResponse) }
                            }
                        }
                    } else {
                        if (locationToFetch != null) {
                            val weatherResponse = weatherRepository.fetchWeatherData(locationToFetch)
                            if (weatherResponse != null) {
                                _uiState.update { it.copy(weatherData = weatherResponse) }
                            }
                        }
                    }
                }
            }
            is HomeAction.GetWeatherDataDelay -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isCountDownStart = action.isCountDownStart) }
                }
            }
            is HomeAction.CountDownProgress -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(countDownProgress = action.countDownProgress) }
                }
            }
            is HomeAction.GetWeatherResponse -> {
                viewModelScope.launch {
                    val weatherResponse = weatherRepository.fetchWeatherData(action.locationData.locationName)
                    val currentList = _uiState.value.locationWithWeatherList
                    val updatedList = currentList.map { locationWithWeather ->
                        if (locationWithWeather.location.uId == action.locationData.uId) {
                            locationWithWeather.copy(weatherResponse = weatherResponse, isWeatherFetched = true)
                        }
                        else {
                            locationWithWeather
                        }
                    }
                    _uiState.update {
                        it.copy(locationWithWeatherList = updatedList)
                    }
                }
            }
        }
    }
}