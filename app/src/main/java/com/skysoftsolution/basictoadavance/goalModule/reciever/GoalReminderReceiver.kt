package com.skysoftsolution.basictoadavance.goalModule.reciever
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.goalModule.SetYourGoalActivity
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator

class GoalReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val goalId = intent?.getIntExtra("GOAL_ID", -1) ?: return
        if (goalId == -1) return

        val db = DataBaseCreator.getInstance(context!!).dataAccessObj
        val goal = db.getGoalById(goalId)

        goal?.let {
            if (it.progress < 100) {  // Check if goal is incomplete
                showNotification(context, "Goal Reminder", "Keep working on: ${it.goalTitle}")

                // Reschedule the alarm again
                scheduleGoalReminder(context, goalId, 30_000)  // 30 seconds for testing
            } else {
                Log.d("AlarmReceiver", "Goal is completed, stopping alarm")
            }
        }
    }

    fun scheduleGoalReminder(context: Context, goalId: Int, interval: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, GoalReminderReceiver::class.java).apply {
            putExtra("GOAL_ID", goalId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, goalId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pendingIntent)
    }
    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "goal_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Goal Reminder", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.goals)  // Replace with your icon
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
