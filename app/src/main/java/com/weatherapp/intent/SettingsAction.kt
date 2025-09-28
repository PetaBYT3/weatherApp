package com.weatherapp.intent

sealed interface SettingsAction {

    //Degree
    data class OpenDegreeBottomSheet(val isOpen: Boolean) : SettingsAction
    data class SetDegree(val degree: String) : SettingsAction

    //Refresh Weather Count Down
    data class OpenCountDownBottomSheet(val isOpen: Boolean) : SettingsAction
    data class SetCountDown(val countDown: Int) : SettingsAction
}