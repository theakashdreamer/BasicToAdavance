package com.skysoftsolution.basictoadavance.learningAndProductivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.callObserver.CallObserverActivity

class Learning_And_Productivity_Activity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_learning_and_productvitiy)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Start delayed navigation to Call_Observer_Activity
        startDelayedNavigation()
    }

    private fun startDelayedNavigation() {
        handler.postDelayed({
            navigateToCallObserver()
        }, 3000) // 3000 milliseconds = 3 seconds
    }

    private fun navigateToCallObserver() {
        val intent = Intent(this, CallObserverActivity::class.java)
        startActivity(intent)

        // Optional: finish current activity if you don't want to come back
        // finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }
}