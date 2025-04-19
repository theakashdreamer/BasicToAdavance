package com.skysoftsolution.basictoadavance.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor

class MainRepository() {
    var dataAccessObj: DataAccessObj? = null

    constructor(dataAccessObj: DataAccessObj) : this() {
        this.dataAccessObj = dataAccessObj
    }

     fun getAllDistributors() = dataAccessObj?.getAllDistributors()
    suspend fun insertDistributor(distributor: Distributor) {
        dataAccessObj?.insert(distributor) // Insert operation
    }


     fun getActiveDistributors(): List<Distributor> {
        return dataAccessObj!!.getActiveDistributors()
    }

     fun getInactiveDistributors(): List<Distributor> {
        return dataAccessObj!!.getInactiveDistributors()
    }

     fun searchDistributors(query: String): List<Distributor> {
        return dataAccessObj!!.searchDistributors("%$query%")
    }

     fun updateDistributorStatus(distributor: Distributor) {
        try {
            // Update distributor status in the database
            dataAccessObj!!.updateDistributorStatus(distributor)
        } catch (e: Exception) {
            throw Exception("Error updating distributor status: ${e.message}")
        }
    }
    fun deleteAllDistributors() {
        dataAccessObj?.deleteAllDistributors()
    }

    fun insertGoalSetTrack(goalSetTrack: GoalSetTrack) {
        dataAccessObj?.insertGoalSetTrack(goalSetTrack) // Insert operation
    }

    fun getAllGoalSetTrack() = dataAccessObj?.getAllGoalSetTrack()

    fun updateGoalSetTrackProgress(goalSetTrack: GoalSetTrack) {
        try {
            // Update distributor status in the database
            dataAccessObj!!.updateGoalSetTrackProgress(goalSetTrack)
        } catch (e: Exception) {
            throw Exception("Error updating distributor status: ${e.message}")
        }
    }

    /*TODO Event Work*/

    fun getAllEventReminder() = dataAccessObj?.getAllReminders()
    fun getAllUpComingReminders() = dataAccessObj?.getAllUpComingReminders()


    suspend fun insertEventReminder(distributor: EventReminder) {
        dataAccessObj?.insertReminder(distributor) // Insert operation
    }


    fun getActiveEventReminder(): List<EventReminder> {
        return dataAccessObj!!.getActiveEventReminder()
    }

    fun getInactiveEventReminder(): List<EventReminder> {
        return dataAccessObj!!.getInactiveEventReminder()
    }

    fun searchEventReminder(query: String): List<EventReminder> {
        return dataAccessObj!!.searchsEventReminder("%$query%")
    }

    fun updateEventReminderStatus(distributor: EventReminder) {
        try {
            // Update distributor status in the database
            dataAccessObj!!.updateEventReminderStatus(distributor)
        } catch (e: Exception) {
            throw Exception("Error updating distributor status: ${e.message}")
        }
    }



}