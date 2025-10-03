package com.weatherapp.state

data class SettingsState(
    //Base Data
    val bottomSheetNotification: Boolean = false,
    val notification: Boolean = false,

    val notificationOnBoot: Boolean = false,

    val bottomSheetDegree: Boolean = false,
    val degree: String? = null,

    val bottomSheetWind: Boolean = false,
    val wind: String? = null,

    //CountDown
    val bottomSheetCountDown: Boolean = false,
    val refreshCountDown: Int? = null,

    val bottomSheetContactDev: Boolean = false,

    val bottomSheetAboutApp: Boolean = false,

    val bottomSheetAbout: Boolean = false
)