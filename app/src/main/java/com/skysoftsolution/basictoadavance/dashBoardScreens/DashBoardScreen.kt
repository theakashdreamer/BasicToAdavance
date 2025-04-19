package com.skysoftsolution.basictoadavance.dashBoardScreens
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
import androidx.core.app.NotificationCompat

class DashBoardScreen : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardScreenBinding
    private lateinit var dashboardmodules: DashBoardModule
    private val viewModel: DashBoardViewModel = DashBoardViewModel()
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var sensorListener: SensorEventListener
    private val CHANNEL_ID = "MotionAlert"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*    Glide.with(this)
                .asGif()
                .load(R.drawable.dashboardgif)  // Your GIF file in res/drawable
                .into(binding.imageViewback)*/

        DateAndTimeWork()
        viewModel.userList.observe(this@DashBoardScreen, Observer { userList ->
            setAdapterData(userList)
        })
        createNotificationChannel()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())
                if (acceleration > 15) {  // adjust sensitivity
                    sendNotification()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)


        val newUser1 = ModuleForUse(1, "Daily Routine ", R.drawable.daily_routine)
        viewModel.addModule(newUser1)
        val newUser = ModuleForUse(2, "Goals", R.drawable.goalsetting)
        viewModel.addModule(newUser)

        val newUser2 = ModuleForUse(3, "Team", R.drawable.partners)
        viewModel.addModule(newUser2)

        val newUser4 = ModuleForUse(5, "Event", R.drawable.goalsetting)
        viewModel.addModule(newUser4)

        val newUser3 = ModuleForUse(4, "Calling", R.drawable.goalsetting)
        viewModel.addModule(newUser3)


        val newUser5 = ModuleForUse(6, "Signature View", R.drawable.goalsetting)
        viewModel.addModule(newUser5)

        val newUser6 = ModuleForUse(9, "Chat ", R.drawable.goalsetting)
        viewModel.addModule(newUser6)

        val newUser7 = ModuleForUse(10, "Chat New", R.drawable.goalsetting)
        viewModel.addModule(newUser7)
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
        val adapter = CustomAdapterForDash(this@DashBoardScreen, dashboardModule,
        )
        binding.gridLayout.adapter = adapter
    }
    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Security Alert")
            .setContentText("Your phone was moved or touched!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
}