package com.skysoftsolution.basictoadavance.datasource
import androidx.lifecycle.LiveData
import androidx.room.*
import com.skysoftsolution.basictoadavance.dashBoardScreens.entity.Category
import com.skysoftsolution.basictoadavance.dashBoardScreens.entity.Task
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor

@Dao
interface DataAccessObj {

   /* @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)*/

     /*   @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE taskId = :id")
    suspend fun getTaskById(id: Int): Task?

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    suspend fun getAllTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY categoryName ASC")
    suspend fun getAllCategories(): List<Category> */

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
}