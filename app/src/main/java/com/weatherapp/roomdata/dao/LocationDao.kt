package com.weatherapp.roomdata.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.weatherapp.roomdata.dataclass.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    @Update
    suspend fun updateLocation(location: List<Location>)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("SELECT * FROM location_table ORDER BY locationName ASC")
    fun getLocation(): Flow<List<Location>>

    @Query("SELECT * FROM location_table WHERE isSelected = 1 LIMIT 1")
    fun getSelectedLocation(): Flow<Location?>

    @Query("SELECT * FROM location_table WHERE uId = :uId LIMIT 1")
    fun getLocationByUid(uId: Int): Flow<Location?>
}