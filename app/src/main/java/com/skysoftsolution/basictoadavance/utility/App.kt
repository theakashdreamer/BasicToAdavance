package com.skysoftsolution.basictoadavance.utility
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.util.Date
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
class App : Application() {

    companion object {
        private val TAG = App::class.java.simpleName
        const val CHANNEL_ID = "Channel_ID1"
        const val CHANNEL_NAME = "Notification"
        const val CHANNEL_DESCRIPTION = "this channel for notification notification "
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            // Register the channel with the system
            val manager = getSystemService(NotificationManager::class.java)
            if (manager != null) {
                manager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created: $CHANNEL_NAME")
            } else {
                Log.e(TAG, "Notification Manager is null")
            }
        }
    }
}

