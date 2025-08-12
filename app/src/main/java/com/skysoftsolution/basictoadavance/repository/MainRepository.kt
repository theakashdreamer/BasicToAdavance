package com.skysoftsolution.basictoadavance.repository

import androidx.lifecycle.LiveData
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor
import java.util.Calendar

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

    fun getRemindersSortedByNearest(query: String) = dataAccessObj?.getRemindersSortedByNearest(query)

    fun getAllUpComingReminders() = dataAccessObj?.getAllUpComingReminders()


    suspend fun insertEventReminder(distributor: EventReminder) {
        dataAccessObj?.insertReminder(distributor) // Insert operation
    }

    fun deleteAllEventReminder() {
        dataAccessObj?.deleteAllEventReminder()
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
    /*TODO Event Work*/
    /*TODO Daily Routine Work*/
    fun getTodayRoutines(todayDate: String): LiveData<List<AddDailyRoutine>> {
        return dataAccessObj?.getTodayRoutines(todayDate)!!
    }
    fun getAllDailyRoutines() = dataAccessObj?.getAllRoutines()

    fun getRoutinesForNextSevenDays(startDate: String, endDate: String): LiveData<List<AddDailyRoutine>> {
        return dataAccessObj?.getRoutinesBetweenDates(startDate, endDate)!!
    }

    fun getAllUpcomingRoutines() = dataAccessObj?.getAllUpcomingRoutines()

    fun insertDailyRoutine(routine: AddDailyRoutine) {
        dataAccessObj?.insertRoutine(routine) // Insert operation
    }

    fun deleteAllDailyRoutines(routine : AddDailyRoutine) {
        dataAccessObj?.deleteRoutine(routine = routine)
    }
    fun deleteAllDailyRoutines() {
        dataAccessObj?.deleteAlldaily_routines()
    }
    // Get a routine by its ID
    fun getRoutineById(routineId: Int): AddDailyRoutine? {
        return dataAccessObj?.getRoutineById(routineId)
    }

    // Mark routine as completed
    fun markRoutineAsCompleted(routineId: Int) {
        dataAccessObj?.markRoutineAsCompleted(routineId)
    }

    fun updateRoutineStatus(routine: AddDailyRoutine) {
        try {
            // Update distributor status in the database
            dataAccessObj!!.updateRoutineStatus(routine)
        } catch (e: Exception) {
            throw Exception("Error updating distributor status: ${e.message}")
        }
    }
    /*    // Get completed routines
        fun getCompletedDailyRoutines(): LiveData<List<AddDailyRoutine>> {
            return dataAccessObj.getCompletedDailyRoutines()
        }

        // Get pending routines (not completed)
        fun getPendingDailyRoutines(): LiveData<List<AddDailyRoutine>> {
            return dataAccessObj.getPendingDailyRoutines()
        }

        // Search daily routines by title or description
        fun searchDailyRoutine(query: String): LiveData<List<AddDailyRoutine>> {
            return dataAccessObj.searchDailyRoutine(query)
        }

        // Update routine status
        suspend fun updateDailyRoutineStatus(routine: AddDailyRoutine) {
            dataAccessObj.updateDailyRoutineStatus(routine)
        }*/

}