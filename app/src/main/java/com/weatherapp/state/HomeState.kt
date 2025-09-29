package com.weatherapp.state

import com.weatherapp.dataclass.LocationWithWeather
import com.weatherapp.dataclass.WeatherResponse

data class HomeState(
    val refreshWeatherCountDown: Int? = null,

    val weatherData: WeatherResponse? = null,
    val degreeFormat: String? = null,
    val selectedLocationUid: Int? = null,
    val locationToFetch: String? = null,
    val coordinateToFetch: String? = null,
    val gpsSettings: Boolean = false,

    val locationWithWeatherList: List<LocationWithWeather> = emptyList(),
    val isCountDownStart: Boolean? = false,
    val countDownProgress: Float? = 0f
)
