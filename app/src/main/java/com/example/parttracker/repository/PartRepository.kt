package com.example.parttracker.repository
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.parttracker.constants.modelToParts
import com.example.parttracker.data.*
import com.example.parttracker.firebase.FirebaseSyncManager
import com.example.parttracker.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PartRepository(
    val scannedPartDao: ScannedPartDao,
    val usedPartDao: UsedPartDao,
    val dashboardDao: DashboardEntryDao,
    val planDao: PlanDao,
    val modelProductionDao: ModelProductionDao
) {

//    suspend fun insertPlan(plan: PlanEntry, context: Context) {
//        planDao.insert(plan)
//        FirebaseSyncManager.pushPlanEntry(plan, context)
//    }

//    suspend fun insert(part: ScannedPart, context: Context) {
////        scannedPartDao.insertPart(part)
//        scannedPartDao.upsert(part)
//        matchScannedPartsToPlans()
//        FirebaseSyncManager.pushScannedPart(part, context)
//    }


    suspend fun insert(part: ScannedPart, context: Context) {
        val exists = scannedPartDao.checkIfExists(part.productId, part.timestamp)

        if (!exists) {
            scannedPartDao.upsert(part)
            matchScannedPartsToPlans()
            FirebaseSyncManager.pushScannedPart(part, context)
        } else {
            println("Duplicate scan ignored: ${part.productId} @ ${part.timestamp}")
        }
    }



//    suspend fun insertPlan(plan: PlanEntry, context: Context) {
//        val existing = planDao.getPlanByModelColorDateShift(
//            model = plan.model,
//            color = plan.color,
//            date = plan.date,
//            shift = plan.shift
//        )
//
//        if (existing == null) {
//            planDao.insert(plan)
//            FirebaseSyncManager.pushPlanEntry(plan, context)
//        } else {
//            println("Plan already exists for ${plan.model} ${plan.color} on ${plan.date} ${plan.shift}, skipping insert.")
//        }
//    }

    suspend fun insertPlan(plan: PlanEntry, context: Context) {
        val existing = planDao.getPlan(
            model = plan.model,
            color = plan.color,
            date = plan.date,
            shift = plan.shift,
            sequence = plan.sequence
        )

        if (existing == null) {
            planDao.insert(plan)
            FirebaseSyncManager.pushPlanEntry(plan, context)
        } else {
            println("Plan already exists for ${plan.model} ${plan.color} on ${plan.date} ${plan.shift} with sequence ${plan.sequence}, skipping insert.")
        }
    }


    fun getPlansForDateShift(date: String, shift: String): LiveData<List<PlanEntry>> {
        return planDao.getPlansForDateShift(date, shift)
    }




    suspend fun insertModelProduction(production: ModelProduction) {
        modelProductionDao.insertOrUpdate(production)
    }

    suspend fun getAllParts(): List<ScannedPart> = scannedPartDao.getAllParts()

    fun getCountByLocation(location: String): LiveData<Int> =
        scannedPartDao.getCountByLocation(location)

    fun getPartCountsByLocation(): LiveData<List<LocationCount>> =
        scannedPartDao.getPartCountsByLocation()

//    suspend fun markPartAsUsed(): Boolean = withContext(Dispatchers.IO) {
//        val ctlParts = scannedPartDao.getPartsByLocation("CTL")
//        if (ctlParts.isNotEmpty()) {
//            val part = ctlParts.first()
//            val usedPart = UsedPart(partName = part.partName, quantity = 1)
//            usedPartDao.insertUsedPart(usedPart)
//            scannedPartDao.deletePart(part)
//            return@withContext true
//        }
//        false
//    }

    suspend fun markPartAsUsed(partName: String, quantity: Int, context: Context) {
        val partsFromCTL = scannedPartDao.getPartsByNameAndLocation(partName, "CTL")
        var toDelete = quantity
        for (part in partsFromCTL) {
            if (toDelete <= 0) break
            scannedPartDao.deletePart(part)
            toDelete--
        }

        val usedPart = UsedPart(partName = partName, quantity = quantity)
        usedPartDao.insertUsedPart(usedPart)
        FirebaseSyncManager.pushUsedPart(usedPart, context) // 🔁 Push here too
    }


    suspend fun getAllScannedParts(): List<ScannedPart> = scannedPartDao.getAllScannedParts()

    fun getCurrentStock(partName: String): LiveData<Int> {
        val arrived = scannedPartDao.getTotalArrivedParts(partName)
        val used = usedPartDao.getTotalUsedParts(partName)

        val stockLiveData = MediatorLiveData<Int>()
        var arrivedValue = 0
        var usedValue = 0

        stockLiveData.addSource(arrived) {
            arrivedValue = it ?: 0
            stockLiveData.value = (arrivedValue - usedValue).coerceAtLeast(0)
        }

        stockLiveData.addSource(used) {
            usedValue = it ?: 0
            stockLiveData.value = (arrivedValue - usedValue).coerceAtLeast(0)
        }

        return stockLiveData
    }


//    suspend fun getUncountedParts(model: String, color: String, date: String, shift: String): List<ScannedPart> {
//        return scannedPartDao.getUncountedParts(model, color, date, shift)
//    }
//
//    suspend fun updateScannedParts(parts: List<ScannedPart>) {
//        scannedPartDao.updateParts(parts)
//    }




    fun getCountByLocationAndPartName(location: String, partName: String): LiveData<Int> =
        scannedPartDao.getCountByLocationAndPartName(location, partName)


    suspend fun insertUsedPart(partName: String, quantity: Int, context: Context) {
        val usedPart = UsedPart(partName = partName, quantity = quantity)
        usedPartDao.insertUsedPart(usedPart)
        FirebaseSyncManager.pushUsedPart(usedPart, context)
    }


    fun getTotalQuantityByLocation(location: String): LiveData<Int> =
        scannedPartDao.getTotalQuantityByLocation(location)

    fun getUsedCountByPart(): LiveData<List<UsedPartCount>> =
        usedPartDao.getUsedCountByPart()

    fun getPartCountsByLocationGrouped(location: String): LiveData<List<PartQuantityCount>> =
        scannedPartDao.getPartCountsByLocation(location)

    fun getDispatchBreakdown(): LiveData<List<PartCountByLocation>> =
        scannedPartDao.getPartBreakdownByLocation("Paintshop")

    fun getStockBreakdown(): LiveData<List<PartCountByLocation>> =
        scannedPartDao.getPartBreakdownByLocation("CTL")

    fun getUsedBreakdown(): LiveData<List<UsedPartCount>> =
        usedPartDao.getUsedBreakdown()

    fun getGroupedPartCountsByLocation(location: String): LiveData<List<PartCountByLocation>> =
        scannedPartDao.getPartBreakdownByLocation(location)

    fun getPlansFor(date: String, shift: String): LiveData<List<PlanEntry>> =
        planDao.getPlansForDateShift(date, shift)

    fun getCountByLocationAndPartNameNow(location: String, partName: String): Int =
        scannedPartDao.getCountByLocationAndPartNameNow(location, partName)

    suspend fun deletePlan(model: String, date: String, shift: String) {
        planDao.deletePlan(model, date, shift)
    }

    suspend fun getPlansByDateShiftAndColor(date: String, shift: String, color: String): List<PlanEntry> {
        return planDao.getPlansByDateAndShift(date, shift).filter { it.color == color }
    }

    suspend fun getCountByLocationDateShiftAndSequence(
        location: String,
        partName: String,
        date: String,
        shift: String,
        sequenceNumber: Int
    ): Int = scannedPartDao.getCountByLocationDateShiftAndSequence(location, partName, date, shift, sequenceNumber)

//    suspend fun matchScannedPartsToPlans() {
//        val unmatched = scannedPartDao.getUnmatchedScans()
//        val allPlans = planDao.getAllPlansNow()
//        for (part in unmatched) {
//            val match = allPlans.find { it.model == part.model && it.color == part.color }
//            if (match != null) {
//                scannedPartDao.updateSequenceForPart(part.id, match.sequence)
//            }
//        }
//    }



    suspend fun matchScannedPartsToPlans() {
        val unmatched = scannedPartDao.getUnmatchedScans()
        val allPlans = planDao.getAllPlansNow()

        for (part in unmatched) {
            val match = allPlans.find { it.model == part.model && it.color == part.color }
            if (match != null) {
                // Replace the unmatched part with a new one having the correct sequence
                val updatedPart = part.copy(sequenceNumber = match.sequence)

                // Upsert again using insertPart (with REPLACE)
                scannedPartDao.insertPart(updatedPart)
            }
        }
    }


    suspend fun clearAllScannedParts() {
        scannedPartDao.deleteAllScannedParts()
    }

    suspend fun clearAllUsedParts() {
        usedPartDao.deleteAllUsedParts()
    }

    fun getDistinctPartNamesFromCTL(): LiveData<List<String>> =
        scannedPartDao.getDistinctPartNamesByLocation("CTL")

    suspend fun getQuantityByLocationAndPartNameNow(location: String, partName: String): Int =
        scannedPartDao.getQuantityByLocationAndPartNameNow(location, partName) ?: 0

    suspend fun insertPlan(plan: PlanEntry) = planDao.insert(plan)

    fun getAllPlansFlow(): Flow<List<PlanEntry>> = planDao.getAllPlansFlow()

    fun getAllPlansLiveData(): LiveData<List<PlanEntry>> = planDao.getAllPlans()

    suspend fun insertPlanAndSync(plan: PlanEntry, context: Context) {
        planDao.insert(plan)
        FirebaseSyncManager.pushPlanEntry(plan, context)
    }






    suspend fun deletePlan(plan: PlanEntry) = planDao.deletePlan(plan)

    // ✅ FIXED VERSION (uses context)
    suspend fun insertOrUpdateDashboard(entry: DashboardRow, context: Context) {
        dashboardDao.insert(entry)
        FirebaseSyncManager.pushDashboardRow(entry, context,
            onSuccess = {
                println("DashboardRow pushed successfully: ${entry.partName}")
            },
            onFailure = {
                println("Failed to push DashboardRow: $it")
            }
        )
    }

    // ✅ FIXED OVERLOADED VERSION
    suspend fun insertOrUpdateDashboard(
        model: String,
        planned: Int,
        date: String,
        shift: String,
        color: String,
        context: Context
    ) {
        val parts = modelToParts[model] ?: return
        for (part in parts) {
            val existing = dashboardDao.getDashboardRow(date, shift, model, color, part)
            val dashboardRow = if (existing == null) {
                DashboardRow(
                    model = model,
                    color = color,
                    partName = part,
                    date = date,
                    shift = shift,
                    planned = planned,
                    ob = 0,
                    dispatch = 0,
                    received = 0,
                    remainingPs = 0,
                    remainingVa = 0,
                    produced = 0,
                    rejection = 0,
                    cb = 0
                )
            } else {
                existing.copy(planned = existing.planned + planned)
            }

            dashboardDao.insert(dashboardRow)

            FirebaseSyncManager.pushDashboardRow(dashboardRow, context,
                onSuccess = {
                    println("DashboardRow pushed successfully: ${dashboardRow.partName}")
                },
                onFailure = {
                    println("Failed to push DashboardRow: $it")
                }
            )
        }
    }

    suspend fun getLastDashboardRowForPart(
        model: String,
        color: String,
        partName: String,
        date: String,
        shift: String
    ): DashboardRow? {
        return dashboardDao.getLastDashboardRowForPart(model, color, partName, date, shift)
    }




    fun getAllDashboardRows(): LiveData<List<DashboardRow>> = dashboardDao.getAllDashboardRows()

    suspend fun getPlansByDateAndShift(date: String, shift: String): List<PlanEntry> =
        planDao.getPlansByDateAndShift(date, shift)

    suspend fun getPartsByModelColorDateShift(model: String, color: String, date: String, shift: String): List<ScannedPart> {
        return scannedPartDao.getPartsByModelColorDateShift(model, color, date, shift)
    }


    suspend fun getPlanByDateShiftAndColor(date: String, shift: String, color: String): PlanEntry? =
        planDao.getPlanByDateShiftAndColor(date, shift, color)


    suspend fun getDashboardRow(date: String, shift: String, model: String, color: String, partName: String): DashboardRow? {
        return dashboardDao.getDashboardRow(date, shift, model, color, partName)
    }




    suspend fun insertModelProduction(production: ModelProduction, context: Context) {
        modelProductionDao.insertOrUpdate(production)
        FirebaseSyncManager.pushModelProduction(production, context)
    }


    suspend fun getPartsByDate(date: String): List<ScannedPart> {
        return scannedPartDao.getPartsByDate(date)
    }

    suspend fun deleteOldScannedParts() {
        val cutoff = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000) // 30 days
        scannedPartDao.deletePartsOlderThan(cutoff)
    }











    suspend fun getModelProduction(
        model: String,
        color: String,
        date: String,
        shift: String
    ): ModelProduction? = modelProductionDao.getModelProduction(model, color, date, shift)

    suspend fun addPlan(entry: PlanEntry) = planDao.insert(entry)

    companion object {
        @Volatile
        private var INSTANCE: PartRepository? = null

        fun getInstance(context: Context): PartRepository {
            return INSTANCE ?: synchronized(this) {
                val db = PartDatabase.getRoomDatabase(context.applicationContext)
                PartRepository(
                    planDao = db.planDao(),
                    modelProductionDao = db.modelProductionDao(),
                    dashboardDao = db.dashboardEntryDao(),
                    scannedPartDao = db.scannedPartDao(),
                    usedPartDao = db.usedPartDao()
                ).also { INSTANCE = it }
            }
        }
    }
}





