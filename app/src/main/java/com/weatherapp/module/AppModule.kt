package com.weatherapp.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.weatherapp.repository.LocationRepository
import com.weatherapp.repository.WeatherRepository
import com.weatherapp.roomdata.dao.LocationDao
import com.weatherapp.roomdata.database.LocationDatabase
import com.weatherapp.userdata.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(): WeatherRepository {
        return WeatherRepository()
    }

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

    @Provides
    @Singleton
    fun provideLocationRepository(
        locationDao: LocationDao,
        dataStore: DataStore,
        @ApplicationContext context: Context
    ): LocationRepository {
        return LocationRepository(locationDao, dataStore, context.applicationContext as Application)
    }
}