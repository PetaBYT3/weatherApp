package com.weatherapp.dataclass

import com.weatherapp.roomdata.dataclass.Location

data class LocationWithWeather(
    val location: Location,
    val weatherResponse: WeatherResponse? = null,
    val isWeatherFetched: Boolean = false
)
