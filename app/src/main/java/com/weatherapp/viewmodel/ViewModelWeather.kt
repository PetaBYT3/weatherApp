package com.weatherapp.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.weatherapp.apiservice.WeatherApiService
import com.weatherapp.dataclass.WeatherResponse
import com.weatherapp.userdata.DataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("MissingPermission")
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelWeather(
    application: Application
): AndroidViewModel(application) {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(WeatherApiService::class.java)

    private suspend fun getWeatherData(location: String): WeatherResponse? {
        return try {
            val response = apiService.getCurrentWeather(
                apiKey = "f09eb76fc29a4f20905224821251609",
                query = location
            )
            Log.d("API_CALL", "Request berhasil untuk lokasi: $location")
            response
        } catch (e: Exception) {
            Log.e("API_CALL", "Request gagal untuk lokasi: $location, error: ${e.message}")
            null
        }
    }


    init {
        viewModelScope.launch {
            val dataStore = DataStore(application)

            dataStore.gpsSettingsFlow
                .flatMapLatest { gpsIsEnabled ->
                    // flatMapLatest akan membatalkan dan memulai ulang flow ini
                    // setiap kali gpsIsEnabled berubah.
                    if (gpsIsEnabled) {
                        // Membuat flow yang mengambil data cuaca berdasarkan GPS
                        flow {
                            var location = ""
                            LocationServices.getFusedLocationProviderClient(application).lastLocation.addOnSuccessListener {
                                location = "${it.latitude},${it.longitude}"
                            }
                            Log.e("GPS", location)
                            while (true) {
                                emit(getWeatherData(location)) // Emit hasil dari fungsi getWeatherData
                                delay(2500L)
                            }

                        }
                    } else {
                        // Membuat flow yang mengambil data cuaca untuk "Jakarta"
                        flow {
                            while (true) {
                                emit(getWeatherData("Jakarta"))
                                delay(2500L)
                            }
                        }
                    }
                }
                .collect { weatherResponse ->
                    // Ini adalah "keran"nya. Setiap data yang di-emit dari flow di atas
                    // akan diterima di sini dan digunakan untuk mengupdate state UI.
                    _weatherData.value = weatherResponse
                }
        }
    }
}