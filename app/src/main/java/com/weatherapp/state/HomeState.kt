package com.weatherapp.state

import com.weatherapp.dataclass.WeatherResponse

data class HomeState(
    val weatherData: WeatherResponse? = null,
    val locationInput: String? = ""
)
