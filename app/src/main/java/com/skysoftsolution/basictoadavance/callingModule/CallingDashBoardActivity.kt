package com.skysoftsolution.basictoadavance.callingModule
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivityCallingDashBoardBinding
import java.util.Calendar
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.skysoftsolution.basictoadavance.callingModule.scheduler.WhatsAppBroadcastReceiver
import com.skysoftsolution.basictoadavance.callingModule.scheduler.WhatsAppSchedulerWorker
import java.util.*
import java.util.concurrent.TimeUnit

class CallingDashBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallingDashBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityCallingDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSend.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val message = binding.etMessage.text.toString().trim()

            if (phoneNumber.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please enter phone number and message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //sendWhatsAppBusinessMessage(phoneNumber,message)
            pickTimeAndSchedule()
        }
    }
    private fun sendWhatsAppBusinessMessage(phoneNumber: String, message: String) {
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.whatsapp.w4b") // WhatsApp Business package

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp Business not installed!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun pickTimeAndSchedule() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val currentTime = Calendar.getInstance()
            val scheduledTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
            }

            val delayInMillis = scheduledTime.timeInMillis - currentTime.timeInMillis
            if (delayInMillis > 0) {
                val phoneNumber = binding.etPhoneNumber.text.toString()
                val message = binding.etMessage.text.toString()
                scheduleWhatsAppMessage(this, phoneNumber, message, delayInMillis)
            } else {
                Toast.makeText(this, "Select a future time!", Toast.LENGTH_SHORT).show()
            }
        }, hour, minute, false).show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleWhatsAppMessage(context: Context, phone: String, message: String, delayInMillis: Long) {
        val intent = Intent(context, WhatsAppBroadcastReceiver::class.java).apply {
            putExtra("phoneNumber", phone)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + delayInMillis
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        Toast.makeText(context, "Message Scheduled!", Toast.LENGTH_SHORT).show()
    }

}