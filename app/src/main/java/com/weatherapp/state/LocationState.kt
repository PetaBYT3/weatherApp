package com.weatherapp.state

import com.weatherapp.roomdata.dataclass.Location

data class LocationState(
    val locationData: List<Location> = emptyList(),
    val gpsSettings: Boolean = false
)
