package com.skysoftsolution.basictoadavance.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skysoftsolution.basictoadavance.eventManager.viewModel.EventViewModel
import com.skysoftsolution.basictoadavance.goalModule.viewModel.SetYourViewModel
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.taskDetails.viewModel.AddTaskViewModel
import com.skysoftsolution.basictoadavance.teamModules.viewModel.TeamViewModel

class MainViewModelFatcory() : ViewModelProvider.Factory {
    lateinit var repository: MainRepository
    lateinit var application: Application

    constructor(repository: MainRepository, application: Application) : this() {
        this.repository = repository
        this.application = application
    }

    @Suppress("Unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamViewModel::class.java)) {
            return TeamViewModel(repository, application) as T
        }
        if (modelClass.isAssignableFrom(SetYourViewModel::class.java)) {
            return SetYourViewModel(repository, application) as T
        }
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(repository, application) as T
        }
        if (modelClass.isAssignableFrom(AddTaskViewModel::class.java)) {
            return AddTaskViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}