package com.skysoftsolution.basictoadavance.eventManager.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventViewModel (private val repository: MainRepository, application: Application) :
    AndroidViewModel(application)  {

    val progress = MutableLiveData<Boolean>()
    val success = MutableLiveData<EventReminder>()
    val error = MutableLiveData<String>()

    val filteredDistributors = MutableLiveData<List<EventReminder>>()

    val isActiveFilter = MutableLiveData<Boolean>()

    init {
        isActiveFilter.value = true
    }

    fun getAllEventReminder(): LiveData<List<EventReminder>> {
        return repository.getAllEventReminder()!!
    }

    fun getAllUpComingReminders(): LiveData<List<EventReminder>> {
        return repository.getAllUpComingReminders()!!
    }
    fun deleteAllEventReminder() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteAllEventReminder()
            } catch (e: Exception) {
                error.postValue("Failed to delete all: ${e.message}")
            }
        }
    }
    fun insertEventReminder(distributor: EventReminder) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                progress.postValue(true)
                repository.insertEventReminder(distributor)
                success.postValue(distributor)  // Notify success
            } catch (e: Exception) {
                error.postValue("Failed to insert: ${e.message}")
            } finally {
                progress.postValue(false)
            }
        }
    }

    fun filterDistributorsByStatus(isActive: Boolean) {
        isActiveFilter.value = isActive
        viewModelScope.launch(Dispatchers.IO) {
            val distributors = if (isActive) {
                repository.getActiveEventReminder()
            } else {
                repository.getInactiveEventReminder()
            }
            withContext(Dispatchers.Main) {
                filteredDistributors.postValue(distributors)
            }
        }
    }

    fun searchDistributors(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val distributors = repository.searchEventReminder(query)
            withContext(Dispatchers.Main) {
                filteredDistributors.postValue(distributors)
            }
        }
    }

    fun updateDistributorStatus(distributor: EventReminder) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateEventReminderStatus(distributor)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}