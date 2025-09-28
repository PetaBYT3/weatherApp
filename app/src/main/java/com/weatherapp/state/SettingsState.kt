package com.weatherapp.state

data class SettingsState(
    //Base Data
    val bottomSheetDegree: Boolean = false,
    val degree: String? = null,

    //CountDown
    val bottomSheetCountDown: Boolean = false,
    val refreshCountDown: Int? = null,
)