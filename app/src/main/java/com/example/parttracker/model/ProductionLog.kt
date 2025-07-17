package com.example.parttracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "production_log")
data class ProductionLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,  // "yyyy-MM-dd"
    val shift: Int,    // 1 or 2
    val model: String,
    val partName: String,

    val plan: Int,
    val openingBalanceVA: Int,
    val vehiclesProduced: Int,
    val rejections: Int
)
