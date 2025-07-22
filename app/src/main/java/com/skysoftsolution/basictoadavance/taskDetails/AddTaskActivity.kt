package com.skysoftsolution.basictoadavance.taskDetails

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivityAddTaskBinding
import com.skysoftsolution.basictoadavance.databinding.CustomLayoutForAddTaskDailyBinding
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.eventManager.adapter.EventManagerAdapter
import com.skysoftsolution.basictoadavance.eventManager.adapter.UpcomingEventManagerAdapter
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.eventManager.utils.ReminderUtils
import com.skysoftsolution.basictoadavance.eventManager.viewModel.EventViewModel
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.taskDetails.adapter.AddTaskAdapter
import com.skysoftsolution.basictoadavance.taskDetails.dailog.SevenDayPlanDialogFragment
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.taskDetails.entity.DailyRoutineGroup
import com.skysoftsolution.basictoadavance.taskDetails.viewModel.AddTaskViewModel
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickAddSendPostion
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor
import com.skysoftsolution.basictoadavance.utility.FirebaseFetchHelper
import com.skysoftsolution.basictoadavance.viewmodelfactory.MainViewModelFatcory
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale

class AddTaskActivity : AppCompatActivity() , AdapterClickAddSendPostion {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var dataAccessObj: DataAccessObj
    private lateinit var addTaskViewModel: AddTaskViewModel
    private lateinit var adapter: AddTaskAdapter
    private lateinit var adapterUpComing: UpcomingEventManagerAdapter
    var currentDate=""
    var currentTime=""
    var currentTimestamp=""
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(title = "Daily Routine", showBackButton = true)
        val now = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        currentDate = dateFormat.format(now)
        currentTime = timeFormat.format(now)
        currentTimestamp = timestampFormat.format(now)
        TextBlinkWork()
        DateAndTimeWork()
        setupRecyclerView()
        setupViewModel()
        fetchdaily_routinesFromFirebase()
        observeAddTask()
        binding.fabShowSevenDays.setOnClickListener {
            val routinesLiveData: LiveData<List<AddDailyRoutine>> = addTaskViewModel.getSevenDaysRoutines()
            routinesLiveData.observe(this) { routines ->
                val grouped = routines.groupBy { routine ->
                    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = inputFormat.parse(routine.event_date)
                    if (date != null) {
                        outputFormat.format(date)  // Format to yyyy-MM-dd
                    } else {
                        ""
                    }
                }
                    .map { (date, routinesForDay) ->
                        DailyRoutineGroup(date, routinesForDay)
                    }
                    .sortedBy { group ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        sdf.parse(group.date)
                    }

                val dialog = SevenDayPlanDialogFragment(grouped)
                dialog.show(supportFragmentManager, "SevenDayPlanDialog")
            }


        }

    }

    private fun fetchdaily_routinesFromFirebase() {
   /*     binding.progressBar.visibility = View.VISIBLE  // Show progress
        db.collection("daily_routines")
            .get()
            .addOnSuccessListener { result ->
                addTaskViewModel.deleteAllDailyRoutine()
                for (document in result) {
                    val distributor = document.toObject(AddDailyRoutine::class.java)
                    addTaskViewModel.insertDailyRoutine(distributor)
                }
                binding.progressBar.visibility = View.GONE
                binding.eventRecyclerView.visibility = View.VISIBLE // Hide progress when done
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE  // Hide progress on failure
            }*/
        binding.progressBar.visibility = View.VISIBLE  // Show progress
        FirebaseFetchHelper.fetchDataIfNeeded<AddDailyRoutine>(
            context = this,
            db = db,
            collectionName = "daily_routines",
            onInsert = { item -> addTaskViewModel.insertDailyRoutine(item) },
            onDeleteAll = { addTaskViewModel.deleteAllDailyRoutine() },
            onComplete = {
                binding.progressBar.visibility = View.GONE
                binding.eventRecyclerView.visibility = View.VISIBLE
            }
        )

    }
    private fun TextBlinkWork() {
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        binding.tvSevenDayPlanner.startAnimation(blinkAnimation)
        val paint = binding.tvSevenDayPlanner.paint
        val width = paint.measureText(binding.tvSevenDayPlanner.text.toString())
        val textShader = LinearGradient(
            0f, 0f, width, binding.tvSevenDayPlanner.textSize,
            intArrayOf(
                ContextCompat.getColor(this, R.color.gradientStart),
                ContextCompat.getColor(this, R.color.gradientEnd)
            ),
            null,
            Shader.TileMode.CLAMP
        )
        binding.tvSevenDayPlanner.paint.shader = textShader
    }
    private fun DateAndTimeWork() {
        val currentDate = System.currentTimeMillis()
        // Format date with "month day year" pattern
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(currentDate))
        binding.monthCurrent.text = formattedDate
    }
    private fun setupRecyclerView() {
        adapter = AddTaskAdapter(Collections.emptyList(),this)
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.eventRecyclerView.adapter = adapter
    }
    private fun setupViewModel() {
        dataAccessObj = DataBaseCreator.getInstance(this).dataAccessObj
        addTaskViewModel = ViewModelProvider(
            this, MainViewModelFatcory(MainRepository(dataAccessObj), application)
        )[AddTaskViewModel::class.java]

        addTaskViewModel.success.observe(this) {
                distributor ->
        }
        addTaskViewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeAddTask() {
        showProgress(true)  // Start loader before fetching data
        addTaskViewModel.getTodayRoutines()?.observe(this) { events ->
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

/*        addTaskViewModel.getAllUpcomingRoutines().observe(this) { events ->
            showProgress(false)  // Stop loader if this is your main data
            val inputFormat = SimpleDateFormat("d/M/yyyy H:m", Locale.getDefault())
            val now = Date()
            val upcomingEvents = events.filter { event ->
                try {
                    val eventDate = inputFormat.parse(event.event_date)
                    eventDate != null && eventDate.after(now)
                } catch (e: Exception) {
                    false
                }
            }.sortedBy { event ->
                inputFormat.parse(event.event_date)
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
        }*/

       /* addTaskViewModel.filteredDistributors.observe(this) { events ->
            showProgress(false)  // Stop loader if filtering too
            adapter.updateData(events)
        }*/
    }

    private fun showProgress(show: Boolean) {
      //  binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.cardNoEventNoEvent.visibility = if (show) View.GONE else binding.cardNoEventNoEvent.visibility
        binding.eventRecyclerView.visibility = if (show) View.GONE else binding.eventRecyclerView.visibility
        //binding.cardNoEvent.visibility = if (show) View.GONE else binding.cardNoEvent.visibility
      //  binding.eventScheduleCardrecyclerView.visibility = if (show) View.GONE else binding.eventScheduleCardrecyclerView.visibility
    }

    private fun setupActionBar(title: String, showBackButton: Boolean = false) {
        supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(showBackButton)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                showAddEventDialog()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddEventDialog() {
        val dialogBinding = CustomLayoutForAddTaskDailyBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()
        dialog.show()
        setupDateTimePicker(dialogBinding)
        setupTimePicker(dialogBinding,"Start")
        setupTimePicker(dialogBinding,"End")
        setupTimePicker(dialogBinding,"Notify")

        dialogBinding.btnSubmitRoutine.setOnClickListener {
             val dailyRoutine = getRoutineFromInput(dialogBinding)
            dailyRoutine?.let {
                db.collection("daily_routines")
                    .add(it)
                    .addOnSuccessListener {
                        Toast.makeText(this, "routines saved!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            if (dailyRoutine != null) {
                addTaskViewModel.insertDailyRoutine(dailyRoutine)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill out all required fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getRoutineFromInput(dialogBinding: CustomLayoutForAddTaskDailyBinding): AddDailyRoutine? {
        val title = dialogBinding.etTitle.text.toString().trim()
        val description = dialogBinding.etDescription.text.toString().trim()  // Assuming this is "description"
        val notificationTime = dialogBinding.etNotificationTime.text.toString().trim() // Assuming this is "notification_time"

        val etDateTime = dialogBinding.etEventDate.text.toString()
        val eventTime = dialogBinding.etStartTime.text.toString()
        val end_time = dialogBinding.etEndTime.text.toString()
        val repeatType = dialogBinding.etRepeatType.text.toString()
        val currentTime = System.currentTimeMillis()
        val sdfDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val createdAt = sdfDateTime.format(Date(currentTime))
        val updatedAt = sdfDateTime.format(Date(currentTime))

        return if (title.isNotEmpty() && description.isNotEmpty() && etDateTime.isNotEmpty() && eventTime.isNotEmpty()) {
            AddDailyRoutine(
                routine_title = title,
                routine_description = description,
                event_date = etDateTime,
                event_time = eventTime,
                end_time = end_time,
                recurring =true                                                                   , // You can update this based on your logic
                repeat_type = repeatType, // You can add an EditText for repeat type if needed
                notification_time = notificationTime,
                completed = false,
                created_at = createdAt,
                updated_at = updatedAt
            )
        } else {
            Toast.makeText(dialogBinding.root.context, "Please fill out all required fields!", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun setupDateTimePicker(dialogBinding: CustomLayoutForAddTaskDailyBinding) {
        dialogBinding.etEventDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
               this@AddTaskActivity,  // if inside Fragment
                { _, year, month, day ->
                    TimePickerDialog(
                        this@AddTaskActivity,
                        { _, hour, minute ->
                            val formattedDate = String.format("%02d/%02d/%04d %02d:%02d", day, month + 1, year, hour, minute)
                            dialogBinding.etEventDate.setText(formattedDate)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // 🔥 Restrict the allowed date range
            datePickerDialog.datePicker.minDate = calendar.timeInMillis  // Today
            val maxCalendar = Calendar.getInstance()
            maxCalendar.add(Calendar.DAY_OF_YEAR, 6)  // 6 more days from today
            datePickerDialog.datePicker.maxDate = maxCalendar.timeInMillis  // 7 days including today
            datePickerDialog.show()
        }
    }

    private fun setupTimePicker(dialogBinding: CustomLayoutForAddTaskDailyBinding, type: String) {
        val editText = when (type) {
            "Start" -> dialogBinding.etStartTime
            "End" -> dialogBinding.etEndTime
            "Notify" -> dialogBinding.etNotificationTime
            else -> null
        }

        editText?.setOnClickListener {
            val calendar = Calendar.getInstance()

            // TimePickerDialog with 24-hour format
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    // Format hour and minute to 24-hour format
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    editText.setText(formattedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY), // 24-hour format hour
                calendar.get(Calendar.MINUTE),
                true // Ensure 24-hour format (no AM/PM)
            ).show()
        }
    }


    override fun onClickListenerEventReminder(distributor: EventReminder) {
        TODO("Not yet implemented")
    }

    override fun onSwitchStatusChanged(distributor: EventReminder, isActive: Boolean) {
        TODO("Not yet implemented")
    }


}
