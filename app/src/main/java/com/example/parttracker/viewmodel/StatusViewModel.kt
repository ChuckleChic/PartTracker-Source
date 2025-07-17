package com.example.parttracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.parttracker.MyApplication
import com.example.parttracker.data.UsedPart
import com.example.parttracker.data.UsedPartDao
import com.example.parttracker.model.LocationCount
import com.example.parttracker.model.UsedPartCount

class StatusViewModel : ViewModel() {
    private val scannedPartDao = MyApplication.database.scannedPartDao()
    private val usedPartDao = MyApplication.database.usedPartDao()

    val dispatchCount: LiveData<Int> = scannedPartDao.getCountByLocation("Paintshop")
    val ctlCount: LiveData<Int> = scannedPartDao.getCountByLocation("CTL")
//    val usedCount: LiveData<Int> = usedPartDao.getUsedCountByPart("GenericPart") // Change to specific part if needed
    //val usedCountByPart: LiveData<List<UsedPartCount>> = scannedPartDao.getUsedCountByPart()
    val usedCountByPart: LiveData<List<UsedPartCount>> = scannedPartDao.getUsedCountByPart()


}
