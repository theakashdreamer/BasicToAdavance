package com.skysoftsolution.basictoadavance.teamModules.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "distributor_table")
data class Distributor(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String = "",
    var cityName: String = "",
    var mobileNumber: String = "",
    var selectedLevel: String = "",
    var distributorId: String = "",
    var status: String = ""
)



