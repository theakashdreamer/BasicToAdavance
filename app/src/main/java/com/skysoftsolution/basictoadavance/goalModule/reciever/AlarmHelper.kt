package com.skysoftsolution.basictoadavance.goalModule.reciever

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object AlarmHelper {
    fun scheduleGoalAlarm(context: Context, goalId: Int) {
        val intent = Intent(context, GoalReminderReceiver::class.java).apply {
            putExtra("GOAL_ID", goalId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, goalId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intervalMillis: Long = 6 * 60 * 60 * 1000 // Repeat every 6 hours

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }
}
