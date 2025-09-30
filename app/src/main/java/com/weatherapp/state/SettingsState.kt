package com.weatherapp.state

data class SettingsState(
    //Base Data
    val bottomSheetDegree: Boolean = false,
    val degree: String? = null,
    val bottomSheetWind: Boolean = false,
    val wind: String? = null,

    //CountDown
    val bottomSheetCountDown: Boolean = false,
    val refreshCountDown: Int? = null,

    val bottomSheetAbout: Boolean = false
)