package com.skysoftsolution.basictoadavance.callingModule.scheduler
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class WhatsAppSchedulerWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val phoneNumber = inputData.getString("phoneNumber") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()

        openWhatsApp(phoneNumber, message)
        return Result.success()
    }

    private fun openWhatsApp(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            applicationContext.startActivity(intent)
            Log.d("WhatsAppScheduler", "Opened WhatsApp for $phoneNumber")
        } catch (e: Exception) {
            Log.e("WhatsAppScheduler", "WhatsApp not installed!", e)
        }
    }
}
