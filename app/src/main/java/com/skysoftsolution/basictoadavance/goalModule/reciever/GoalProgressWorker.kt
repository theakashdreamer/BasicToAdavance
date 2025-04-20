package com.skysoftsolution.basictoadavance.goalModule.reciever

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator

class GoalProgressWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val goalDao: DataAccessObj = DataBaseCreator.getInstance(context).dataAccessObj
    override fun doWork(): Result {
        val goalId = inputData.getInt("GOAL_ID", -1)
        if (goalId == -1) return Result.failure()

   /*     val goal = goalDao.getGoalById(goalId) ?: return Result.failure()

        if (goal.progress < 100) {
            // Reschedule the alarm
            AlarmHelper.scheduleGoalAlarm(applicationContext, goalId)
            return Result.success()
        } else {
            // Goal completed, no need for alarm
            return Result.success()
        }*/
        return Result.success()
    }
}
