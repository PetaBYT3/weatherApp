package com.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.intent.LocationAction
import com.weatherapp.repository.LocationRepository
import com.weatherapp.state.LocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(LocationState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepository.gpsSettings.collect { gpsSettings ->
                _uiState.update { it.copy(gpsSettings = gpsSettings ) }
            }
        }

        viewModelScope.launch {
            locationRepository.allLocationData.collect { locationData ->
                _uiState.update { it.copy(locationData = locationData) }
                Log.d("locationData", locationData.toString())
            }
        }
    }

    fun onAction(action: LocationAction) {
        when(action) {
            is LocationAction.GpsSettings -> {
                viewModelScope.launch {
                    locationRepository.setGpsSettings(action.newGpsSettings)
                }
            }

            is LocationAction.InsertLocation -> {
                viewModelScope.launch {
                    locationRepository.insertLocation(action.newInsertLocation)
                }
            }

            is LocationAction.DeleteLocation -> {
                viewModelScope.launch {
                    locationRepository.deleteLocation(action.newDeleteLocation)
                }
            }
        }
    }
}