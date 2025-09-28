package com.weatherapp.dataclass

import android.health.connect.datatypes.units.Percentage
import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    val temp_c: Double,
    val condition: Condition,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val humidity: Int,
    val pressure_in: Double,
)
