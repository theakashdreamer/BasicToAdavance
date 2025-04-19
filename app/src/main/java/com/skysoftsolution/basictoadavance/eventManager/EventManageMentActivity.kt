package com.skysoftsolution.basictoadavance.eventManager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivityEventManageMentBinding
import com.skysoftsolution.basictoadavance.databinding.CustomEventAddDetailsLayoutBinding
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.eventManager.adapter.EventManagerAdapter
import com.skysoftsolution.basictoadavance.eventManager.adapter.UpcomingEventManagerAdapter
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.eventManager.viewModel.EventViewModel
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickEventSendPostion
import com.skysoftsolution.basictoadavance.viewmodelfactory.MainViewModelFatcory
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale


class EventManageMentActivity : AppCompatActivity(), AdapterClickEventSendPostion {
    private lateinit var binding: ActivityEventManageMentBinding
    private lateinit var dataAccessObj: DataAccessObj
    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: EventManagerAdapter
    private lateinit var adapterUpComing: UpcomingEventManagerAdapter
    private var scrollHandler: Handler? = null
    private var scrollRunnable: Runnable? = null
    private var currentPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventManageMentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        }
        setupRecyclerViewUpcomingEvent()
        setupRecyclerView()
        setupViewModel()
        setupFABActions()
        observeEventReminder()
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


    private fun observeEventReminder() {
        eventViewModel.getAllEventReminder()?.observe(this) { events ->
            adapter.updateData(events)
        }

      /*  eventViewModel.getAllUpComingReminders()?.observe(this) { events ->
            adapterUpComing.updateData(events)
        }*/

        eventViewModel.getAllUpComingReminders().observe(this) { events ->
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
            adapterUpComing.updateData(upcomingEvents)
            currentPosition = 0
            startAutoScroll()
        }
        eventViewModel.filteredDistributors.observe(this) { events ->
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
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
                    dialogBinding.etDateTime.setText(formatted)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        dialogBinding.btnSubmit.setOnClickListener {
            val event = getEventFromInput(dialogBinding)
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
                    val formatted = "$day/${month + 1}/$year $hour:$minute"
                    etDateTime.setText(formatted)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        val dateTime = dialogBinding.etDateTime.text.toString().trim()


        return if (name.isNotEmpty() && etSpeakerName.isNotEmpty()) {
            EventReminder(
                title = name,
                speakerName = etSpeakerName,
                cityName = cityName,
                eventTime =dateTime,
                isRecurring = "1"
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
}