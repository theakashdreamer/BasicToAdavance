package com.skysoftsolution.basictoadavance.teamModules.viewModel

import android.app.Application
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeamViewModel(private val repository: MainRepository, application: Application) :
    AndroidViewModel(application) {

    val progress = MutableLiveData<Boolean>()
    val success = MutableLiveData<Distributor>()
    val error = MutableLiveData<String>()

    val filteredDistributors = MutableLiveData<List<Distributor>>()

    val isActiveFilter = MutableLiveData<Boolean>()

    init {
        isActiveFilter.value = true
    }

    fun getAllDistributors(): LiveData<List<Distributor>> {
        return repository.getAllDistributors()!!
    }

    fun insertDistributor(distributor: Distributor) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                progress.postValue(true)
                repository.insertDistributor(distributor)
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
                repository.getActiveDistributors()
            } else {
                repository.getInactiveDistributors()
            }
            withContext(Dispatchers.Main) {
                filteredDistributors.postValue(distributors)
            }
        }
    }

    fun searchDistributors(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val distributors = repository.searchDistributors(query)
            withContext(Dispatchers.Main) {
                filteredDistributors.postValue(distributors)
            }
        }
    }

    fun updateDistributorStatus(distributor: Distributor) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateDistributorStatus(distributor)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    fun deleteAllDistributors() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteAllDistributors()
            } catch (e: Exception) {
                error.postValue("Failed to delete all: ${e.message}")
            }
        }
    }

}
