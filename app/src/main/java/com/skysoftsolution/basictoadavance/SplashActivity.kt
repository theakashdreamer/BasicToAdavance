package com.skysoftsolution.basictoadavance

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.skysoftsolution.basictoadavance.dashBoardScreens.DashBoardScreen
import com.skysoftsolution.basictoadavance.motiondetectorServic.MotionDetectService

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        setUi()
    }
    private fun setUi() {
        Handler(Looper.getMainLooper()).postDelayed({
            var intent: Intent? = null
            intent = Intent(this@SplashActivity, DashBoardScreen::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

}