package com.example.parttracker.model

data class RowData(
    val partName: String = "",
    val planned: Int = 0,
    var ob: Int = 0,
    val dispatch: Int = 0,
    val received: Int = 0,
    val remainingPs: Int = 0,
    val remainingVa: Int = 0,
    val produced: Int = 0,
    val rejection: Int = 0,
    val cb: Int = 0,
    val timestamp: Long = 0,
    val model: String = "",
    val color: String = "",
    val shift: String = "",
    val date: String = "",
    val trolleyName: String = "",
    val trolleyNumber: String = "",
    val location: String = ""
)
