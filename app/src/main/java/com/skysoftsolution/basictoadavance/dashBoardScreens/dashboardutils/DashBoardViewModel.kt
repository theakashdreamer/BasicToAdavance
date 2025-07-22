package com.skysoftsolution.thingisbeing.dashBoard.dashboardutils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.DashBoardModule
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.ModuleForUse

class DashBoardViewModel : ViewModel() {
    private val _userList = MutableLiveData<DashBoardModule>()
    val userList: LiveData<DashBoardModule> get() = _userList

    init {
        // Initialize with an empty list of users
        _userList.value = DashBoardModule(emptyList())
    }

    fun addModule(user: ModuleForUse) {
        val currentList = _userList.value?.userList ?: emptyList()
        val updatedList = currentList.toMutableList().apply { add(user) }
        _userList.value = DashBoardModule(updatedList)
    }


}