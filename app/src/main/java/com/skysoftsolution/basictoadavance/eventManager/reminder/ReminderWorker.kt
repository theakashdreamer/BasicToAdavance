package com.skysoftsolution.basictoadavance.eventManager.reminder

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.skysoftsolution.basictoadavance.R


class ReminderWorker(private val context:Context,params:WorkerParameters):Worker(context,params) {
    override fun doWork(): Result {
        playAlarmTone();
        NotificationHelper(context).createNotification(
            inputData.getString("Title").toString(),
            inputData.getString("Message").toString()
        )
        return Result.success()
    }
    private fun playAlarmTone() {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val mediaPlayer = MediaPlayer.create(applicationContext, uri)

            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )

            mediaPlayer.isLooping = false
            var playCount = 0

            mediaPlayer.setOnCompletionListener {
                playCount++
                if (playCount < 3) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.release()
                }
            }

            mediaPlayer.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}