package com.example.parttracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashboard_entry")
data class DashboardEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val shift: String,
    val model: String,
    val color: String,
    val partName: String,
    val planned: Int,
    val openingBalance: Int,
    val received: Int,
    val dispatch: Int,
    val produced: Int,
    val rejection: Int,
    val remainingPs: Int,
    val remainingVa: Int,
    val cb: Int
)



