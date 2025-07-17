package com.example.parttracker.ui.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateAndShift(): Pair<String, String> {
    val now = LocalTime.now()
    val today = LocalDate.now()
    return when {
        now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
        now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
        else -> today.minusDays(1).toString() to "B"
    }
}

