package com.weatherapp.repository

import android.util.Log
import com.weatherapp.apiservice.WeatherApiService
import com.weatherapp.dataclass.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(WeatherApiService::class.java)

    suspend fun fetchWeatherData(location: String): WeatherResponse? {
        return try {
            val response = apiService.getCurrentWeather(
                apiKey = "f09eb76fc29a4f20905224821251609",
                query = location
            )
            response
        } catch (e: Exception) {
            null
        }
    }
}