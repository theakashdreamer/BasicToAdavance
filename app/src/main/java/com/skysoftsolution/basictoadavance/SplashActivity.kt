package com.skysoftsolution.basictoadavance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.skysoftsolution.basictoadavance.dashBoardScreens.DashBoardScreen

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUi()
    }
    private fun setUi() {
        Handler(Looper.getMainLooper()).postDelayed({
            var intent: Intent? = null
            intent = Intent(this@SplashActivity, DashBoardScreen::class.java)
            startActivity(intent)
            finish()
        }, 100)
    }
}