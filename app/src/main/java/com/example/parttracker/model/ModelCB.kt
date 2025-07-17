package com.example.parttracker.model

data class CBPartEntry(
    val partName: String,
    val cb: Int
)

data class ColorGroup(
    val color: String,
    val partsCB: List<CBPartEntry>,  // <--- this is important
    var isExpanded: Boolean = false
)

data class ModelGroup(
    val model: String,
    val colorGroups: List<ColorGroup>,  // <--- this is important
    var isExpanded: Boolean = false
)
