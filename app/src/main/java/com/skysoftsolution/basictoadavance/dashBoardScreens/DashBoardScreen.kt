package com.skysoftsolution.basictoadavance.dashBoardScreens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.adapters.CustomAdapterForDash
import com.skysoftsolution.basictoadavance.databinding.ActivityDashBoardScreenBinding
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.DashBoardModule
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.ModuleForUse
import com.skysoftsolution.thingisbeing.dashBoard.dashboardutils.DashBoardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashBoardScreen : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardScreenBinding
    private lateinit var dashboardmodules: DashBoardModule
    private val viewModel: DashBoardViewModel = DashBoardViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*    Glide.with(this)
                .asGif()
                .load(R.drawable.dashboardgif)  // Your GIF file in res/drawable
                .into(binding.imageViewback)*/

        DateAndTimeWork()
        viewModel.userList.observe(this@DashBoardScreen, Observer { userList ->
            setAdapterData(userList)
        })



        val newUser1 = ModuleForUse(1, "Daily Routine ", R.drawable.daily_routine)
        viewModel.addModule(newUser1)
        val newUser = ModuleForUse(2, "Goals", R.drawable.goalsetting)
        viewModel.addModule(newUser)

        val newUser2 = ModuleForUse(3, "Team", R.drawable.partners)
        viewModel.addModule(newUser2)
        val newUser3 = ModuleForUse(4, "Calling", R.drawable.goalsetting)
        viewModel.addModule(newUser3)
        val newUser4 = ModuleForUse(5, "Music Player", R.drawable.goalsetting)
        viewModel.addModule(newUser4)

        val newUser5 = ModuleForUse(6, "Signature View", R.drawable.goalsetting)
        viewModel.addModule(newUser5)
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
        val adapter = CustomAdapterForDash(this@DashBoardScreen, dashboardModule,
        )
        binding.gridLayout.adapter = adapter
    }

}