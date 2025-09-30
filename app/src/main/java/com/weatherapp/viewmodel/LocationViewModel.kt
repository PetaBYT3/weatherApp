package com.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.dataclass.LocationWithWeather
import com.weatherapp.intent.LocationAction
import com.weatherapp.repository.LocationRepository
import com.weatherapp.repository.SettingsRepository
import com.weatherapp.repository.WeatherRepository
import com.weatherapp.roomdata.dataclass.Location
import com.weatherapp.state.LocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository
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
            locationRepository.selectedLocation.collect { selectedLocation ->
                _uiState.update { it.copy(selectedLocation = selectedLocation) }
            }
        }

        viewModelScope.launch {
            locationRepository.allLocationData.collect { locationData ->
                val locationList = locationData.map {
                    LocationWithWeather(
                        location = it,
                        weatherResponse = null,
                        isWeatherFetched = false
                    )
                }
                _uiState.update { it.copy(locationWithWeatherList = locationList) }
            }
        }

        viewModelScope.launch {
            settingsRepository.degree.collect { degree ->
                _uiState.update { it.copy(degreeFormat = degree) }
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
            is LocationAction.OpenBottomSheetInput -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(
                        bottomSheetInsert = true
                    ) }
                }
            }
            LocationAction.DismissBottomSheetInput -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(
                        bottomSheetInsert = false
                    ) }
                }

            }
            is LocationAction.ConfirmInsertLocation -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            locationToInsert = action.insertLocation,
                            bottomSheetInsert = false
                        )
                    }
                    _uiState.value.locationToInsert?.let {
                        locationRepository.insertLocation(it)
                    }
                }
                Log.d("locationData", action.insertLocation.toString())

            }
            is LocationAction.OpenBottomSheetDelete -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(
                        locationToDelete = action.deleteLocation,
                        bottomSheetDelete = true
                    ) }
                }
            }
            LocationAction.DismissBottomSheetDelete -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(
                        locationToDelete = null,
                        bottomSheetDelete = false
                    ) }
                }
            }
            LocationAction.ConfirmDeleteLocation -> {
                viewModelScope.launch {
                    _uiState.value.locationToDelete!!.location.let {
                        val locationData = Location(
                            uId = it.uId,
                            locationName = it.locationName,
                            isSelected = it.isSelected
                        )
                        locationRepository.deleteLocation(locationData)
                    }
                    _uiState.update {
                        it.copy(
                            locationToDelete = null,
                            bottomSheetDelete = false
                        )
                    }
                }
            }
            is LocationAction.OpenBottomSheetSelect -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            locationToSelect = action.selectLocation,
                            bottomSheetSelect = true
                        )
                    }
                }
            }
            LocationAction.DismissBottomSheetSelect -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            locationToSelect = null,
                            bottomSheetSelect = false
                        )
                    }
                }
            }
            LocationAction.ConfirmSelectLocation -> {
                viewModelScope.launch {
                    val selectedLocationUid = _uiState.value.locationToSelect?.location?.uId
                    if (selectedLocationUid != null) {
                        locationRepository.setSelectedLocation(selectedLocationUid)
                    }

                    _uiState.update{
                        it.copy(
                            locationToSelect = null,
                            bottomSheetSelect = false
                        )
                    }
                }
            }
            is LocationAction.GetWeatherResponse -> {
                viewModelScope.launch {
                    val weatherResponse = weatherRepository.fetchWeatherData(action.locationData.locationName)
                    val currentList = _uiState.value.locationWithWeatherList
                    val updatedList = currentList.map { locationWithWeather ->
                        if (locationWithWeather.location.uId == action.locationData.uId) {
                            locationWithWeather.copy(weatherResponse = weatherResponse, isWeatherFetched = true)
                        } else {
                            locationWithWeather
                        }
                    }
                    _uiState.update {
                        it.copy(locationWithWeatherList = updatedList)
                    }
                }
            }
            is LocationAction.ActionBottomSheetPermissionLocation -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            bottomSheetPermissionLocation = action.isBottomSheetOpen
                        )
                    }
                }
            }
        }
    }
}