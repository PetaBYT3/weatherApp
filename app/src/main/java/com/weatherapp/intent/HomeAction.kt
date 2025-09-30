package com.weatherapp.intent

import com.weatherapp.roomdata.dataclass.Location

sealed interface HomeAction {

    data object GetCoordinate: HomeAction
    data object GetLocation: HomeAction
    data object GetWeatherData: HomeAction
    data class GetWeatherDataDelay(val isCountDownStart: Boolean): HomeAction
    data class CountDownProgress(val countDownProgress: Float): HomeAction
    data class CountDownTimer(val countDownTimer: Int): HomeAction

    data class GetWeatherResponse(val locationData: Location): HomeAction


}