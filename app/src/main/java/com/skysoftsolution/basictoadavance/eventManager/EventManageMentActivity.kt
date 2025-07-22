package com.skysoftsolution.basictoadavance.eventManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.firebase.firestore.FirebaseFirestore
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivityEventManageMentBinding
import com.skysoftsolution.basictoadavance.databinding.CustomEventAddDetailsLayoutBinding
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.eventManager.adapter.EventManagerAdapter
import com.skysoftsolution.basictoadavance.eventManager.adapter.UpcomingEventManagerAdapter
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
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
class EventManageMentActivity : AppCompatActivity(), AdapterClickAddSendPostion {
    private lateinit var binding: ActivityEventManageMentBinding
    private lateinit var dataAccessObj: DataAccessObj
    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: EventManagerAdapter
    private lateinit var adapterUpComing: UpcomingEventManagerAdapter
    private var scrollHandler: Handler? = null
    private var scrollRunnable: Runnable? = null
    private var currentPosition = 0
    val db = FirebaseFirestore.getInstance()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventManageMentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        setupActionBar("Events", true)
        setupRecyclerViewUpcomingEvent()
        setupRecyclerView()
        setupViewModel()
        fetchDistributorsFromFirebase()
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

        eventViewModel.success.observe(this) {
                distributor ->
        }
        eventViewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.cardNoEventNoEvent.visibility = if (show) View.GONE else binding.cardNoEventNoEvent.visibility
        binding.eventRecyclerView.visibility = if (show) View.GONE else binding.eventRecyclerView.visibility
        binding.cardNoEvent.visibility = if (show) View.GONE else binding.cardNoEvent.visibility
        binding.eventScheduleCardrecyclerView.visibility = if (show) View.GONE else binding.eventScheduleCardrecyclerView.visibility
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeEventReminder() {
        showProgress(true)  // Start loader before fetching data
        eventViewModel.getAllEventReminder()?.observe(this) { events ->
            showProgress(false)  // Stop loader when data is loaded
           /* val formatter = DateTimeFormatter.ofPattern("d/M/yyyy H:mm")
            val sortedList = events.sortedBy {
                LocalDateTime.parse(it.eventTime, formatter)
            }
*/
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
        adapter = EventManagerAdapter(Collections.emptyList(),this)
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.eventRecyclerView.adapter = adapter
    }
    private fun setupRecyclerViewUpcomingEvent() {
        adapterUpComing = UpcomingEventManagerAdapter(Collections.emptyList(), this)
        binding.eventScheduleCardrecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.eventScheduleCardrecyclerView.adapter = adapterUpComing
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.eventScheduleCardrecyclerView)
        startAutoScroll()
    }
    private fun fetchDistributorsFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE  // Show progress
//        db.collection("events_for_training")
//            .get()
//            .addOnSuccessListener { result ->
//                eventViewModel.deleteAllEventReminder()
//                for (document in result) {
//                    val event = document.toObject(EventReminder::class.java)
//                    eventViewModel.insertEventReminder(event)
//                }
//                binding.progressBar.visibility = View.GONE
//                binding.llForUpcomingEvents.visibility = View.VISIBLE // Hide progress when done
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
//                binding.progressBar.visibility = View.GONE  // Hide progress on failure
//            }
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
            showAddEventDialog()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    private fun showAddEventDialog() {
        val dialogBinding = CustomEventAddDetailsLayoutBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        alertDialog.show()

        // 👉 Set up date-time picker listener here
        dialogBinding.etDateTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val formatted = "$day/${month + 1}/$year $hour:$minute"
                    val formattedText = String.format("%02d/%02d/%04d %02d:%02d", day, month + 1, year, hour, minute)
                    dialogBinding.etDateTime.setText(formattedText)

                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        dialogBinding.btnSubmit.setOnClickListener {
            val event = getEventFromInput(dialogBinding)
            event?.let {
                db.collection("events_for_training")  // Name of collection
                    .add(it)
                    .addOnSuccessListener {
                        Toast.makeText(dialogBinding.root.context, "events saved!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(dialogBinding.root.context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            if (event != null) {
                eventViewModel.insertEventReminder(event)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill out all required fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getEventFromInput(dialogBinding: CustomEventAddDetailsLayoutBinding): EventReminder? {
        val name = dialogBinding.etTitleName.text.toString().trim()
        val etSpeakerName = dialogBinding.etSpeakerName.text.toString().trim()
        val cityName = dialogBinding.etCityNAme.text.toString().trim()
        val etDateTime = dialogBinding.etDateTime
        etDateTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    // Save this for database
                    val isoFormat = String.format("%04d-%02d-%02d %02d:%02d", year, month + 1, day, hour, minute)
                    // Show this to the user
                    val displayFormat = String.format("%d/%d/%d %02d:%02d", day, month + 1, year, hour, minute)

                    etDateTime.setTag(isoFormat)   // store for DB save
                    etDateTime.setText(displayFormat)  // show friendly text
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val dateTime = dialogBinding.etDateTime.text?.toString()?.trim() ?: ""

        return if (name.isNotEmpty() && etSpeakerName.isNotEmpty()) {
            EventReminder(
                title = name,
                speakerName = etSpeakerName,
                cityName = cityName,
                eventTime =dateTime,
                isRecurring =true
            )
        } else {
            Toast.makeText(dialogBinding.root.context, "Please fill out all required fields!", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override fun onClickListenerEventReminder(distributor: EventReminder) {
        TODO("Not yet implemented")
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
}