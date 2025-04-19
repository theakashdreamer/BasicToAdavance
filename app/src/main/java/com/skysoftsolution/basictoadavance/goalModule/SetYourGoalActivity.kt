package com.skysoftsolution.basictoadavance.goalModule

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.callingModule.CallingDashBoardActivity
import com.skysoftsolution.basictoadavance.databinding.ActivitySetYourGoalBinding
import com.skysoftsolution.basictoadavance.databinding.CustomLayoutForGoalBinding
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.goalModule.adapter.GoalAdapter
import com.skysoftsolution.basictoadavance.goalModule.adapter.GoalClickListener
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack
import com.skysoftsolution.basictoadavance.goalModule.reciever.GoalReminderReceiver
import com.skysoftsolution.basictoadavance.goalModule.viewModel.SetYourViewModel
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.viewmodelfactory.MainViewModelFatcory
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator
import java.util.Calendar

class SetYourGoalActivity : AppCompatActivity(), GoalClickListener {
    private lateinit var binding: ActivitySetYourGoalBinding
    private lateinit var viewModel: SetYourViewModel
    private lateinit var adapter: GoalAdapter
    private lateinit var dataAccessObj: DataAccessObj
    private var isAllFabsVisible: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetYourGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Set Your Goal"
        Glide.with(this)
            .asGif()
            .load(R.drawable.gif_goaldash)
            .into(object : CustomTarget<GifDrawable>() {
                override fun onResourceReady(
                    resource: GifDrawable,
                    transition: Transition<in GifDrawable>?
                ) {
                    binding.draggableView.background = resource
                    resource.start() // Ensure the GIF animates
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.draggableView.background = placeholder
                }
            })
        setupViewModel()
        setupRecyclerView()
        observeGoals()
        setupFABActions()
    }

    private fun setupViewModel() {
        dataAccessObj = DataBaseCreator.getInstance(this).dataAccessObj
        viewModel = ViewModelProvider(
            this, MainViewModelFatcory(MainRepository(dataAccessObj), application)
        )[SetYourViewModel::class.java]

        viewModel.success.observe(this) { goal ->
            scheduleGoalReminder(goal.id, 30_000)
            Toast.makeText(this, "Goal '${goal.goalTitle}' saved!", Toast.LENGTH_SHORT).show()
        }

        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = GoalAdapter(emptyList(), this)
        binding.recyclerViewDistributors.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDistributors.adapter = adapter
    }

    private fun observeGoals() {
        viewModel.getAllGoalSetTrack()?.observe(this) { goals ->
            adapter.updateData(goals)
        }
    }

    private fun setupFABActions() {
        binding.addFab.setOnClickListener { toggleFabMenu() }
        binding.addPersonFab.setOnClickListener { showAddGoalDialog() }
        binding.addAlarmFab.setOnClickListener {
            Toast.makeText(this, "Alarm Added", Toast.LENGTH_SHORT).show()
        }
    }
    private fun toggleFabMenu() {
        isAllFabsVisible = !isAllFabsVisible
        val visibility = if (isAllFabsVisible) View.VISIBLE else View.GONE
     //   binding.addAlarmFab.visibility = visibility
        binding.addPersonFab.visibility = visibility
    //    binding.addAlarmActionText.visibility = visibility
        binding.addPersonActionText.visibility = visibility
    }

    private fun showAddGoalDialog() {
        val dialogBinding = CustomLayoutForGoalBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        alertDialog.show()

        dialogBinding.btnSubmit.setOnClickListener {
            val goal = getGoalSetTrackFromInput(dialogBinding)
            if (goal != null) {
                viewModel.insertGoalSetTrack(goal)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill out all required fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getGoalSetTrackFromInput(dialogBinding: CustomLayoutForGoalBinding): GoalSetTrack? {
        val goalTitle = dialogBinding.etGoalTitle.text.toString().trim()
        val goalDescription = dialogBinding.etGoalDescription.text.toString().trim()
        val startDate = dialogBinding.etStartDate.text.toString().trim()
        val endDate = dialogBinding.etEndDate.text.toString().trim()
        val progress = dialogBinding.sbProgress.progress
        val status = dialogBinding.spStatus.selectedItem.toString()

        if (goalTitle.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            return null
        }

        return GoalSetTrack(
            goalTitle = goalTitle,
            goalDescription = goalDescription,
            startDate = startDate,
            endDate = endDate,
            progress = progress,
            status = status
        )
    }

    private fun scheduleGoalReminder(goalId: Int, intervalMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, GoalReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, goalId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + intervalMillis
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            intervalMillis,
            pendingIntent
        )
    }

    private fun cancelGoalReminder(goalId: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, GoalReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, goalId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun onGoalProgressClicked(goal: GoalSetTrack) {
        val intent = Intent(this@SetYourGoalActivity, TrackYourGoalActivity::class.java)
        intent.putExtra("goal_object", goal) // Pass Parcelable object
        startActivity(intent)
    }

    override fun onGoalItemClicked(goal: GoalSetTrack) {
        showAddGoalDialog()
    }

    private fun showInProgressGoalDialog() {
        val dialogBinding = CustomLayoutForGoalBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        alertDialog.show()

        dialogBinding.btnSubmit.setOnClickListener {
            val goal = getGoalSetTrackFromInput(dialogBinding)
            if (goal != null) {
                viewModel.insertGoalSetTrack(goal)
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill out all required fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
