package com.example.parttracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.parttracker.model.LocationCount
import com.example.parttracker.model.PartCountByLocation
import com.example.parttracker.model.PartQuantityCount
import com.example.parttracker.model.PlanEntry
import com.example.parttracker.model.ScannedPart
import com.example.parttracker.model.UsedPartCount

@Dao
interface ScannedPartDao {


    @Query("""
    SELECT * FROM scanned_part_table
    WHERE partName = :partName AND productId = :productId AND trolleyName = :trolleyName AND
          trolleyNumber = :trolleyNumber AND location = :location AND model = :model AND
          color = :color AND date = :date AND shift = :shift
    LIMIT 1
""")
    suspend fun getExistingScan(
        partName: String,
        productId: String,
        trolleyName: String,
        trolleyNumber: String,
        location: String,
        model: String,
        color: String,
        date: String,
        shift: String
    ): ScannedPart?

    @Query("""
    SELECT * FROM scanned_part_table
    WHERE partName = :partName AND productId = :productId AND trolleyName = :trolleyName AND
          trolleyNumber = :trolleyNumber AND location = :location AND model = :model AND
          color = :color AND date = :date AND shift = :shift AND ABS(timestamp - :timestamp) < 2000
    LIMIT 1
""")
    suspend fun getExistingScanWithTolerance(
        partName: String,
        productId: String,
        trolleyName: String,
        trolleyNumber: String,
        location: String,
        model: String,
        color: String,
        date: String,
        shift: String,
        timestamp: Long
    ): ScannedPart?


    @Transaction
    suspend fun upsert(part: ScannedPart) {
        val existing = getExistingScan(
            part.partName,
            part.productId,
            part.trolleyName,
            part.trolleyNumber,
            part.location,
            part.model,
            part.color,
            part.date,
            part.shift
        )

        if (existing == null) {
            insertPart(part)
        } else {
            val updated = existing.copy(quantity = existing.quantity + part.quantity)
            update(updated)
        }
    }




//    @Insert
//    suspend fun insert(part: ScannedPart)

    @Update
    suspend fun update(part: ScannedPart)

//    @Transaction
//    suspend fun upsert(part: ScannedPart) {
//        val existing = getExistingScan(
//            part.partName,
//            part.productId,
//            part.trolleyName,
//            part.trolleyNumber,
//            part.location,
//            part.model,
//            part.color,
//            part.date,
//            part.shift
//        )
//
//        if (existing == null) {
//            insert(part)
//        } else {
//            val updated = existing.copy(quantity = existing.quantity + part.quantity)
//            update(updated)
//        }
//    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPart(part: ScannedPart)




    @Delete
    suspend fun deletePart(part: ScannedPart)

    @Query("SELECT * FROM scanned_part_table")
    suspend fun getAllParts(): List<ScannedPart>

    @Query("SELECT * FROM scanned_part_table WHERE location = :location")
    suspend fun getPartsByLocation(location: String): List<ScannedPart>

    @Query("SELECT COUNT(*) FROM scanned_part_table WHERE location = :location")
    fun getCountByLocation(location: String): LiveData<Int>

    @Query("SELECT partName, SUM(quantity) as count FROM scanned_part_table WHERE location = :location GROUP BY partName")
    fun getPartCountsByLocation(location: String): LiveData<List<PartQuantityCount>>

    @Query("SELECT partName, COUNT(*) as count FROM scanned_part_table WHERE location = 'Assembly' GROUP BY partName")
    fun getUsedCountByPart(): LiveData<List<UsedPartCount>>

    @Query("SELECT * FROM scanned_part_table ORDER BY timestamp DESC")
    suspend fun getAllScannedParts(): List<ScannedPart>

    @Query("SELECT SUM(quantity) FROM scanned_part_table WHERE location = 'CTL' AND partName = :partName")
    fun getTotalArrivedParts(partName: String): LiveData<Int>


    @Query("SELECT location, COUNT(*) as count FROM scanned_part_table GROUP BY location")
    fun getPartCountsByLocation(): LiveData<List<LocationCount>>


    @Query("SELECT COUNT(*) FROM scanned_part_table WHERE location = :location AND partName = :partName")
    fun getCountByLocationAndPartName(location: String, partName: String): LiveData<Int>


    @Query("SELECT SUM(quantity) FROM scanned_part_table WHERE location = :location")
    fun getTotalQuantityByLocation(location: String): LiveData<Int>

    @Query("SELECT SUM(quantity) FROM scanned_part_table WHERE location = :location AND partName = :partName")
    fun getQuantityByLocationAndPartName(location: String, partName: String): LiveData<Int>

    @Query("SELECT partName, SUM(quantity) as count FROM scanned_part_table WHERE location = :location GROUP BY partName")
    fun getPartBreakdownByLocation(location: String): LiveData<List<PartCountByLocation>>

    @Query("SELECT EXISTS(SELECT 1 FROM scanned_part_table WHERE productId = :productId AND timestamp = :timestamp)")
    suspend fun checkIfExists(productId: String, timestamp: Long): Boolean



    @Query("DELETE FROM scanned_part_table")
    suspend fun deleteAllScannedParts()

    @Query("SELECT * FROM scanned_part_table WHERE partName = :partName AND location = :location LIMIT :limit")
    suspend fun getPartsByNameAndLocation(partName: String, location: String, limit: Int = 100): List<ScannedPart>


    @Query("SELECT DISTINCT partName FROM scanned_part_table WHERE location = :location")
    fun getDistinctPartNamesByLocation(location: String): LiveData<List<String>>

//    @Query("SELECT COUNT(*) FROM scanned_part_table WHERE location = :location AND partName = :partName")
//    suspend fun getCountByLocationAndPartNameNow(location: String, partName: String): Int

    @Query("SELECT SUM(quantity) FROM scanned_part_table WHERE trolleyName = :location AND partName = :partName")
    suspend fun getQuantityByLocationAndPartNameNow(location: String, partName: String): Int?

    @Query("SELECT partName, SUM(quantity) AS count FROM scanned_part_table WHERE location = :location GROUP BY partName")
    fun getGroupedCountsByLocation(location: String): LiveData<List<PartCountByLocation>>

    @Query("SELECT IFNULL(SUM(quantity), 0) FROM scanned_part_table WHERE location = :location AND partName = :partName")
    fun getCountByLocationAndPartNameNow(location: String, partName: String): Int

    @Query("SELECT * FROM plan_table WHERE date = :date AND shift = :shift AND color = :color LIMIT 1")
    suspend fun getPlanByDateShiftAndColor(date: String, shift: String, color: String): PlanEntry?

    @Query("SELECT * FROM plan_table WHERE date = :date AND shift = :shift")
    suspend fun getPlansByDateAndShift(date: String, shift: String): List<PlanEntry>

    @Query("SELECT * FROM scanned_part_table WHERE sequenceNumber IS NULL")
    suspend fun getUnmatchedScans(): List<ScannedPart>

    @Query("UPDATE scanned_part_table SET sequenceNumber = :sequence WHERE id = :id")
    suspend fun updateSequenceForPart(id: Int, sequence: Int)

    @Query("SELECT * FROM scanned_part_table WHERE model = :model AND color = :color AND date = :date AND shift = :shift")
    suspend fun getPartsByModelColorDateShift(model: String, color: String, date: String, shift: String): List<ScannedPart>

    @Query("""
    SELECT * FROM scanned_part_table
    WHERE date = :date
    ORDER BY timestamp DESC
""")
    suspend fun getPartsByDate(date: String): List<ScannedPart>

    @Query("DELETE FROM scanned_part_table WHERE timestamp < :cutoff")
    suspend fun deletePartsOlderThan(cutoff: Long)


//    @Query("SELECT * FROM scanned_part_table WHERE model = :model AND color = :color AND date = :date AND shift = :shift AND countedInDashboard = 0")
//    suspend fun getUncountedParts(model: String, color: String, date: String, shift: String): List<ScannedPart>
//
//    @Update
//    suspend fun updateParts(parts: List<ScannedPart>)










    @Query("""
    SELECT IFNULL(SUM(quantity), 0)
    FROM scanned_part_table
    WHERE location = :location
      AND partName = :partName
      AND date = :date
      AND shift = :shift
      AND sequenceNumber = :sequenceNumber
""")
    suspend fun getCountByLocationDateShiftAndSequence(
        location: String,
        partName: String,
        date: String,
        shift: String,
        sequenceNumber: Int
    ): Int

}
