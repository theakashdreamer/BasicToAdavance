package com.skysoftsolution.basictoadavance.goalModule.viewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack
import com.skysoftsolution.basictoadavance.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class SetYourViewModel(private val repository: MainRepository, application: Application) :
    AndroidViewModel(application) {
    val progress = MutableLiveData<Boolean>()
    val success = MutableLiveData<GoalSetTrack>()
    val error = MutableLiveData<String>()
    fun getAllGoalSetTrack(): LiveData<List<GoalSetTrack>> {
        return repository.getAllGoalSetTrack()!!
    }
    fun insertGoalSetTrack(goalSetTrack: GoalSetTrack) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                progress.postValue(true)
                repository.insertGoalSetTrack(goalSetTrack)
                success.postValue(goalSetTrack)  // Notify success
            } catch (e: Exception) {
                error.postValue("Failed to insert: ${e.message}")
            } finally {
                progress.postValue(false)
            }
        }
    }

    fun updateDistributorStatus(goalSetTrack: GoalSetTrack) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateGoalSetTrackProgress(goalSetTrack)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

}