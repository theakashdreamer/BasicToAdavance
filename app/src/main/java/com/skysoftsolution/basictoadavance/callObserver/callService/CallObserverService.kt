package com.skysoftsolution.basictoadavance.callObserver.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.callObserver.broadcastReciver.CallStateReceiver

class CallObserverService : Service() {

    private lateinit var callStateReceiver: CallStateReceiver
    private lateinit var callStatusReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerReceivers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceivers()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "call_observer_channel",
                "Call Observer",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "call_observer_channel")
            .setContentTitle("Call Observer")
            .setContentText("Monitoring call activities")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceivers() {
        // Register system call receiver
        callStateReceiver = CallStateReceiver()
        val systemFilter = IntentFilter().apply {
            addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            addAction(Intent.ACTION_NEW_OUTGOING_CALL)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(callStateReceiver, systemFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(callStateReceiver, systemFilter)
        }

        // Register custom broadcast receiver
        callStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                handleCallEvent(intent)
            }
        }
        val customFilter = IntentFilter().apply {
            addAction("OUTGOING_CALL_STARTED")
            addAction("CALL_CONNECTED")
            addAction("CALL_NOT_ANSWERED")
            addAction("OUTGOING_CALL_COMPLETED")
            addAction("INCOMING_CALL_RINGING")
            addAction("CALL_ANSWERED")
            addAction("MISSED_CALL")
            addAction("INCOMING_CALL_ENDED")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(callStatusReceiver, customFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(callStatusReceiver, customFilter)
        }
    }

    private fun unregisterReceivers() {
        try {
            unregisterReceiver(callStateReceiver)
            unregisterReceiver(callStatusReceiver)
        } catch (e: Exception) {
            Log.e("CallObserverService", "Error unregistering receivers", e)
        }
    }

    private fun handleCallEvent(intent: Intent?) {
        val number = intent?.getStringExtra("phone_number") ?: "Unknown"
        val duration = intent?.getLongExtra("call_duration", 0) ?: 0
        val action = intent?.action ?: return

        // Broadcast to activity if it's in foreground
        val activityIntent = Intent("CALL_STATE_UPDATE").apply {
            putExtra("action", action)
            putExtra("phone_number", number)
            putExtra("call_duration", duration)
        }

        // For Android 13+, use explicit export flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            sendBroadcast(activityIntent, null)
        } else {
            sendBroadcast(activityIntent)
        }

        // Save to database
        saveCallToDatabase(action, number, duration)
    }

    private fun saveCallToDatabase(action: String, number: String, duration: Long) {
        // Implement database saving here
        Log.d("CallObserverService", "Saving call: $action, $number, $duration")
    }
}