package com.weatherapp.userdata

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
    }

    val gpsSettingsFlow = context.dataStore.data.map {
        it[GPS_SETTINGS] ?: false
    }

    suspend fun setGpsSettings(gpsSettings: Boolean) {
        context.dataStore.edit {
            it[GPS_SETTINGS] = gpsSettings
        }
    }
}