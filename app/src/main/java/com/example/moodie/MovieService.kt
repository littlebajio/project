package com.example.moodie

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MovieService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val intervalMillis = 60 * 1000L

    companion object {
        const val CHANNEL_ID = "MovieRecommendationChannel"
        const val FOREGROUND_NOTIFICATION_ID = 1
        const val RECOMMENDATION_NOTIFICATION_ID = 2
        const val MOVIE_ID_EXTRA = "movie_id_extra"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("MovieService", "Service created")
        createNotificationChannel()

        runnable = Runnable {
            sendRecommendationNotification()
            handler.postDelayed(runnable, intervalMillis)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MovieService", "Service started")

        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification())

        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, intervalMillis)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        stopForeground(STOP_FOREGROUND_REMOVE)
        Log.d("MovieService", "Service destroyed")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Рекомендации фильмов"
            val descriptionText = "Канал для показа рекомендаций фильмов"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Moodie работает")
            .setContentText("Сервис рекомендаций активен.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun sendRecommendationNotification() {
        val randomMovie = MovieData.allMovies.randomOrNull()

        if (randomMovie == null) {
            Log.w("MovieService", "Нет фильмов для рекомендации.")
            return
        }

        Log.d("MovieService", "Sending recommendation for: ${randomMovie.title}")

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MOVIE_ID_EXTRA, randomMovie.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, randomMovie.id, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Рекомендация фильма")
            .setContentText("Попробуйте посмотреть: ${randomMovie.title}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Настроение: ${randomMovie.mood}\n${randomMovie.description}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notify(RECOMMENDATION_NOTIFICATION_ID, builder.build())
            } else {
                Log.w("MovieService", "POST_NOTIFICATIONS permission not granted.")
            }
        }
    }
}
