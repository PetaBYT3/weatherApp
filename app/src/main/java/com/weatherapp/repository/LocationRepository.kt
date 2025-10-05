package com.weatherapp.repository

import android.app.Application
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.weatherapp.roomdata.dao.LocationDao
import com.weatherapp.roomdata.dataclass.Location
import com.weatherapp.userdata.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val  locationDao: LocationDao,
    private val dataStore: DataStore,
    private val application: Application
) {

    val gpsSettings = dataStore.gpsSettingsFlow

    suspend fun setGpsSettings(setGpsSettings: Boolean) {
        dataStore.setGpsSettings(setGpsSettings)
    }

    val selectedLocation = dataStore.selectedLocation

    suspend fun setSelectedLocation(selectedLocation: Int) {
        dataStore.setSelectedLocation(selectedLocation)
    }

    @RequiresPermission(allOf = ["android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"])
    suspend fun fetchCoordinate(): String? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
        return fusedLocationClient.lastLocation.await()?.let {
            "${it.latitude}, ${it.longitude}"
        }
    }

    val allLocationData = locationDao.getLocation()

    suspend fun insertLocation(location: Location) {
        locationDao.insertLocation(location)
    }

    suspend fun updateLocation(location: List<Location>) {
        locationDao.updateLocation(location)
    }

    suspend fun deleteLocation(location: Location) {
        locationDao.deleteLocation(location)
    }

    fun fetchLocationByUid(uId: Int): Flow<Location?> {
        return locationDao.getLocationByUid(uId)
    }
}