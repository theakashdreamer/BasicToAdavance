package com.skysoftsolution.basictoadavance.dashBoardScreens

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.adapters.CustomAdapterForDash
import com.skysoftsolution.basictoadavance.databinding.ActivityDashBoardScreenBinding
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.DashBoardModule
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.ModuleForUse
import com.skysoftsolution.thingisbeing.dashBoard.dashboardutils.DashBoardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.networkmonitoring.NetworkMonitor
import com.skysoftsolution.basictoadavance.networkmonitoring.NetworkMonitorII
import com.skysoftsolution.basictoadavance.networkmonitoring.NetworkViewModel
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.taskDetails.viewModel.AddTaskViewModel
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor
import com.skysoftsolution.basictoadavance.teamModules.viewModel.TeamViewModel
import com.skysoftsolution.basictoadavance.utility.FirebaseFetchHelper
import com.skysoftsolution.basictoadavance.viewmodelfactory.MainViewModelFatcory
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator

class DashBoardScreen : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardScreenBinding
    private lateinit var dashboardmodules: DashBoardModule
    private val viewModel: DashBoardViewModel = DashBoardViewModel()
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var sensorListener: SensorEventListener
    private lateinit var addTaskViewModel: AddTaskViewModel
    private lateinit var teamViewModel: TeamViewModel
    private lateinit var dataAccessObj: DataAccessObj
    private lateinit var db: FirebaseFirestore
    private val CHANNEL_ID = "MotionAlert"
    private lateinit var viewNetwoekModel: NetworkViewModel
    private var waveAnimator: AnimatorSet? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*    Glide.with(this)
                .asGif()
                .load(R.drawable.dashboardgif)  // Your GIF file in res/drawable
                .into(binding.imageViewback)*/
        db = FirebaseFirestore.getInstance()


        // ViewModel connects to NetworkMonitorII
        viewNetwoekModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        viewNetwoekModel.getStatus().observe(this) { status ->
            checkNetworkType(status)
        }

        setupViewModel()

        DateAndTimeWork()
        viewModel.userList.observe(this@DashBoardScreen, Observer { userList ->
            setAdapterData(userList)
        })
        val newUser1 = ModuleForUse(1, "Daily Routine ", R.drawable.daily_routine)
        viewModel.addModule(newUser1)
        val newUser = ModuleForUse(2, "Goals", R.drawable.goalsetting)
        viewModel.addModule(newUser)

        val newUser2 = ModuleForUse(3, "Team", R.drawable.partners)
        viewModel.addModule(newUser2)

        val newUser4 = ModuleForUse(5, "Event", R.drawable.goalsetting)
        viewModel.addModule(newUser4)

        val newUser3 = ModuleForUse(4, "Learning\n&\nProductivity", R.drawable.goalsetting)
        viewModel.addModule(newUser3)
        val newUser5 = ModuleForUse(6, "Voice Detector", R.drawable.goalsetting)
        viewModel.addModule(newUser5)

        /*


                val newUser6 = ModuleForUse(9, "Chat ", R.drawable.goalsetting)
                viewModel.addModule(newUser6)

                val newUser7 = ModuleForUse(10, "Chat New", R.drawable.goalsetting)
                viewModel.addModule(newUser7)*/

        clickListeiner()
    }

    private fun setupViewModel() {
        dataAccessObj = DataBaseCreator.getInstance(this).dataAccessObj
        // Initialize ViewModels
        teamViewModel = ViewModelProvider(
            this, MainViewModelFatcory(MainRepository(dataAccessObj), application)
        )[TeamViewModel::class.java]

        teamViewModel = ViewModelProvider(
            this, MainViewModelFatcory(MainRepository(dataAccessObj), application)
        )[TeamViewModel::class.java]

    }

    private fun refreshAllData() {
        binding.progressBar.visibility = View.VISIBLE
        val totalCollections = 2 // total collections you are refreshing
        var completedCollections = 0

        fun checkAndHideProgress() {
            completedCollections++
            if (completedCollections == totalCollections) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "All data refreshed successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch daily_routines
        FirebaseFetchHelper.fetchDataIfNeeded<AddDailyRoutine>(
            context = this,
            db = db,
            collectionName = "daily_routines",
            onInsert = { item ->
                addTaskViewModel.insertDailyRoutine(item)
            },
            onDeleteAll = {
                addTaskViewModel.deleteAllDailyRoutine()
            },
            onComplete = {
                checkAndHideProgress()
            }
        )

        // Fetch distributors
        FirebaseFetchHelper.fetchDataIfNeeded<Distributor>(
            context = this,
            db = db,
            collectionName = "distributors",
            onInsert = { item ->
                teamViewModel.insertDistributor(item)
            },
            onDeleteAll = {
                teamViewModel.deleteAllDistributors()
            },
            onComplete = {
                checkAndHideProgress()
            }
        )
    }

    private fun clickListeiner() {
        /*TODO Sync*/
        binding.imageView.setOnClickListener {
            /*  refreshAllData()*/
            showAccountBottomSheet(this@DashBoardScreen)
        }
    }

    fun showAccountBottomSheet(context: Context) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.custom_layout_switch_profile, null)
        val container = view.findViewById<LinearLayout>(R.id.accountList)

        val usernames =
            listOf("the_akashdreamer", "dreamwithsakash", "the_statuspro", "technohomey")
        val profilePics = listOf(
            R.drawable.plus_icon, R.drawable.ic_baseline_person_24,
            R.drawable.sun_icon, R.drawable.plus_icon
        )
        val selectedPosition = 0 // example

        usernames.forEachIndexed { index, username ->
            val item =
                LayoutInflater.from(context).inflate(R.layout.item_account_row, container, false)
            val usernameView = item.findViewById<TextView>(R.id.username)
            val profileView = item.findViewById<ImageView>(R.id.profilePic)
            val checkmark = item.findViewById<ImageView>(R.id.checkmark)

            usernameView.text = username
            profileView.setImageResource(profilePics[index])

            checkmark.visibility = if (index == selectedPosition) View.VISIBLE else View.GONE

            item.setOnClickListener {
                Toast.makeText(context, "Switched to $username", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            container.addView(item)
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun DateAndTimeWork() {
        binding.TimeCurrent.format12Hour = "hh:mm aa"
        val currentDate = System.currentTimeMillis()
        // Format date with "month day year" pattern
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(currentDate))
        binding.monthCurrent.text = formattedDate
    }

    private fun setAdapterData(dashboardModule: DashBoardModule) {
        val adapter = CustomAdapterForDash(
            this@DashBoardScreen, dashboardModule,
        )
        binding.gridLayout.adapter = adapter
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Security Alert")
            .setContentText("Your phone was moved or touched!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Motion Alert"
            val descriptionText = "Notifies when the phone is moved"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(sensorListener)
    }

    private fun waveAnimation(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 0.95f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f, 0.95f, 1f)
        val rotation = ObjectAnimator.ofFloat(view, "rotation", -2f, 2f, -1f, 1f, 0f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.9f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, rotation, alpha)
            duration = 1200
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun checkNetworkType(status: NetworkMonitor.Status) {
        when (status.type) {
            NetworkMonitor.Status.Type.AVAILABLE -> {
                binding.tvNetworkStatus.text = "Network Available ✅"
                stopWaveAnimation()
            }

            NetworkMonitor.Status.Type.UNAVAILABLE -> {
                binding.tvNetworkStatus.text = "Network Unavailable ❌"
                startWaveAnimation()
            }

            NetworkMonitor.Status.Type.LOSING -> {
                binding.tvNetworkStatus.text =
                    "Network Losing… will drop in ${status.maxMsToLive} ms ⚠️"
                startWaveAnimation()
            }

            NetworkMonitor.Status.Type.LOST -> {
                binding.tvNetworkStatus.text = "Network Lost ❌"
                startWaveAnimation()
            }
        }
    }

    private fun startWaveAnimation() {
        // Prevent multiple instances
        if (waveAnimator?.isRunning == true) return

        val scaleX = ObjectAnimator.ofFloat(binding.tvNetworkStatus, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.tvNetworkStatus, "scaleY", 1f, 1.2f, 1f)
        val alpha = ObjectAnimator.ofFloat(binding.tvNetworkStatus, "alpha", 1f, 0.6f, 1f)

        waveAnimator = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            // 🔥 Repeat forever like fire/waves
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // restart automatically
                    start()
                }
            })
            start()
        }
    }

    private fun stopWaveAnimation() {
        waveAnimator?.cancel()
        binding.tvNetworkStatus.scaleX = 1f
        binding.tvNetworkStatus.scaleY = 1f
        binding.tvNetworkStatus.alpha = 1f
        waveAnimator = null
    }

}