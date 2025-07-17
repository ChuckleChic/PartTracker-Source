package com.example.parttracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.parttracker.model.DashboardEntry
import com.example.parttracker.model.DashboardRow


@Dao
interface DashboardEntryDao {

    @Query("SELECT * FROM dashboard_table")
    suspend fun getAllRows(): List<DashboardRow>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DashboardEntry)

    @Query("SELECT * FROM dashboard_entry WHERE date = :date")
    fun getEntriesByDate(date: String): LiveData<List<DashboardEntry>>

    @Query("SELECT * FROM dashboard_entry ORDER BY date DESC")
    fun getAllEntries(): LiveData<List<DashboardEntry>>

    @Delete
    suspend fun deleteEntry(entry: DashboardEntry)

//    @Query("SELECT * FROM dashboard_entry WHERE date = :date AND shift = :shift AND model = :model AND partName = :partName LIMIT 1")
//    suspend fun getDashboardRow(date: String, shift: String, model: String, partName: String): DashboardEntry?


    @Query("SELECT * FROM dashboard_table WHERE date = :date AND shift = :shift AND model = :model AND color = :color AND partName = :partName LIMIT 1")
    suspend fun getDashboardRow(
        date: String,
        shift: String,
        model: String,
        color: String,
        partName: String
    ): DashboardRow?

    @Query("SELECT * FROM dashboard_entry WHERE date = :date AND shift = :shift AND model = :model AND partName = :partName LIMIT 1")
    suspend fun getDashboardEntry(date: String, shift: String, model: String, partName: String): DashboardEntry?

    @Update
    suspend fun updateDashboardEntry(entry: DashboardEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDashboardEntry(entry: DashboardEntry)

    @Query("SELECT * FROM dashboard_table")
    fun getAllDashboardRows(): LiveData<List<DashboardRow>>

    @Query("SELECT * FROM dashboard_table WHERE model = :model AND date = :date AND shift = :shift LIMIT 1")
    suspend fun getEntryByDetails(model: String, date: String, shift: String): DashboardRow?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DashboardRow)

    @Update
    suspend fun update(entry: DashboardRow)

    @Query("SELECT * FROM dashboard_table WHERE date = :date AND shift = :shift")
    fun getRows(date: String, shift: String): LiveData<List<DashboardRow>>


    @Query("SELECT ob FROM dashboard_table WHERE date = :date AND shift = :shift AND partName = :partName LIMIT 1")
    suspend fun getOpeningBalance(date: String, shift: String, partName: String): Int?


    @Query("SELECT * FROM dashboard_table")
    suspend fun getAllDashboardRowsList(): List<DashboardRow>


    @Query("SELECT produced FROM dashboard_table WHERE date = :date AND shift = :shift AND partName = :partName LIMIT 1")
    suspend fun getProduced(date: String, shift: String, partName: String): Int?

    @Query("SELECT rejection FROM dashboard_table WHERE date = :date AND shift = :shift AND partName = :partName LIMIT 1")
    suspend fun getRejection(date: String, shift: String, partName: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dashboardRow: DashboardRow)

    @Query("""
    SELECT * FROM dashboard_table 
    WHERE model = :model AND color = :color AND partName = :partName 
      AND (date < :date OR (date = :date AND shift < :shift)) 
    ORDER BY date DESC, shift DESC 
    LIMIT 1
""")
    suspend fun getLastDashboardRowForPart(
        model: String,
        color: String,
        partName: String,
        date: String,
        shift: String
    ): DashboardRow?





    @Query("""
    SELECT * FROM dashboard_table 
    WHERE (date < :date) 
       OR (date = :date AND shift < :shift)
       AND model = :model AND color = :color
    ORDER BY date DESC, shift DESC
    LIMIT 1
""")
    suspend fun getLastDashboardRow(model: String, color: String, date: String, shift: String): DashboardRow?





}
