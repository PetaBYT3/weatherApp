package com.weatherapp.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.weatherapp.apiservice.WeatherApiService
import com.weatherapp.repository.LocationRepository
import com.weatherapp.repository.SettingsRepository
import com.weatherapp.repository.WeatherRepository
import com.weatherapp.roomdata.dao.LocationDao
import com.weatherapp.roomdata.database.LocationDatabase
import com.weatherapp.userdata.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

// --- Resep untuk WeatherRepository ---

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit { // 1. Ajari Hilt cara membuat Retrofit
        return Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/") // Ganti dengan URL base Anda
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService { // 2. Ajari cara membuat ApiService
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: WeatherApiService): WeatherRepository { // 3. Sekarang Hilt tahu cara membuat WeatherRepository
        return WeatherRepository(apiService)
    }

    // --- Resep untuk Settings & Location Repository ---

    @Provides
    @Singleton
    fun provideSettingsRepository(dataStore: DataStore): SettingsRepository {
        return SettingsRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        locationDao: LocationDao,
        dataStore: DataStore,
        application: Application // <-- Minta Application langsung
    ): LocationRepository {
        return LocationRepository(locationDao, dataStore, application)
    }

    // --- Resep untuk Local Database ---

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore {
        return DataStore(context)
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: LocationDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideLocationDatabase(@ApplicationContext context: Context): LocationDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocationDatabase::class.java,
            "location_database"
        ).build()
    }
}