package com.weatherapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.apiservice.WeatherApiService
import com.weatherapp.dataclass.WeatherResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewModelWeather: ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(WeatherApiService::class.java)

    fun getWeatherData(location: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getCurrentWeather(
                    apiKey = "f09eb76fc29a4f20905224821251609",
                    query = location
                )
                _weatherData.value = response
                Log.d("API_CALL", "Request berhasil, data: $response")
            } catch (e: Exception) {
                Log.e("API_CALL", "Request gagal, error: ${e.message}")
            }
        }
    }

    fun getWeatherDataAlways(location: String) {
        viewModelScope.launch {
            while (true) {
                getWeatherData(location)
                delay(2500L)
            }
        }
    }
}