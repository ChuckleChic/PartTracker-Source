////package com.example.parttracker.model
////
////import androidx.room.Entity
////import androidx.room.PrimaryKey
////
////
////@Entity(tableName = "model_production", primaryKeys = ["model", "color", "date", "shift"])
////data class ModelProduction(
////    val model: String,
////    val color: String,
////    val date: String,
////    val shift: String,
////    val openingBalance: Int,
////    val produced: Int,
////    val rejection: Int
////)
//
//package com.example.parttracker.model
//
//import androidx.room.Entity
//import androidx.room.Ignore
//
//@Entity(tableName = "model_production", primaryKeys = ["model", "color", "date", "shift"])
//data class ModelProduction(
//    val model: String,
//    val color: String,
//    val date: String,
//    val shift: String,
//    val openingBalance: Int,
//    val produced: Int,
//    val rejection: Int,
//    @Ignore
//    var documentId: String = ""
//) {
//
//    fun toFirestoreMap(): Map<String, Any?> {
//        return mapOf(
//            "documentId" to documentId,
//            "model" to model,
//            "color" to color,
//            "date" to date,
//            "shift" to shift,
//            "openingBalance" to openingBalance,
//            "produced" to produced,
//            "rejection" to rejection
//        )
//    }
//
//    companion object {
//        fun fromFirestoreMap(data: Map<String, Any?>): ModelProduction {
//            return ModelProduction(
//                model = data["model"] as? String ?: "",
//                color = data["color"] as? String ?: "",
//                date = data["date"] as? String ?: "",
//                shift = data["shift"] as? String ?: "",
//                openingBalance = (data["openingBalance"] as? Long ?: 0L).toInt(),
//                produced = (data["produced"] as? Long ?: 0L).toInt(),
//                rejection = (data["rejection"] as? Long ?: 0L).toInt(),
//                documentId = data["documentId"] as? String ?: ""
//            )
//        }
//    }
//}


package com.example.parttracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
// No @Ignore needed for documentId here

@Entity(
    tableName = "model_production",
    primaryKeys = ["date", "shift", "model", "color"]
)
data class ModelProduction(
    val model: String,
    val color: String,
    val date: String,
    val shift: String,
    val openingBalance: Int = 0,
    val produced: Int = 0,
    val rejection: Int = 0
) {
    // Moved documentId outside the primary constructor
    var documentId: String = ""

    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "documentId" to documentId,
            "model" to model,
            "color" to color,
            "date" to date,
            "shift" to shift,
            "openingBalance" to openingBalance,
            "produced" to produced,
            "rejection" to rejection
        )
    }

    companion object {
        fun fromFirestoreMap(data: Map<String, Any?>): ModelProduction? {
            return try {
                val modelProduction = ModelProduction(
                    model = data["model"] as String,
                    color = data["color"] as String,
                    date = data["date"] as String,
                    shift = data["shift"] as String,
                    openingBalance = (data["openingBalance"] as Long).toInt(),
                    produced = (data["produced"] as Long).toInt(),
                    rejection = (data["rejection"] as Long).toInt()
                )
                modelProduction.documentId = data["documentId"] as? String ?: ""
                return modelProduction
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

