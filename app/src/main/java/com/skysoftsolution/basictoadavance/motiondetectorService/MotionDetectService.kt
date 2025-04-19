package com.skysoftsolution.basictoadavance.motiondetectorServic
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MotionDetectService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val CHANNEL_ID = "MotionChannel"

    override fun onCreate() {
        super.onCreate()
        try {
            // Sensor setup
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometer == null) {
                Log.e("MotionDetectService", "No accelerometer found.")
                stopSelf()
                return
            }
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

            // Notification
            createNotificationChannel()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Motion Detection Active")
                .setContentText("Service is running...")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .build()

            startForeground(1, notification)

        } catch (e: Exception) {
            Log.e("MotionDetectService", "Error in onCreate: ${e.message}")
            stopSelf()
        }
    }


    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())
        if (acceleration > 15) {
            Log.d("MotionDetectService", "Phone was moved!")

            sendTouchNotification()
        }
    }

    private fun sendTouchNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alert!")
            .setContentText("Your phone was touched or moved! 🚨")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, notification)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Motion Detector Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel)
            } else {
                Log.e("MotionDetectService", "NotificationManager is null")
            }
        }
    }
}

