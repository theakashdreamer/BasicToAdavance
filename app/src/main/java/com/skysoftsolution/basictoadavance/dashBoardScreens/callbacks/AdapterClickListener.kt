package com.skysoftsolution.basictoadavance.dashBoardScreens.callbacks
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.DashBoardModule
import com.skysoftsolution.basictoadavance.dashBoardScreens.entity.ModuleForUse

interface AdapterClickListener {
    fun onClickListener(dashBoardModule: DashBoardModule)
}