package com.skysoftsolution.basictoadavance.taskDetails.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTaskViewModel(private val repository: MainRepository, application: Application) :
    AndroidViewModel(application) {

    val progress = MutableLiveData<Boolean>()
    val success = MutableLiveData<AddDailyRoutine>()
    val error = MutableLiveData<String>()
    val filteredRoutines = MutableLiveData<List<AddDailyRoutine>>()

    val isActiveFilter = MutableLiveData<Boolean>()

    init {
        isActiveFilter.value = true
    }

    // Get all routines
    fun getAllDailyRoutines(): LiveData<List<AddDailyRoutine>> {
        return repository.getAllDailyRoutines()!!
    }
    fun getTodayRoutines(): LiveData<List<AddDailyRoutine>> {
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        return repository.getTodayRoutines(todayDate)
    }
    // Get all upcoming routines
    fun getAllUpcomingRoutines(): LiveData<List<AddDailyRoutine>> {
        return repository.getAllUpcomingRoutines()!!
    }

    fun getSevenDaysRoutines(): LiveData<List<AddDailyRoutine>> {
        val today = Calendar.getInstance()
        val sdfDatabaseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = sdfDatabaseFormat.format(today.time)
        today.add(Calendar.DAY_OF_YEAR, 6)
        val endDate = sdfDatabaseFormat.format(today.time)
        return repository.getRoutinesForNextSevenDays(startDate, endDate)
    }


    // Delete all routines
/*
    fun deleteAllDailyRoutines() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteAllDailyRoutines()
            } catch (e: Exception) {
                error.postValue("Failed to delete all: ${e.message}")
            }
        }
    }
*/

    // Insert a new routine
    fun insertDailyRoutine(routine: AddDailyRoutine) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                progress.postValue(true)
                repository.insertDailyRoutine(routine)
                success.postValue(routine)  // Notify success
            } catch (e: Exception) {
                error.postValue("Failed to insert: ${e.message}")
            } finally {
                progress.postValue(false)
            }
        }
    }
    fun deleteAllDailyRoutine() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteAllDailyRoutines()
            } catch (e: Exception) {
                error.postValue("Failed to delete all: ${e.message}")
            }
        }
    }
/*    // Filter routines by completed or not
    fun filterRoutinesByStatus(isCompleted: Boolean) {
        isActiveFilter.value = isCompleted
        viewModelScope.launch(Dispatchers.IO) {
            val routines = if (isCompleted) {
                repository.getCompletedDailyRoutines()
            } else {
                repository.getPendingDailyRoutines()
            }
            withContext(Dispatchers.Main) {
                filteredRoutines.postValue(routines)
            }
        }
    }

    // Search routines by title or description
    fun searchRoutines(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val routines = repository.searchDailyRoutine(query)
            withContext(Dispatchers.Main) {
                filteredRoutines.postValue(routines)
            }
        }
    }*/

    // Update a routine (for example marking it complete)
    fun updateRoutineStatus(routine: AddDailyRoutine) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateRoutineStatus(routine)
            } catch (e: Exception) {
                error.postValue("Failed to update: ${e.message}")
            }
        }
    }
}
