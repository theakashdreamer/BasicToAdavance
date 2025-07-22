package com.skysoftsolution.basictoadavance.goalModule

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.DashBoardModule
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.ModuleForUse
import com.skysoftsolution.thingisbeing.dashBoard.dashboardutils.DashBoardViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.dashBoardScreens.callbacks.AdapterClickListener
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.adapters.CustomAdapterForDash
import com.skysoftsolution.basictoadavance.databinding.ActivityGoalDashBoardBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoalDashBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalDashBoardBinding
    private val viewModel: DashBoardViewModel = DashBoardViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set Action Bar Title
        supportActionBar?.title = "Goal Dashboard"
        Glide.with(this)
            .asGif()
            .load(R.drawable.gif_goaldash)
            .into(object : CustomTarget<GifDrawable>() {
                override fun onResourceReady(
                    resource: GifDrawable,
                    transition: Transition<in GifDrawable>?
                ) {
                    binding.draggableView.background = resource
                    resource.start() // Ensure the GIF animates
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.draggableView.background = placeholder
                }
            })
        DateAndTimeWork()
        viewModel.userList.observe(this@GoalDashBoardActivity, Observer { userList ->
            setAdapterData(userList)
        })


        val newUser1 = ModuleForUse(7, "Set", R.drawable.daily_routine)
        viewModel.addModule(newUser1)
        val newUser = ModuleForUse(8, "Track", R.drawable.goalsetting)
        viewModel.addModule(newUser)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
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
        val adapter = CustomAdapterForDash(this@GoalDashBoardActivity, dashboardModule)
        binding.gridLayout.adapter = adapter
    }

}