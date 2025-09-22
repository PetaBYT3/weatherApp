package com.weatherapp.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.application.Application
import com.weatherapp.roomdata.database.LocationDatabase
import com.weatherapp.roomdata.dataclass.Location
import kotlinx.coroutines.launch

class LocationViewModel(
    application: Application
): AndroidViewModel(application) {

    private val locationDao = LocationDatabase.getDatabase(application).locationDao()
    val allLocation = locationDao.getLocation()

    fun insertLocation(location: Location) {
        viewModelScope.launch {
            locationDao.insertLocation(location)
        }
    }

    fun updateLocation(location: Location) {
        viewModelScope.launch {
            locationDao.updateLocation(location)
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            locationDao.deleteLocation(location)
        }
    }
}