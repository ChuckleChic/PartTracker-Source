package com.example.parttracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.parttracker.model.DashboardEntry
import com.example.parttracker.model.DashboardRow
import com.example.parttracker.model.PlanEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(planEntry: PlanEntry)
//
//    @Query("SELECT * FROM plan_table WHERE date = :date AND shift = :shift")
//    suspend fun getTodayPlans(date: String, shift: String): List<PlanEntry>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertPlan(plan: PlanEntry)

    @Query("SELECT * FROM plan_table ORDER BY date DESC, sequence ASC")
    fun getAllPlans(): LiveData<List<PlanEntry>>



    @Query("SELECT * FROM plan_table ORDER BY date DESC, sequence ASC")
    suspend fun getAllPlansRaw(): List<PlanEntry>  // ✅ plain List

    @Query("SELECT * FROM plan_table")
    suspend fun getAllPlansNow(): List<PlanEntry>




    @Query("SELECT * FROM plan_table ORDER BY date DESC, sequence ASC")
    fun getAllPlansFlow(): Flow<List<PlanEntry>>


    @Query("SELECT COUNT(*) FROM plan_table WHERE date = :date AND shift = :shift AND sequence = :sequence")
    suspend fun countSequenceForDateShift(date: String, shift: String, sequence: Int): Int

//    @Query("UPDATE plan_table SET completed = :completed WHERE id = :id")
//    suspend fun updateCompletionStatus(id: Int, completed: Boolean)



    @Delete
    suspend fun deletePlan(plan: PlanEntry)


    @Query("DELETE FROM plan_table WHERE model = :model AND date = :date AND shift = :shift")
    suspend fun deletePlan(model: String, date: String, shift: String)


    @Query("SELECT * FROM plan_table WHERE date = :date AND shift = :shift")
    suspend fun getTodayPlans(date: String, shift: String): List<PlanEntry>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(plan: PlanEntry)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: PlanEntry)

    @Query("SELECT * FROM plan_table WHERE date = :date AND shift = :shift ORDER BY sequence ASC")
    fun getPlansForDateShift(date: String, shift: String): LiveData<List<PlanEntry>>


    @Query("SELECT * FROM plan_table WHERE date = :date AND shift = :shift")
    fun getPlans(date: String, shift: String): LiveData<List<PlanEntry>>

    @Query("SELECT * FROM plan_table WHERE date = :date AND shift = :shift AND color = :color LIMIT 1")
    suspend fun getPlanByDateShiftAndColor(date: String, shift: String, color: String): PlanEntry?

    @Query("SELECT * FROM plan_table WHERE date = :date AND shift = :shift")
    suspend fun getPlansByDateAndShift(date: String, shift: String): List<PlanEntry>

//    @Query("SELECT * FROM plan_table WHERE model = :model AND color = :color AND date = :date AND shift = :shift AND sequence = :sequence LIMIT 1")
//    suspend fun getPlan(model: String, color: String, date: String, shift: String, sequence: Int): PlanEntry?


    @Query("SELECT * FROM plan_table WHERE model = :model AND color = :color AND date = :date AND shift = :shift AND sequence = :sequence LIMIT 1")
    suspend fun getPlan(model: String, color: String, date: String, shift: String, sequence: Int): PlanEntry?


    @Query("SELECT * FROM plan_table WHERE model = :model AND color = :color AND date = :date AND shift = :shift LIMIT 1")
    suspend fun getPlanByModelColorDateShift(
        model: String,
        color: String,
        date: String,
        shift: String
    ): PlanEntry?









    @Query("""
        SELECT
            d.id AS id,  -- Corrected line: Select id from dashboard_entry
            p.date AS date,
            p.shift AS shift,
            p.model AS model,
            p.color AS color,
            d.partName AS partName,
            p.quantity AS planned,
            d.openingBalance AS openingBalance,
            0 AS dispatch,
            0 AS received,
            0 AS remainingPs,
            0 AS remainingVa,
            d.produced AS produced,
            d.rejection AS rejection,
            (d.openingBalance + d.produced - d.rejection) AS cb
        FROM plan_table p
        JOIN dashboard_entry d
            ON p.date = d.date
            AND p.shift = d.shift
            AND p.model = d.model
            AND p.color = d.color
    """)
    fun getAllDashboardRowsFlow(): Flow<List<DashboardEntry>>

    @Query("""
        SELECT
            d.id AS id,  -- Corrected line: Select id from dashboard_entry
            p.date AS date,
            p.shift AS shift,
            p.model AS model,
            p.color AS color,
            d.partName AS partName,
            p.quantity AS planned,
            d.openingBalance AS openingBalance,
            0 AS dispatch,
            0 AS received,
            0 AS remainingPs,
            0 AS remainingVa,
            d.produced AS produced,
            d.rejection AS rejection,
            (d.openingBalance + d.produced - d.rejection) AS cb
        FROM plan_table p
        JOIN dashboard_entry d
            ON p.date = d.date
            AND p.shift = d.shift
            AND p.model = d.model
            AND p.color = d.color
        WHERE p.date = :date AND p.shift = :shift
    """)
    fun getDashboardData(date: String, shift: String): LiveData<List<DashboardEntry>>
}