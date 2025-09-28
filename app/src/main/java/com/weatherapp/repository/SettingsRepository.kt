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

    val refreshCountDown = dataStore.refreshCountDown

    suspend fun setRefreshCountDown(newRefreshCountDown: Int) {
        dataStore.setRefreshCountDown(newRefreshCountDown)
    }

}