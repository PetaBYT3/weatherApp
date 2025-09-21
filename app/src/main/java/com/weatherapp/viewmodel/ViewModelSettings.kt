package com.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
}