package com.skysoftsolution.basictoadavance.teamModules.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "distributor_table")
data class Distributor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val cityName: String,
    val mobileNumber: String,
    val selectedLevel: String,
    val distributorId: String,
    var status: String
)
