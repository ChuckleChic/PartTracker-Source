//package com.example.parttracker.viewmodel
//
//import android.app.Application
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.viewModelScope
//import com.example.parttracker.MyApplication
//import com.example.parttracker.model.LocationCount
//import com.example.parttracker.model.PartQuantityCount
//import com.example.parttracker.model.PlanEntry
//import com.example.parttracker.model.ScannedPart
//import com.example.parttracker.model.UsedPartCount
//import com.example.parttracker.repository.PartRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import android.content.Context
//
//class PartViewModel(application: Application) : AndroidViewModel(application) {
//
//    // ✅ Single, clean repository initialization
//    private val repository = PartRepository(
//        scannedPartDao = MyApplication.database.scannedPartDao(),
//        usedPartDao = MyApplication.database.usedPartDao(),
//        dashboardDao = MyApplication.database.dashboardEntryDao(),
//        planDao = MyApplication.database.planDao(),
//        modelProductionDao = MyApplication.database.modelProductionDao()
//    )
//
//
//
//
//
//    // ✅ LiveData exposed from repository
//    val partCountsByLocation: LiveData<List<LocationCount>> = repository.getPartCountsByLocation()
//    val usedCountByPart: LiveData<List<UsedPartCount>> = repository.getUsedCountByPart()
//
//    val plans: LiveData<List<PlanEntry>> = repository.planDao.getAllPlans()
//
//
//
//
//    val dispatchBreakdown = repository.getDispatchBreakdown()
//    val stockBreakdown = repository.getStockBreakdown()
//    val usedBreakdown = repository.getUsedBreakdown()
//
//
//
//    // ✅ Insert scanned part
//    fun insertPart(part: ScannedPart, context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insert(part, context)
//        }
//    }
//
//    // ✅ Mark one part as used from CTL automatically
//    fun insertUsedPart() {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.markPartAsUsed()
//        }
//    }
//
//    // ✅ Get scanned part count at a specific location
//    fun getCountByLocation(location: String): LiveData<Int> {
//        return repository.getCountByLocation(location)
//    }
//
//    // ✅ Get all scanned parts (non-LiveData, one-shot)
//    suspend fun getAllParts(): List<ScannedPart> {
//        return repository.getAllParts()
//    }
//
//    // ✅ Get current stock for a specific part
//    fun getStockForPart(partName: String): LiveData<Int> {
//        return repository.getCurrentStock(partName)
//    }
//
//    // ✅ Mark N quantity of a part as used
//    fun markPartAsUsed(partName: String, quantity: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.markPartAsUsed(partName, quantity)
//        }
//    }
//
//    fun getCountByLocationAndPartName(location: String, partName: String): LiveData<Int> {
//        return repository.getCountByLocationAndPartName(location, partName)
//    }
//
////    fun markUsed(partName: String, quantity: Int) {
////        viewModelScope.launch {
////            repository.markUsed(partName, quantity)
////        }
////    }
//
//    fun insertUsedPart(partName: String, quantity: Int) {
//        viewModelScope.launch {
//            repository.markPartAsUsed(partName, quantity, context)
//
//            )
//        }
//    }
//
//    fun getTotalQuantityByLocation(location: String): LiveData<Int> {
//        return repository.getTotalQuantityByLocation(location)
//    }
//
//    fun getGroupedPartCountsByLocation(location: String): LiveData<List<PartQuantityCount>> {
//        return repository.getPartCountsByLocationGrouped(location)
//    }
//
//    fun clearAllScannedParts() {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.clearAllScannedParts()
//        }
//    }
//
//    fun clearAllUsedParts() = viewModelScope.launch {
//        repository.clearAllUsedParts()
//    }
//
//    fun getDistinctPartNamesFromCTL(): LiveData<List<String>> {
//        return repository.getDistinctPartNamesFromCTL()
//    }
//
//    fun getGroupedPartQuantitiesByLocation(location: String): LiveData<List<LocationCount>> {
//        return repository.getPartCountsByLocation()
//    }
////    fun getPartCountsByLocationGrouped(location: String): LiveData<List<PartQuantityCount>> {
////        return repository.getPartCountsByLocation(location)
////    }
//
//
//
//
//
//
//
//}


package com.example.parttracker.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.parttracker.MyApplication
import com.example.parttracker.model.*
import com.example.parttracker.repository.PartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PartRepository(
        scannedPartDao = MyApplication.database.scannedPartDao(),
        usedPartDao = MyApplication.database.usedPartDao(),
        dashboardDao = MyApplication.database.dashboardEntryDao(),
        planDao = MyApplication.database.planDao(),
        modelProductionDao = MyApplication.database.modelProductionDao()
    )

    val partCountsByLocation: LiveData<List<LocationCount>> = repository.getPartCountsByLocation()
    val usedCountByPart: LiveData<List<UsedPartCount>> = repository.getUsedCountByPart()
    val plans: LiveData<List<PlanEntry>> = repository.planDao.getAllPlans()
    val dispatchBreakdown = repository.getDispatchBreakdown()
    val stockBreakdown = repository.getStockBreakdown()
    val usedBreakdown = repository.getUsedBreakdown()

    fun insertPart(part: ScannedPart, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(part, context)
        }
    }

    fun getCountByLocation(location: String): LiveData<Int> {
        return repository.getCountByLocation(location)
    }

    suspend fun getAllParts(): List<ScannedPart> {
        return repository.getAllParts()
    }

    fun getStockForPart(partName: String): LiveData<Int> {
        return repository.getCurrentStock(partName)
    }

    // ✅ MARK USED (with context for Firebase sync)
    fun markPartAsUsed(partName: String, quantity: Int, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markPartAsUsed(partName, quantity, context)
        }
    }

    fun insertUsedPart(partName: String, quantity: Int, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUsedPart(partName, quantity, context)
        }
    }

    fun getCountByLocationAndPartName(location: String, partName: String): LiveData<Int> {
        return repository.getCountByLocationAndPartName(location, partName)
    }

    fun getTotalQuantityByLocation(location: String): LiveData<Int> {
        return repository.getTotalQuantityByLocation(location)
    }

    fun getGroupedPartCountsByLocation(location: String): LiveData<List<PartQuantityCount>> {
        return repository.getPartCountsByLocationGrouped(location)
    }

    fun clearAllScannedParts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllScannedParts()
        }
    }

    fun clearAllUsedParts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllUsedParts()
        }
    }

    fun getDistinctPartNamesFromCTL(): LiveData<List<String>> {
        return repository.getDistinctPartNamesFromCTL()
    }

    fun getGroupedPartQuantitiesByLocation(location: String): LiveData<List<LocationCount>> {
        return repository.getPartCountsByLocation()
    }


    suspend fun getPartsByDate(date: String): List<ScannedPart> {
        return repository.getPartsByDate(date)
    }

    fun deleteOldScans() {
        viewModelScope.launch {
            repository.deleteOldScannedParts()
        }
    }


}
