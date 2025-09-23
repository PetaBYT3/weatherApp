package com.weatherapp.intent

sealed interface HomeAction {

    data object FetchWeather: HomeAction
}