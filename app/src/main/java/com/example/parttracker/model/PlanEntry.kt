////package com.example.parttracker.model
////
////import androidx.room.Entity
////import androidx.room.PrimaryKey
////
////
////@Entity(tableName = "plan_table")
////data class PlanEntry(
////    @PrimaryKey(autoGenerate = true) val id: Int = 0,
////    val sequence: Int,
////    val model: String,
////    val quantity: Int,
////    val date: String,
////    val shift: String,
////    val color: String
////
////    //val completed: Int = 0
////)
////
////
//
//
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//@Entity(tableName = "plan_table")
//data class PlanEntry(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val sequence: Int,
//    val model: String,
//    val quantity: Int,
//    val date: String,
//    val shift: String,
//    val color: String
//) {
//    fun toFirestoreMap(): Map<String, Any> {
//        return mapOf(
//            "sequence" to sequence,
//            "model" to model,
//            "quantity" to quantity,
//            "date" to date,
//            "shift" to shift,
//            "color" to color
//        )
//    }
//
//    companion object {
//        fun fromFirestoreMap(data: Map<String, Any?>): PlanEntry {
//            return PlanEntry(
//                sequence = (data["sequence"] as? Long)?.toInt() ?: 0,
//                model = data["model"] as? String ?: "",
//                quantity = (data["quantity"] as? Long)?.toInt() ?: 0,
//                date = data["date"] as? String ?: "",
//                shift = data["shift"] as? String ?: "",
//                color = data["color"] as? String ?: ""
//            )
//        }
//    }
//}

package com.example.parttracker.model
import androidx.room.Entity

@Entity(
    tableName = "plan_table",
    primaryKeys = ["model", "color", "date", "shift", "sequence"]
)
data class PlanEntry(
    val sequence: Int,
    val model: String,
    val quantity: Int,
    val date: String,
    val shift: String,
    val color: String
) {
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "sequence" to sequence,
            "model" to model,
            "quantity" to quantity,
            "date" to date,
            "shift" to shift,
            "color" to color
        )
    }

    companion object {
        fun fromFirestoreMap(data: Map<String, Any?>): PlanEntry {
            return PlanEntry(
                sequence = (data["sequence"] as? Long)?.toInt() ?: 0,
                model = data["model"] as? String ?: "",
                quantity = (data["quantity"] as? Long)?.toInt() ?: 0,
                date = data["date"] as? String ?: "",
                shift = data["shift"] as? String ?: "",
                color = data["color"] as? String ?: ""
            )
        }
    }
}
