package com.weatherapp.dataclass

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    val temp_c: Double,
    val condition: Condition,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_degree: Int,
    val wind_dir: String,
)
