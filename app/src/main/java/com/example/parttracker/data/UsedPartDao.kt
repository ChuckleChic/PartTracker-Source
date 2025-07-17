//package com.example.parttracker.data
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.example.parttracker.model.UsedPartCount
//
//@Dao
//interface UsedPartDao {
//
//    @Insert
//    suspend fun insertUsedPart(part: UsedPart)
//
//
//
//    @Query("SELECT * FROM used_parts ORDER BY timestamp DESC")
//    fun getAllUsedParts(): LiveData<List<UsedPart>>
//
//    @Query("SELECT SUM(quantity) FROM used_parts WHERE partName = :partName")
//    fun getTotalUsedParts(partName: String): LiveData<Int>
//
//
//    @Query("SELECT partName, SUM(quantity) AS count  FROM used_parts GROUP BY partName")
//    fun getUsedCountByPart(): LiveData<List<UsedPartCount>>
//
//
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(usedPart: UsedPart)
//
//    @Query("SELECT partName, SUM(quantity) as count FROM used_parts GROUP BY partName")
//    fun getUsedBreakdown(): LiveData<List<UsedPartCount>>
//
//    @Query("DELETE FROM used_parts")
//    suspend fun deleteAllUsedParts()
//
//
//
//
//
//
//
//
//
//
//
//}
//
//


package com.example.parttracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.parttracker.model.UsedPartCount

@Dao
interface UsedPartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsedPart(part: UsedPart)

    @Query("SELECT * FROM used_parts ORDER BY timestamp DESC")
    fun getAllUsedParts(): LiveData<List<UsedPart>>

    @Query("SELECT SUM(quantity) FROM used_parts WHERE partName = :partName")
    fun getTotalUsedParts(partName: String): LiveData<Int>

    @Query("SELECT partName, SUM(quantity) AS count FROM used_parts GROUP BY partName")
    fun getUsedCountByPart(): LiveData<List<UsedPartCount>>

    @Query("SELECT partName, SUM(quantity) AS count FROM used_parts GROUP BY partName")
    fun getUsedBreakdown(): LiveData<List<UsedPartCount>>

    @Query("DELETE FROM used_parts")
    suspend fun deleteAllUsedParts()


}
