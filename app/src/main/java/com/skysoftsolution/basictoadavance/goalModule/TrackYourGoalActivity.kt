package com.skysoftsolution.basictoadavance.goalModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack

class TrackYourGoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_your_goal)
        val goal: GoalSetTrack? = intent.getParcelableExtra("goal_object")

        goal?.let {
            // Use goal properties
            Log.d(" ", "Goal Title: ${it.goalTitle}")
        }
    }
}