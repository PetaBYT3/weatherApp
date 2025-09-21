package com.weatherapp.roomdata.state

import com.weatherapp.roomdata.dataclass.Location

data class LocationState(
    val location: List<Location> = emptyList(),
    val locationName: String = "",
    val isAddingLocation: Boolean = false
)
