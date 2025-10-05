package com.weatherapp.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.weatherapp.worker.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class Application: Application(), Configuration.Provider, LifecycleObserver {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val workerName = "WeatherNotificationWork"

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val observer = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    startWeatherWorker()
                }
                Lifecycle.Event.ON_START -> {
                    cancelWeatherWorker()
                }
                else -> {}
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
    }

    private fun startWeatherWorker() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            workerName,
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun cancelWeatherWorker() {
        WorkManager.getInstance(this).cancelUniqueWork(workerName)
    }
}