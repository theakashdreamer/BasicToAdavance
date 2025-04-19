package com.skysoftsolution.basictoadavance.goalModule.reciever
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.skysoftsolution.basictoadavance.R

class AlarmService : Service() {
    private var ringtone: Ringtone? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        playAlarmSound()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Ensures the service restarts if killed
    }

    override fun onDestroy() {
        ringtone?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val channelId = "alarm_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarm Alert!")
            .setContentText("Your alarm is ringing.")
            .setSmallIcon(R.drawable.daily_routine)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }

    private fun playAlarmSound() {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, alarmSound)
        ringtone?.play()
    }
}

