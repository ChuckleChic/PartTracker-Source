package com.example.parttracker.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.parttracker.model.PlanEntry
import com.example.parttracker.repository.PartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.map
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.parttracker.firebase.FirebaseSyncManager


class PlanViewModel(private val repository: PartRepository) : ViewModel() {

    fun getPlansForDateShift(date: String, shift: String): LiveData<List<PlanEntry>> {
        return repository.getPlansForDateShift(date, shift)
    }


    val allPlans: LiveData<List<PlanEntry>> = liveData(Dispatchers.IO) {
        emitSource(repository.getAllPlansLiveData())
    }

    init {
        FirebaseSyncManager.pullAllPlans { plans ->
            viewModelScope.launch {
                for (plan in plans) {
                    repository.insertPlan(plan) // Insert locally
                }
            }
        }

        FirebaseSyncManager.listenToPlanUpdates { plan ->
            viewModelScope.launch {
                repository.insertPlan(plan) // Live sync updates
            }
        }
    }



//    val plans: LiveData<List<PlanEntry>> = repository.planDao.getAllPlans()

    @RequiresApi(Build.VERSION_CODES.O)
    val plans: LiveData<List<PlanEntry>> = repository.getAllPlansLiveData().map { planList ->
        val today = java.time.LocalDate.now()
        planList.filter {
            try {
                java.time.LocalDate.parse(it.date) >= today
            } catch (e: Exception) {
                false
            }
        }
    }





    // ✅ Get plans for a specific date and shift reactively
    fun getPlansFor(date: String, shift: String): LiveData<List<PlanEntry>> {
        return repository.getPlansFor(date, shift)
    }

    // ✅ Insert plan without needing to manually reload — observer gets updated automatically
    fun insertPlan(plan: PlanEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPlan(plan)
        }
    }

    fun deletePlan(model: String, date: String, shift: String) {
        viewModelScope.launch {
            repository.deletePlan(model, date, shift)
        }
    }

    fun deletePlan(plan: PlanEntry) {
        viewModelScope.launch {
            repository.deletePlan(plan)
        }
    }

    fun insertPlanAndSync(plan: PlanEntry, context: Context) {
        viewModelScope.launch {
            repository.insertPlanAndSync(plan, context)
        }
    }




}
