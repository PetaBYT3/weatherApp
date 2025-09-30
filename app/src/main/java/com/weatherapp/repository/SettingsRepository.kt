package com.weatherapp.repository

import com.weatherapp.userdata.DataStore
import jakarta.inject.Inject

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore
) {

    val degree = dataStore.degree

    suspend fun setDegree(newDegree: String) {
        dataStore.setDegree(newDegree)
    }

    val wind = dataStore.wind

    suspend fun setWind(newWind: String) {
        dataStore.setWind(newWind)
    }

    val refreshCountDown = dataStore.refreshCountDown

    suspend fun setRefreshCountDown(newRefreshCountDown: Int) {
        dataStore.setRefreshCountDown(newRefreshCountDown)
    }

}