package com.skysoftsolution.basictoadavance.eventManager.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.goalModule.reciever.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.Locale

object ReminderUtils {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleEventReminder(context: Context, event: EventReminder) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", event.title)
            putExtra("speaker", event.speakerName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val inputFormat = SimpleDateFormat("d/M/yyyy H:m", Locale.getDefault())
        val eventDate = inputFormat.parse(event.eventTime)

        eventDate?.let {
            var alarmManager: AlarmManager? =null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                 alarmManager =  context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                }
            }
            val futureTime = System.currentTimeMillis() + 60_000 // 1 minute
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    it.time,
                    pendingIntent
                )
            }
        }
    }
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleEventReminder11(context: Context, event: EventReminder) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.skysoftsolution.EVENT_REMINDER"  // Must match your receiver's filter!
            putExtra("title", event.title)
            putExtra("speaker", event.speakerName)
            putExtra("timestamp", parseEventTime(event.eventTime))  // Pass the timestamp to use in notification
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id,  // Unique ID per event
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val eventTime = parseEventTime(event.eventTime)

        if (eventTime > 0) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Request permission for exact alarms on Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                val permissionIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                permissionIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(permissionIntent)
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                eventTime,
                pendingIntent
            )
        }
    }

    // Helper to parse your date string into timestamp
    private fun parseEventTime(eventTime: String): Long {
        return try {
            val inputFormat = SimpleDateFormat("d/M/yyyy H:m", Locale.getDefault())
            inputFormat.parse(eventTime)?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun cancelEventReminder(context: Context, eventId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(it)
        }
    }
}
