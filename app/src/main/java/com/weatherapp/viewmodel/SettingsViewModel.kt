package com.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.intent.SettingsAction
import com.weatherapp.repository.SettingsRepository
import com.weatherapp.state.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.notificationSetting.collect { notification ->
                _uiState.update { it.copy(notification = notification) }
            }
        }

        viewModelScope.launch {
            settingsRepository.degree.collect {
                _uiState.value = _uiState.value.copy(
                    degree = it
                )
            }
        }

        viewModelScope.launch {
            settingsRepository.wind.collect { wind ->
                _uiState.update { it.copy(wind = wind) }
            }
        }

        viewModelScope.launch {
            settingsRepository.refreshCountDown.collect {
                _uiState.value = _uiState.value.copy(
                    refreshCountDown = it
                )
            }
        }
    }

    fun onAction(action: SettingsAction) {
        when(action) {
            is SettingsAction.OpenNotificationBottomSheet -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(bottomSheetNotification = action.isOpen) }
                }
            }
            is SettingsAction.SetNotification -> {
                viewModelScope.launch {
                    settingsRepository.setNotificationSetting(action.notification)
                }
            }
            is SettingsAction.OpenCountDownBottomSheet -> {
                _uiState.value = _uiState.value.copy(
                    bottomSheetCountDown = action.isOpen
                )
            }
            is SettingsAction.SetCountDown -> {
                viewModelScope.launch {
                    settingsRepository.setRefreshCountDown(action.countDown)
                }
            }
            is SettingsAction.OpenDegreeBottomSheet -> {
                _uiState.value = _uiState.value.copy(
                    bottomSheetDegree = action.isOpen
                )
            }
            is SettingsAction.SetDegree -> {
                viewModelScope.launch {
                    settingsRepository.setDegree(action.degree)
                }
            }
            is SettingsAction.OpenWindBottomSheet -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(bottomSheetWind = action.isOpen)
                    }
                }
            }
            is SettingsAction.SetWind -> {
                viewModelScope.launch {
                    settingsRepository.setWind(action.wind)
                }
            }
            is SettingsAction.OpenAboutAppBottomSheet -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(bottomSheetAbout = action.isOpen)
                    }
                }
            }
            is SettingsAction.OpenContactDevBottomSheet -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(bottomSheetContactDev = action.isOpen)
                    }
                }
            }
        }
    }
}