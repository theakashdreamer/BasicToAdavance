package com.skysoftsolution.basictoadavance.callObserver.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,          // OUTGOING, INCOMING, MISSED
    val number: String?,
    val duration: Long,        // in milliseconds
    val timestamp: Long        // System.currentTimeMillis()
)
