package com.skysoftsolution.basictoadavance.callObserver.broadcastReciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallStateReceiver : BroadcastReceiver() {

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var isIncoming = false
    private var savedNumber: String? = null
    private var callStartTime: Long = 0
    private var wasConnected = false

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            // ✅ Outgoing call started
            Intent.ACTION_NEW_OUTGOING_CALL -> handleOutgoingCall(intent, context)

            // ✅ Phone state changed (Ringing, Offhook, Idle)
            TelephonyManager.ACTION_PHONE_STATE_CHANGED -> handlePhoneStateChanged(intent, context)
        }
    }

    // ------------------------------
    // 🔹 Outgoing Call Handling
    // ------------------------------
    private fun handleOutgoingCall(intent: Intent, context: Context) {
        savedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
        isIncoming = false
        Log.d(TAG, "Outgoing call started to: $savedNumber")
        sendBroadcast(context, ACTION_OUTGOING_STARTED, savedNumber)
    }

    // ------------------------------
    // 🔹 Incoming Call / State Change Handling
    // ------------------------------
    private fun handlePhoneStateChanged(intent: Intent, context: Context) {
        val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val state = when (stateStr) {
            TelephonyManager.EXTRA_STATE_IDLE -> TelephonyManager.CALL_STATE_IDLE
            TelephonyManager.EXTRA_STATE_OFFHOOK -> TelephonyManager.CALL_STATE_OFFHOOK
            TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
            else -> TelephonyManager.CALL_STATE_IDLE
        }
        onCallStateChanged(context, state, number)
    }

    // ------------------------------
    // 🔹 State Transitions
    // ------------------------------
    private fun onCallStateChanged(context: Context, state: Int, number: String?) {
        if (lastState == state) return

        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> handleIncomingRinging(context, number)
            TelephonyManager.CALL_STATE_OFFHOOK -> handleCallConnected(context)
            TelephonyManager.CALL_STATE_IDLE -> handleCallEnded(context)
        }

        lastState = state
    }

    // ------------------------------
    // 🔹 Specific Handlers
    // ------------------------------
    private fun handleIncomingRinging(context: Context, number: String?) {
        isIncoming = true
        savedNumber = number
        Log.d(TAG, "Incoming call from: $savedNumber")
        sendBroadcast(context, ACTION_INCOMING_RINGING, savedNumber)
    }

    private fun handleCallConnected(context: Context) {
        callStartTime = System.currentTimeMillis()
        wasConnected = true

        if (lastState == TelephonyManager.CALL_STATE_RINGING) {
            isIncoming = true
            Log.d(TAG, "Incoming call answered")
            sendBroadcast(context, ACTION_INCOMING_ANSWERED, savedNumber)
        } else {
            isIncoming = false
            Log.d(TAG, "Outgoing call connected to: $savedNumber")
            sendBroadcast(context, ACTION_OUTGOING_CONNECTED, savedNumber)
        }
    }

    private fun handleCallEnded(context: Context) {
        val duration = if (callStartTime > 0) System.currentTimeMillis() - callStartTime else 0

        when {
            lastState == TelephonyManager.CALL_STATE_RINGING -> {
                Log.d(TAG, "Missed call from: $savedNumber")
                sendBroadcast(context, ACTION_MISSED_CALL, savedNumber, duration)
            }
            isIncoming -> {
                Log.d(TAG, "Incoming call ended. Duration: ${duration}ms")
                sendBroadcast(context, ACTION_INCOMING_ENDED, savedNumber, duration)
            }
            else -> {
                val status = if (wasConnected) "COMPLETED" else "NOT_ANSWERED"
                Log.d(TAG, "Outgoing call ended - $status. Duration: ${duration}ms")

                if (wasConnected) {
                    sendBroadcast(context, ACTION_OUTGOING_COMPLETED, savedNumber, duration)
                } else {
                    sendBroadcast(context, ACTION_OUTGOING_NOT_ANSWERED, savedNumber, duration)
                }
            }
        }

        // Reset for next call
        wasConnected = false
        callStartTime = 0
    }

    // ------------------------------
    // 🔹 Utility
    // ------------------------------
    private fun sendBroadcast(context: Context, action: String, number: String?, duration: Long = 0) {
        val intent = Intent(action).apply {
            putExtra("phone_number", number)
            putExtra("call_duration", duration)
        }
        context.sendBroadcast(intent)
    }

    companion object {
        private const val TAG = "CallObserver"

        // Custom broadcast actions
        const val ACTION_OUTGOING_STARTED = "OUTGOING_CALL_STARTED"
        const val ACTION_OUTGOING_CONNECTED = "CALL_CONNECTED"
        const val ACTION_OUTGOING_COMPLETED = "OUTGOING_CALL_COMPLETED"
        const val ACTION_OUTGOING_NOT_ANSWERED = "CALL_NOT_ANSWERED"

        const val ACTION_INCOMING_RINGING = "INCOMING_CALL_RINGING"
        const val ACTION_INCOMING_ANSWERED = "CALL_ANSWERED"
        const val ACTION_INCOMING_ENDED = "INCOMING_CALL_ENDED"
        const val ACTION_MISSED_CALL = "MISSED_CALL"
    }
}
