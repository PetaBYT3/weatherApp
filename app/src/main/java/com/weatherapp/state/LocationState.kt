package com.weatherapp.state

import com.weatherapp.dataclass.LocationWithWeather
import com.weatherapp.dataclass.WeatherResponse
import com.weatherapp.roomdata.dataclass.Location

data class LocationState(
    val locationList: List<Location> = emptyList(),
    val gpsSettings: Boolean = false,

    //List
    val locationWithWeatherList: List<LocationWithWeather> = emptyList(),
    val degreeFormat: String? = null,

    //Selected Location
    val selectedLocation: Int? = null,

    //Insert
    val bottomSheetInsert: Boolean = false,
    val locationToInsert: Location? = null,

    //Delete
    val bottomSheetDelete: Boolean = false,
    val locationToDelete: LocationWithWeather? = null,

    //Select Location
    val bottomSheetSelect: Boolean = false,
    val locationToSelect: LocationWithWeather? = null,

    //Permission
    val bottomSheetPermissionLocation: Boolean = false,
)
