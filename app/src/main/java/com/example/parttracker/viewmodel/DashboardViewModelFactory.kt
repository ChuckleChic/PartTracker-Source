//package com.example.parttracker.viewmodel
//
//import android.content.Context
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.parttracker.data.PartDatabase
//import com.example.parttracker.repository.PartRepository
//
//class DashboardViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
//            val db = PartDatabase.getRoomDatabase(context)
//            val repository = PartRepository(
//                db.scannedPartDao(),
//                db.usedPartDao(),
//                db.dashboardEntryDao(),
//                db.planDao()
//            )
//            return DashboardViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//    }
//}

//

package com.example.parttracker.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parttracker.data.PartDatabase
import com.example.parttracker.repository.PartRepository

class DashboardViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            val db = PartDatabase.getRoomDatabase(context)
            val repository = PartRepository(
                db.scannedPartDao(),
                db.usedPartDao(),
                db.dashboardEntryDao(),
                db.planDao(),
                db.modelProductionDao(),

            )
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

