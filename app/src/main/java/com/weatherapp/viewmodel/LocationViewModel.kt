package com.weatherapp.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.weatherapp.application.Application
import com.weatherapp.roomdata.dao.LocationDao
import com.weatherapp.roomdata.database.LocationDatabase
import com.weatherapp.roomdata.dataclass.Location
import com.weatherapp.roomdata.event.LocationEvent
import com.weatherapp.roomdata.state.LocationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationViewModel(
    application: Application
): AndroidViewModel(application) {

    private val locationDao = LocationDatabase.getDatabase(application).locationDao()
    val allLocation = locationDao.getLocation()

    fun addLocation(location: Location) {
        viewModelScope.launch {
            locationDao.insertLocation(location)
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            locationDao.deleteLocation(location)
        }
    }
}