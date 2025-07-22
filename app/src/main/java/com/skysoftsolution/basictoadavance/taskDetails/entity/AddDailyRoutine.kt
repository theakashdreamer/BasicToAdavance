package com.skysoftsolution.basictoadavance.taskDetails.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_routines")
data class AddDailyRoutine(
    @PrimaryKey(autoGenerate = true) var routine_id: Int = 0,
    var routine_title: String = "",
    var routine_description: String = "",
    var event_date: String = "",
    var event_time: String = "",
    var end_time: String = "",
    var recurring: Boolean = false, // ⬅️ no `is_`
    var repeat_type: String = "",
    var notification_time: String = "",
    var completed: Boolean = false, // ⬅️ no `is_`
    var created_at: String = "",
    var updated_at: String = ""
)

