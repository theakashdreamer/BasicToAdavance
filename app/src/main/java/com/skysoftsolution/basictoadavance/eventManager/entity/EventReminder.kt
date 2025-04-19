package com.skysoftsolution.basictoadavance.eventManager.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_reminders")
data class EventReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val speakerName: String?,
    val cityName: String?,
    val eventTime: String,
    val isRecurring: String
)

