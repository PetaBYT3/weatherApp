package com.weatherapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Numbers
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weatherapp.R
import com.weatherapp.dataclass.WeatherResponse
import com.weatherapp.repository.LocationRepository
import com.weatherapp.repository.SettingsRepository
import com.weatherapp.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
): CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerProviderEntryPoint {
        fun weatherRepository(): WeatherRepository
        fun settingsRepository(): SettingsRepository
    }

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            WorkerProviderEntryPoint::class.java
        )

        val temperatureFormat = entryPoint.settingsRepository().degree.first()
        val lastLocation = entryPoint.settingsRepository().lastLocation.first()
        val weatherResponse = entryPoint.weatherRepository().fetchWeatherData(lastLocation)

        if (lastLocation.isNotBlank()) {
            if (weatherResponse != null) {
                when (temperatureFormat) {
                    "Celcius" -> {
                        showNotification(
                            location = "${weatherResponse.location.region} | ${weatherResponse.location.name}",
                            condition = weatherResponse.current.condition.text,
                            temperature = "${weatherResponse.current.temp_c}°C",
                        )
                    }
                    "Fahrenheit" -> {
                        showNotification(
                            location = "${weatherResponse.location.region} | ${weatherResponse.location.name}",
                            condition = weatherResponse.current.condition.text,
                            temperature = "${weatherResponse.current.temp_f}°F"
                        )
                    }

                }
            }
        } else {
            showNotification(
                location = "Cant Get Weather Data !",
                condition = "Location not available. Choose location or enable GPS on app",
                temperature = ""
            )
        }
        return Result.success()
    }

    private fun showNotification(
        location: String,
        condition: String,
        temperature: String,
    ) {
        val channelId = "notification_channel_id"
        val notificationId = 1

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(location)
            .setContentText("$temperature | $condition")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(appContext)) {
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }
}