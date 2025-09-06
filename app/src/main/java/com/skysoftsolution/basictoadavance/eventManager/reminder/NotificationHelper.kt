package com.skysoftsolution.basictoadavance.eventManager.reminder

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.eventManager.EventManageMentActivity
import java.util.Date


class NotificationHelper(val context: Context) {
    private  val CHANNEL_ID="REMINDER_NOTIFICATIONS"
    private val NOTIFICATION_ID=Date().time.toString().let { it.substring(it.length-5).toInt() }
    @SuppressLint("MissingPermission")
    fun createNotification(title:String, message:String){
        createNotificationChannel()


        val intent=Intent(context,EventManageMentActivity::class.java)
        val pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_IMMUTABLE)
        val icon=BitmapFactory.decodeResource(context.resources,R.drawable.ic_notification)
        val notification=NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID,notification)
    }


    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel=NotificationChannel(CHANNEL_ID,CHANNEL_ID,NotificationManager.IMPORTANCE_DEFAULT).apply {
                description="Reminder channel to display all user scheduled reminders"
            }
            val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}