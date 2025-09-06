package com.skysoftsolution.basictoadavance.eventManager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivityEventManageMentBinding
import com.skysoftsolution.basictoadavance.databinding.CustomEventAddDetailsLayoutBinding
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.eventManager.adapter.EventManagerAdapter
import com.skysoftsolution.basictoadavance.eventManager.adapter.UpcomingEventManagerAdapter
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.eventManager.reminder.ReminderWorker
import com.skysoftsolution.basictoadavance.eventManager.utils.ReminderUtils
import com.skysoftsolution.basictoadavance.eventManager.viewModel.EventViewModel
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickAddSendPostion
import com.skysoftsolution.basictoadavance.utility.FirebaseFetchHelper
import com.skysoftsolution.basictoadavance.viewmodelfactory.MainViewModelFatcory
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class EventManageMentActivity : AppCompatActivity(), AdapterClickAddSendPostion {
    private lateinit var binding: ActivityEventManageMentBinding
    private lateinit var dataAccessObj: DataAccessObj
    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: EventManagerAdapter
    private lateinit var adapterUpComing: UpcomingEventManagerAdapter
    private var scrollHandler: Handler? = null
    private var scrollRunnable: Runnable? = null
    private var currentPosition = 0
    private lateinit var mapView: MapView
    private lateinit var googleMapInstance: GoogleMap
    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventManageMentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupActionBar("Events", true)
        setupRecyclerViewUpcomingEvent()
        setupRecyclerView()
        setupViewModel()
        //fetchDistributorsFromFirebase()
        setupFABActions()
        observeEventReminder()
    }

    private fun setupActionBar(title: String, showBackButton: Boolean = false) {
        supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(showBackButton)
        }
    }

    private fun setupViewModel() {
        dataAccessObj = DataBaseCreator.getInstance(this).dataAccessObj
        eventViewModel = ViewModelProvider(
            this, MainViewModelFatcory(MainRepository(dataAccessObj), application)
        )[EventViewModel::class.java]

        eventViewModel.success.observe(this) { distributor ->
        }
        eventViewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.cardNoEventNoEvent.visibility =
            if (show) View.GONE else binding.cardNoEventNoEvent.visibility
        binding.eventRecyclerView.visibility =
            if (show) View.GONE else binding.eventRecyclerView.visibility
        binding.cardNoEvent.visibility = if (show) View.GONE else binding.cardNoEvent.visibility
        binding.eventScheduleCardrecyclerView.visibility =
            if (show) View.GONE else binding.eventScheduleCardrecyclerView.visibility
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeEventReminder() {
        val current = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date())
        showProgress(true)  // Start loader before fetching data
        eventViewModel.getAllEventReminder()?.observe(this) { events ->
            showProgress(false)  // Stop loader when data is loaded
            if (events.isEmpty()) {
                binding.cardNoEventNoEvent.visibility = View.VISIBLE
                binding.eventRecyclerView.visibility = View.GONE
            } else {
                binding.cardNoEventNoEvent.visibility = View.GONE
                binding.eventRecyclerView.visibility = View.VISIBLE
                adapter.updateData(events)
            }
        }

        eventViewModel.getAllUpComingReminders().observe(this) { events ->
            showProgress(false)  // Stop loader if this is your main data
            val inputFormat = SimpleDateFormat("d/M/yyyy H:m", Locale.getDefault())
            val now = Date()
            val upcomingEvents = events.filter { event ->
                try {
                    val eventDate = inputFormat.parse(event.eventTime)
                    eventDate != null && eventDate.after(now)
                } catch (e: Exception) {
                    false
                }
            }.sortedBy { event ->
                inputFormat.parse(event.eventTime)
            }
            // Schedule alarms for all upcoming events
            upcomingEvents.forEach { event ->
                ReminderUtils.scheduleEventReminder11(this, event)
            }
            if (upcomingEvents.isEmpty()) {
                binding.cardNoEvent.visibility = View.VISIBLE
                binding.eventScheduleCardrecyclerView.visibility = View.GONE
            } else {
                binding.cardNoEvent.visibility = View.GONE
                binding.eventScheduleCardrecyclerView.visibility = View.VISIBLE
                adapterUpComing.updateData(upcomingEvents)
                currentPosition = 0
                startAutoScroll()
            }
        }

        eventViewModel.filteredDistributors.observe(this) { events ->
            showProgress(false)  // Stop loader if filtering too
            adapter.updateData(events)
        }
    }


    private fun setupRecyclerView() {
        adapter = EventManagerAdapter(Collections.emptyList(), this)
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setItemSelectionListener(this@EventManageMentActivity)
        binding.eventRecyclerView.adapter = adapter
    }

    private fun setupRecyclerViewUpcomingEvent() {
        adapterUpComing = UpcomingEventManagerAdapter(Collections.emptyList(), this)
        binding.eventScheduleCardrecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.eventScheduleCardrecyclerView.adapter = adapterUpComing
        adapterUpComing.setItemSelectionListener(this@EventManageMentActivity)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.eventScheduleCardrecyclerView)
        startAutoScroll()
    }

    private fun fetchDistributorsFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE  // Show progress
        db.collection("events_for_training")
            .get()
            .addOnSuccessListener { result ->
                eventViewModel.deleteAllEventReminder()
                for (document in result) {
                    val event = document.toObject(EventReminder::class.java)
                    eventViewModel.insertEventReminder(event)
                }
                binding.progressBar.visibility = View.GONE
                binding.llForUpcomingEvents.visibility = View.VISIBLE // Hide progress when done
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error fetching data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE  // Hide progress on failure
            }
        FirebaseFetchHelper.fetchDataIfNeeded<EventReminder>(
            context = this,
            db = db,
            collectionName = "events_for_training",
            onInsert = { item -> eventViewModel.insertEventReminder(item) },
            onDeleteAll = { eventViewModel.deleteAllEventReminder() },
            onComplete = {
                binding.progressBar.visibility = View.GONE
                binding.eventRecyclerView.visibility = View.VISIBLE
            }
        )
    }

    private fun startAutoScroll() {
        scrollHandler = Handler(Looper.getMainLooper())
        scrollRunnable = object : Runnable {
            override fun run() {
                val itemCount = adapterUpComing.itemCount
                if (itemCount == 0) return
                if (currentPosition >= itemCount) currentPosition = 0
                binding.eventScheduleCardrecyclerView.smoothScrollToPosition(currentPosition)
                currentPosition++
                scrollHandler?.postDelayed(this, 2000)  // 1 seconds delay
            }
        }
        scrollHandler?.post(scrollRunnable!!)
    }

    private fun stopAutoScroll() {
        scrollHandler?.removeCallbacks(scrollRunnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
    }

    private fun setupFABActions() {
        binding.btnAddClassroom.setOnClickListener {
            addReminder()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    private fun getEventFromInput(
        dialogBinding: CustomEventAddDetailsLayoutBinding,
        selectedAddress: String?,
        selectedLat: Double?,
        selectedLng: Double?
    ): EventReminder? {

        val title = dialogBinding.etTitleName.text.toString().trim()
        val speakerName = dialogBinding.etSpeakerName.text.toString().trim()
        val cityName = dialogBinding.etCityNAme.text.toString().trim()
        val dateTime = dialogBinding.etDateTime.text?.toString()?.trim() ?: ""

        // Validation
        if (title.isEmpty()) {
            showToast("Please enter the event title")
            return null
        }
        if (speakerName.isEmpty()) {
            showToast("Please enter the speaker name")
            return null
        }
        if (cityName.isEmpty()) {
            showToast("Please enter the city name")
            return null
        }
        if (dateTime.isEmpty()) {
            showToast("Please select the date and time")
            return null
        }
        if (selectedAddress.isNullOrEmpty() || selectedLat == null || selectedLng == null) {
            showToast("Please select a location on the map")
            return null
        }

        // Create and return EventReminder
        return EventReminder().apply {
            this.title = title
            this.speakerName = speakerName
            this.cityName = cityName
            this.eventTime = dateTime
            this.isRecurring = true // Or take from a checkbox if you add one
            this.address = selectedAddress
            this.latitude = selectedLat
            this.longitude = selectedLng
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onClickListenerEventReminder(eventReminder: EventReminder) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(eventReminder.address)}")

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // Let the system choose the available maps app (Google Maps, Rapido, Ola, etc.)
        val chooser = Intent.createChooser(mapIntent, "Open with")
        try {
            startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(this, "No maps application found", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onSwitchStatusChanged(distributor: EventReminder, isActive: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            android.R.id.home -> {
                finish()  // Handles back button on ActionBar
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddEventDialog() {
        val dialogBinding = CustomEventAddDetailsLayoutBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        alertDialog.show()

        var selectedLatLng: LatLng? = null
        var selectedAddress: String? = null

        // --- MAP INITIALIZATION ---
        dialogBinding.mapViewDialog.onCreate(null)
        dialogBinding.mapViewDialog.getMapAsync { googleMap ->
            googleMapInstance = googleMap
            val defaultLocation = LatLng(28.6139, 77.2090) // New Delhi
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
            googleMap.setOnMapClickListener { latLng ->
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
                selectedLatLng = latLng
                selectedAddress = getAddressFromLatLng(latLng) // Reverse geocode
            }
        }

        // MapView lifecycle cleanup
        alertDialog.setOnDismissListener {
            dialogBinding.mapViewDialog.onDestroy()
        }

        // --- DATE & TIME PICKER ---
        val pickedDate = Calendar.getInstance()
        dialogBinding.etDateTime.setOnClickListener {
            val year = pickedDate.get(Calendar.YEAR)
            val month = pickedDate.get(Calendar.MONTH)
            val day = pickedDate.get(Calendar.DAY_OF_MONTH)
            val hour = pickedDate.get(Calendar.HOUR_OF_DAY)
            val minute = pickedDate.get(Calendar.MINUTE)
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    TimePickerDialog(
                        this,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            pickedDate.set(year, month, dayOfMonth, hourOfDay, minute)
                            Log.d("Date And Time", "Picked Date and Time $pickedDate")
                            dialogBinding.etDateTime.setText(
                                getCurrentDateAndTime(pickedDate.timeInMillis)
                            )
                        },
                        hour,
                        minute,
                        false
                    ).show()
                },
                year,
                month,
                day
            )
                .show()
        }

        // --- SEARCH FUNCTIONALITY ---
        dialogBinding.etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 3) { // Only search when typing at least 3 chars
                    searchAndMoveMap(query) { latLng, address ->
                        selectedLatLng = latLng
                        selectedAddress = address
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // --- SUBMIT BUTTON ---
        dialogBinding.btnSubmit.setOnClickListener {
            val event = getEventFromInput(
                dialogBinding,
                selectedAddress,
                selectedLatLng?.latitude,
                selectedLatLng?.longitude
            )
            val timeDelayinSeconds =
                (pickedDate.timeInMillis / 1000L) - (Calendar.getInstance().timeInMillis / 1000L)
            if (timeDelayinSeconds < 0) {
                Toast.makeText(this, "Cant set reminders for past", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (event != null) {
                // Save to Firestore
         /*       db.collection("events_for_training")
                    .add(event)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }*/

                // Save to local DB
                eventViewModel.insertEventReminder(event)
                //Work Manager
                createWorkRequest(
                    event.title,
                    event.speakerName,
                    timeDelayinSeconds
                )
                alertDialog.dismiss()
            }
        }

        dialogBinding.mapViewDialog.onResume()
    }

    private fun getCurrentDateAndTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }

    /**
     * Reverse geocode to get human-readable address from LatLng
     */
    private fun getAddressFromLatLng(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0) ?: ""
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun searchAndMoveMap(locationName: String, onResult: (LatLng, String) -> Unit) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)

                googleMapInstance.clear()
                googleMapInstance.addMarker(MarkerOptions().position(latLng).title(locationName))
                googleMapInstance.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                onResult(latLng, address.getAddressLine(0) ?: "")
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= 33) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationPermissionDialog()
                    } else {
                        showSettingsDialog()
                    }
                }
            } else {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            }
        }

    private fun addReminder() {
        if (Build.VERSION.SDK_INT >= 33 && !NotificationManagerCompat.from(this)
                .areNotificationsEnabled()
        ) {
            showNotificationPermissionDialog()
        } else {
            showAddEventDialog()
        }
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Notification Permission")
            .setMessage("Notification permission required to show notifications")
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${this.applicationContext?.packageName}")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionDialog() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle("Notification Permission")
            .setMessage("Notification permission required to show notifications")
            .setPositiveButton("OK") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createWorkRequest(title: String, reminderType: String, delay: Long) {
        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setInputData(workDataOf("Title" to "Event: $reminderType", "Message" to title))
            .build()
        WorkManager.getInstance(this).enqueue(reminderWorkRequest)
    }
}