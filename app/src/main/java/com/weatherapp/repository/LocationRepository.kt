package com.weatherapp.repository

import com.weatherapp.roomdata.dao.LocationDao
import com.weatherapp.roomdata.dataclass.Location
import com.weatherapp.userdata.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val  locationDao: LocationDao,
    private val dataStore: DataStore
) {

    val gpsSettings = dataStore.gpsSettingsFlow

    suspend fun setGpsSettings(setGpsSettings: Boolean) {
        dataStore.setGpsSettings(setGpsSettings)
    }

    val allLocationData = locationDao.getLocation()

    suspend fun insertLocation(location: Location) {
        locationDao.insertLocation(location)
    }

    suspend fun updateLocation(location: Location) {
        locationDao.updateLocation(location)
    }

    suspend fun deleteLocation(location: Location) {
        locationDao.updateLocation(location)
    }
}