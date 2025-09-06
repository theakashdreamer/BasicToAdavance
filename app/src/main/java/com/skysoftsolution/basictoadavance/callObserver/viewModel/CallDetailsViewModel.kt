package com.skysoftsolution.basictoadavance.callObserver.viewModel

import android.app.Application
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skysoftsolution.basictoadavance.callObserver.entity.CallLog
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CallDetailsViewModel(private val repository: MainRepository, application: Application) :
    AndroidViewModel(application) {

    val progress = MutableLiveData<Boolean>()
    val success = MutableLiveData<CallLog>()
    val error = MutableLiveData<String>()
    val isActiveFilter = MutableLiveData<Boolean>()

    init {
        isActiveFilter.value = true
    }

    fun getAllCallLog(): LiveData<List<CallLog>> {
        return repository.getAllCallLog()!!
    }

    fun insertCallLog(callLog: CallLog) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                progress.postValue(true)
                repository.insertCallLog(callLog)
                success.postValue(callLog)  // Notify success
            } catch (e: Exception) {
                error.postValue("Failed to insert: ${e.message}")
            } finally {
                progress.postValue(false)
            }
        }
    }


}
