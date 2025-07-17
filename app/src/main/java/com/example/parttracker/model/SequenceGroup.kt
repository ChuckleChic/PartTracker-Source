package com.example.parttracker.model

data class SequenceGroup(
    val sequence: Int,
    val totalPlanned: Int,
    val totalCompleted: Int,
    val colorGroups: List<ColorGroupedDashboard>,
    var isExpanded: Boolean = false
)

data class ColorGroupedDashboard(
    val color: String,
    val rows: List<DashboardRow>
)

