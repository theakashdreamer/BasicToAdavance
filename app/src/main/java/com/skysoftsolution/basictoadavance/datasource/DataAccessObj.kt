package com.skysoftsolution.basictoadavance.datasource

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.skysoftsolution.basictoadavance.callObserver.entity.CallLog
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
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

    @Query("DELETE FROM event_reminders")
    fun deleteAllEventReminder()

    @Update
    fun updateReminder(reminder: EventReminder)

    @Delete
    fun deleteReminder(reminder: EventReminder)

    @Query("SELECT * FROM event_reminders ORDER BY eventTime DESC")
    fun getAllReminders(): LiveData<List<EventReminder>>

    @Query(
        """
SELECT * FROM event_reminders
ORDER BY 
    CASE 
        WHEN eventTime >= :currentDateTime THEN 0
        ELSE 1
    END,
    ABS(strftime('%s', eventTime) - strftime('%s', :currentDateTime))
"""
    )
    fun getRemindersSortedByNearest(currentDateTime: String): LiveData<List<EventReminder>>

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

    /*TODO Daily Routine Activity
    * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoutine(routine: AddDailyRoutine)

    @Update
    fun updateRoutine(routine: AddDailyRoutine)

    @Delete
    fun deleteRoutine(routine: AddDailyRoutine)

    @Query("DELETE FROM daily_routines")
    fun deleteAlldaily_routines()

    @Query("SELECT * FROM daily_routines WHERE event_date LIKE :todayDate || '%' ORDER BY event_date ASC")
    fun getTodayRoutines(todayDate: String): LiveData<List<AddDailyRoutine>>

    @Query("SELECT * FROM daily_routines ORDER BY event_date ASC, event_time ASC")
    fun getAllRoutines(): LiveData<List<AddDailyRoutine>>

    @Query("SELECT * FROM daily_routines WHERE routine_id = :routineId LIMIT 1")
    fun getRoutineById(routineId: Int): AddDailyRoutine?

    @Query("UPDATE daily_routines SET completed = 1 WHERE routine_id = :routineId")
    fun markRoutineAsCompleted(routineId: Int)

    @Query("SELECT * FROM daily_routines WHERE completed = 0")
    fun getAllUpcomingRoutines(): LiveData<List<AddDailyRoutine>>

    @Update
    fun updateRoutineStatus(dailyRoutine: AddDailyRoutine)

    /*    @Query("SELECT * FROM daily_routines WHERE event_date BETWEEN :startDate AND :endDate ORDER BY event_date ASC")
        fun getRoutinesBetweenDates(startDate: Date, endDate: Date): LiveData<List<AddDailyRoutine>>*/
    /*    @Query("SELECT * FROM daily_routines WHERE event_date BETWEEN :startDate AND :endDate ORDER BY event_date ASC")
        fun getRoutinesBetweenDates(startDate: Date, endDate: Date): LiveData<List<AddDailyRoutine>>*/
    @Query("""SELECT * FROM daily_routines WHERE substr(created_at, 1, 10) BETWEEN :startDate AND :endDate ORDER BY event_date ASC""")
    fun getRoutinesBetweenDates(startDate: String, endDate: String): LiveData<List<AddDailyRoutine>>
    @Insert
     fun insertCallLog(callLog: CallLog)

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
     fun getAllLogs(): LiveData<List<CallLog>>
}