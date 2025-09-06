package com.skysoftsolution.basictoadavance.callObserver

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.callObserver.entity.CallLog
import com.skysoftsolution.basictoadavance.callObserver.service.CallObserverService
import com.skysoftsolution.basictoadavance.callObserver.viewModel.CallDetailsViewModel
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.viewmodelfactory.MainViewModelFatcory
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator

class CallObserverActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var detailsTextView: TextView
    private lateinit var phoneNumberEditText: EditText
    private lateinit var makeCallButton: Button
    private lateinit var callLogViewModel: CallDetailsViewModel
    private lateinit var dataAccessObj: DataAccessObj

    // Receiver for service updates
    private val serviceUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.getStringExtra("action") ?: return
            val number = intent.getStringExtra("phone_number") ?: "Unknown"
            val duration = intent.getLongExtra("call_duration", 0)

            handleCallEvent(action, number, duration)
        }
    }

    // ---------------------------------------------------------
    // 🔹 Permission Handling
    // ---------------------------------------------------------
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            startCallObserverService()
            updateUI("Ready", "Call observer active. Make a call to test.")
            enableCallButton()
        } else {
            showToast("Permissions denied. Call observation won't work.")
            updateUI("Permission Denied", "Please grant phone permissions")
            disableCallButton()
        }
    }

    // ---------------------------------------------------------
    // 🔹 Lifecycle
    // ---------------------------------------------------------
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call_observer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()
        initializeUI()
        setupClickListeners()

        // Request permissions
        requestCallPermissions()
    }

    override fun onResume() {
        super.onResume()
        registerServiceUpdateReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterServiceUpdateReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't stop service here if you want it to continue running
    }

    // ---------------------------------------------------------
    // 🔹 Setup ViewModel & Repository
    // ---------------------------------------------------------
    private fun setupViewModel() {
        dataAccessObj = DataBaseCreator.getInstance(this).dataAccessObj
        callLogViewModel = ViewModelProvider(
            this,
            MainViewModelFatcory(MainRepository(dataAccessObj), application)
        )[CallDetailsViewModel::class.java]
    }

    private fun initializeUI() {
        statusTextView = findViewById(R.id.statusTextView111)
        detailsTextView = findViewById(R.id.detailsTextView)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        makeCallButton = findViewById(R.id.makeCallButton)
        disableCallButton()
    }

    private fun setupClickListeners() {
        makeCallButton.setOnClickListener {
            makePhoneCall()
        }
    }

    // ---------------------------------------------------------
    // 🔹 Service Management
    // ---------------------------------------------------------
    private fun startCallObserverService() {
        val serviceIntent = Intent(this, CallObserverService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerServiceUpdateReceiver() {
        val filter = IntentFilter("CALL_STATE_UPDATE")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use ContextCompat for proper handling
            registerReceiver(serviceUpdateReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(serviceUpdateReceiver, filter)
        }
    }

    private fun unregisterServiceUpdateReceiver() {
        try {
            unregisterReceiver(serviceUpdateReceiver)
        } catch (e: Exception) {
            // Receiver was not registered
        }
    }

    // ---------------------------------------------------------
    // 🔹 Make Phone Call Function
    // ---------------------------------------------------------
    @SuppressLint("MissingPermission")
    private fun makePhoneCall() {
        val phoneNumber = phoneNumberEditText.text.toString().trim()

        if (phoneNumber.isEmpty()) {
            showToast("Please enter a phone number")
            return
        }

        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            showToast("Calling: $phoneNumber")
        } catch (e: SecurityException) {
            showToast("Call permission denied")
        } catch (e: Exception) {
            showToast("Failed to make call: ${e.message}")
        }
    }

    // ---------------------------------------------------------
    // 🔹 Handle Call Events
    // ---------------------------------------------------------
    private fun handleCallEvent(action: String, number: String, duration: Long) {
        when (action) {
            "OUTGOING_CALL_STARTED" -> {
                updateUI("📞 Outgoing Call Started", "Dialing: $number")
                saveCallToDb("OUTGOING_STARTED", number, 0)
            }
            "CALL_CONNECTED" -> {
                updateUI("✅ Call Connected", "Connected to: $number")
                saveCallToDb("CALL_CONNECTED", number, 0)
            }
            "CALL_NOT_ANSWERED" -> {
                updateUI("❌ Not Answered", "No response from: $number")
                saveCallToDb("CALL_NOT_ANSWERED", number, 0)
            }
            "OUTGOING_CALL_COMPLETED" -> {
                val durationSec = duration / 1000
                updateUI("📴 Call Completed", "Duration: ${durationSec}s with $number")
                saveCallToDb("OUTGOING_COMPLETED", number, durationSec)
            }
            "INCOMING_CALL_RINGING" -> {
                updateUI("📲 Incoming Call", "Ringing from: $number")
                saveCallToDb("INCOMING_RINGING", number, 0)
            }
            "CALL_ANSWERED" -> {
                updateUI("✅ Call Answered", "Answered call from: $number")
                saveCallToDb("CALL_ANSWERED", number, 0)
            }
            "MISSED_CALL" -> {
                updateUI("⏰ Missed Call", "Missed call from: $number")
                saveCallToDb("MISSED_CALL", number, 0)
            }
            "INCOMING_CALL_ENDED" -> {
                val durationSec = duration / 1000
                updateUI("📴 Incoming Call Ended", "Duration: ${durationSec}s from $number")
                saveCallToDb("INCOMING_ENDED", number, durationSec)
            }
        }
    }

    // ---------------------------------------------------------
    // 🔹 Permissions
    // ---------------------------------------------------------
    private fun requestCallPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CALL_PHONE
        )
        requestPermissionLauncher.launch(permissions)
    }

    // ---------------------------------------------------------
    // 🔹 UI & DB Helpers
    // ---------------------------------------------------------
    private fun updateUI(status: String, details: String) {
        runOnUiThread {
            statusTextView.text = "Status: $status"
            detailsTextView.text = details
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun enableCallButton() {
        makeCallButton.isEnabled = true
        makeCallButton.alpha = 1.0f
    }

    private fun disableCallButton() {
        makeCallButton.isEnabled = false
        makeCallButton.alpha = 0.5f
    }

    private fun saveCallToDb(type: String, number: String?, duration: Long) {
        val callLog = CallLog(
            type = type,
            number = number,
            duration = duration,
            timestamp = System.currentTimeMillis()
        )
        callLogViewModel.insertCallLog(callLog)
    }
}