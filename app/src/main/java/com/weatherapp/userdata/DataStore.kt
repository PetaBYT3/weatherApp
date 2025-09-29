package com.weatherapp.userdata

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DataStore(private val context: Context) {

    companion object {
        val GPS_SETTINGS = booleanPreferencesKey("gps_settings")
        val SELECTED_LOCATION = intPreferencesKey("selected_location")
        val DEGREE = stringPreferencesKey("degree")
        val REFRESH_COUNT_DOWN = intPreferencesKey("refresh_count_down")
    }

    val gpsSettingsFlow = context.dataStore.data.map {
        it[GPS_SETTINGS] ?: false
    }

    suspend fun setGpsSettings(gpsSettings: Boolean) {
        context.dataStore.edit {
            it[GPS_SETTINGS] = gpsSettings
        }
    }

    val selectedLocation = context.dataStore.data.map {
        it[SELECTED_LOCATION] ?: 0
    }

    suspend fun setSelectedLocation(selectedLocation: Int) {
        context.dataStore.edit {
            it[SELECTED_LOCATION] = selectedLocation
        }
    }

    val degree = context.dataStore.data.map {
        it[DEGREE] ?: "Celcius"
    }

    suspend fun setDegree(degree: String) {
        context.dataStore.edit {
            it[DEGREE] = degree
        }
    }

    val refreshCountDown = context.dataStore.data.map {
        it[REFRESH_COUNT_DOWN] ?: 5
    }

    suspend fun setRefreshCountDown(refreshCountDown: Int) {
        context.dataStore.edit {
            it[REFRESH_COUNT_DOWN] = refreshCountDown
        }
    }
}