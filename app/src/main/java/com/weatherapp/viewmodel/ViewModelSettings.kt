package com.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.roomdata.database.LocationDatabase
import com.weatherapp.roomdata.dataclass.Location
import com.weatherapp.userdata.DataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ViewModelSettings(
    application: Application
): AndroidViewModel(application) {

    private val dataStore = DataStore(application)
    val gpsSettings = dataStore.gpsSettingsFlow

    fun setGpsPrefs(setGpsSettings: Boolean) {
        viewModelScope.launch {
            dataStore.setGpsSettings(setGpsSettings)
        }
    }

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