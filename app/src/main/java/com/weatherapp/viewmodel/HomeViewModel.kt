package com.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.Logger
import com.weatherapp.intent.HomeAction
import com.weatherapp.repository.WeatherRepository
import com.weatherapp.state.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: HomeAction) {
        when(action) {
            HomeAction.FetchWeather -> {
                fetchWeather()
            }
        }
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            val newWeatherData = weatherRepository.fetchWeatherData("Bandung")

            if (newWeatherData != null) {
                _uiState.update { it.copy(weatherData = newWeatherData) }
            }
            Log.d("HOME", newWeatherData.toString())
        }
    }
}