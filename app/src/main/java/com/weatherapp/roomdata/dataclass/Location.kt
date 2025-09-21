package com.weatherapp.roomdata.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val uId: Int = 0,
    val locationName: String,
)