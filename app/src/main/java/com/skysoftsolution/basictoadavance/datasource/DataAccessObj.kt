package com.skysoftsolution.basictoadavance.datasource

import androidx.lifecycle.LiveData
import androidx.room.*
import com.skysoftsolution.basictoadavance.dashBoardScreens.entity.Category
import com.skysoftsolution.basictoadavance.dashBoardScreens.entity.Task
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor

@Dao
interface DataAccessObj {


    @Insert
    fun insert(distributor: Distributor)

    @Query("SELECT * FROM distributor_table WHERE status = '1'")
    fun getAllDistributors(): LiveData<List<Distributor>>

    @Query("SELECT * FROM distributor_table WHERE name LIKE :query")
    fun searchDistributors(query: String): List<Distributor>

    @Query("SELECT * FROM distributor_table WHERE status = '1'")
    fun getActiveDistributors(): List<Distributor>

    @Query("SELECT * FROM distributor_table WHERE status = '0'")
    fun getInactiveDistributors(): List<Distributor>

    @Query("DELETE FROM distributor_table")
    fun deleteAllDistributors()


    @Update
    fun updateDistributorStatus(distributor: Distributor)

    @Insert
    fun insertGoalSetTrack(goalSetTrack: GoalSetTrack)

    @Query("SELECT * FROM goal_table ORDER BY startDate DESC")
    fun getAllGoalSetTrack(): LiveData<List<GoalSetTrack>>

    @Update
    fun updateGoalSetTrackProgress(goalSetTrack: GoalSetTrack)

    @Query("SELECT * FROM goal_table WHERE id = :goalId LIMIT 1")
    fun getGoalById(goalId: Int): GoalSetTrack?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertReminder(reminder: EventReminder): Long



    @Update
    fun updateReminder(reminder: EventReminder)

    @Delete
    fun deleteReminder(reminder: EventReminder)

    @Query("SELECT * FROM event_reminders ORDER BY eventTime ASC")
    fun getAllReminders(): LiveData<List<EventReminder>>

/*    @Query("SELECT * FROM event_reminders WHERE eventTime >= DATE('now') ORDER BY eventTime asc")
    fun getAllUpComingReminders(): LiveData<List<EventReminder>>*/

    @Query("SELECT * FROM event_reminders  ORDER BY eventTime asc")
    fun getAllUpComingReminders(): LiveData<List<EventReminder>>

    @Query("SELECT * FROM event_reminders WHERE id = :id")
    fun getReminderById(id: Int): EventReminder?

    @Query("SELECT * FROM event_reminders WHERE isRecurring = '1'")
    fun getActiveEventReminder(): List<EventReminder>

    @Query("SELECT * FROM event_reminders WHERE isRecurring = '0'")
    fun getInactiveEventReminder(): List<EventReminder>

    @Query("SELECT * FROM event_reminders WHERE title LIKE :query")
    fun searchsEventReminder(query: String): List<EventReminder>

    @Update
    fun updateEventReminderStatus(distributor: EventReminder)

}