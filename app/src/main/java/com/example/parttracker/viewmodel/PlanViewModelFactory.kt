package com.example.parttracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parttracker.MyApplication
import com.example.parttracker.repository.PartRepository

class PlanViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanViewModel::class.java)) {
            val database = MyApplication.database
            val repository = PartRepository(
                scannedPartDao = database.scannedPartDao(),
                usedPartDao = database.usedPartDao(),
                dashboardDao = database.dashboardEntryDao(),
                planDao = database.planDao(),
                modelProductionDao = database.modelProductionDao()

            )
            @Suppress("UNCHECKED_CAST")
            return PlanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
