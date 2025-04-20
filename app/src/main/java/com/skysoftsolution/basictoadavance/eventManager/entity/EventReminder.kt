package com.skysoftsolution.basictoadavance.eventManager.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_reminders")
data class EventReminder(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String = "",
    var speakerName: String = "",
    var cityName: String = "",
    var eventTime: String = "",
    var isRecurring: Boolean = false
)

