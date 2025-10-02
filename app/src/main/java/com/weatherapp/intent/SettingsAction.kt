package com.weatherapp.intent

sealed interface SettingsAction {

    //Notification
    data class SetNotification(val notification: Boolean) : SettingsAction
    //Degree
    data class OpenDegreeBottomSheet(val isOpen: Boolean) : SettingsAction
    data class SetDegree(val degree: String) : SettingsAction
    //Wind
    data class OpenWindBottomSheet(val isOpen: Boolean) : SettingsAction
    data class SetWind(val wind: String) : SettingsAction

    //Refresh Weather Count Down
    data class OpenCountDownBottomSheet(val isOpen: Boolean) : SettingsAction
    data class SetCountDown(val countDown: Int) : SettingsAction

    data class OpenAboutBottomSheet(val isOpen: Boolean) : SettingsAction
}