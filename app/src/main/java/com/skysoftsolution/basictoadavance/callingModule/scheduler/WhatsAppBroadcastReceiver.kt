package com.skysoftsolution.basictoadavance.callingModule.scheduler
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class WhatsAppBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val phoneNumber = intent?.getStringExtra("phoneNumber")
        val message = intent?.getStringExtra("message")

        if (phoneNumber != null && message != null) {
            sendWhatsAppMessage(context, phoneNumber, message)
        }
    }

    private fun sendWhatsAppMessage(context: Context, phoneNumber: String, message: String) {
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
        val whatsappIntent = Intent(Intent.ACTION_VIEW, uri)
        whatsappIntent.setPackage("com.whatsapp.w4b") // WhatsApp Business

        whatsappIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Ensures it starts properly

        try {
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp Business not installed!", Toast.LENGTH_SHORT).show()
        }
    }
}
