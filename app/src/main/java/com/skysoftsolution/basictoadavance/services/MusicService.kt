package com.skysoftsolution.basictoadavance.services
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.utility.App

class MusicService : Service() {
    companion object {
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
    }
    private lateinit var mediaSession: MediaSessionCompat
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "MusicService")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_PLAY -> playMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_NEXT -> nextTrack()
            ACTION_PREVIOUS -> previousTrack()
        }

        showNotification(isPlaying = action == ACTION_PLAY)
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun playMusic() {

    }
    private fun pauseMusic() {

    }
    private fun nextTrack() {

    }
    private fun previousTrack() {

    }
    private fun showNotification(isPlaying: Boolean) {
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.baseline_play_circle_outline_24,
                "Pause",
                getPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.baseline_play_circle_outline_24,
                "Play",
                getPendingIntent(ACTION_PLAY)
            )
        }

        val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_pause_circle_outline_24)
            .setContentTitle("Now Playing")
            .setContentText("Song Title - Artist Name")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .addAction(NotificationCompat.Action(R.drawable.ic_previous, "Previous", getPendingIntent(ACTION_PREVIOUS)))
            .addAction(playPauseAction)
            .addAction(NotificationCompat.Action(R.drawable.baseline_skip_next_24, "Next", getPendingIntent(ACTION_NEXT)))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(1, 2)) // Show Play/Pause and Next buttons
            .build()

        startForeground(1, notification)
    }
    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                App.CHANNEL_ID,
                App.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music Player Notification Channel"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

}
