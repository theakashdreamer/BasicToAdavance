package com.skysoftsolution.basictoadavance.goalModule.reciever

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.eventManager.EventManageMentActivity

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "EVENT_REMINDER_CHANNEL"
        const val CHANNEL_NAME = "Event Reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = createNotificationChannel(context)

        when (intent.action) {
            "com.skysoftsolution.EVENT_REMINDER" -> {
                showEventNotification(context, intent, notificationManager)
            }
        }
    }

    private fun showEventNotification(context: Context, intent: Intent, notificationManager: NotificationManager) {
        val eventTitle = intent.getStringExtra("title") ?: "Event Reminder"
        val speaker = intent.getStringExtra("speaker") ?: "Speaker"
        val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())

        val notifyIntent = Intent(context, EventManageMentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            timestamp.toInt(),  // Use unique ID
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.no_event_shedule)
            .setContentTitle(eventTitle)
            .setContentText("Speaker: $speaker")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Speaker: $speaker"))
            .setColor(ContextCompat.getColor(context, R.color.colorSecondary))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(timestamp.toInt(), notification)
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel(context: Context): NotificationManager {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                lightColor = ContextCompat.getColor(context, R.color.colorSecondary)
                description = "Reminders for your scheduled events"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC

                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }

            notificationManager.createNotificationChannel(channel)
        }
        return notificationManager
    }
}
