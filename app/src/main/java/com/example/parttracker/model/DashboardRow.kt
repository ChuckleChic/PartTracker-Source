////package com.example.parttracker.model
//
////import androidx.room.Entity
////import androidx.room.PrimaryKey
////
////
////@Entity(tableName = "dashboard_table")
////data class DashboardRow(
////    @PrimaryKey(autoGenerate = true) val id: Int = 0,
////    val date: String,
////    val shift: String,
////    val model: String,
////    val partName: String,
////    val planned: Int,
////    val openingBalance: Int = 0,
////    val dispatch: Int = 0,
////    val received: Int = 0,
////    val remainingPs: Int = 0,
////    val remainingVa: Int = 0,
////    val produced: Int = 0,
////    val rejection: Int = 0,
////    val cb: Int = 0
////)
//
//package com.example.parttracker.model
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//@Entity(tableName = "dashboard_table", primaryKeys = ["date", "shift", "model", "color", "partName"])
//data class DashboardRow(
//    val date: String,
//    val shift: String,
//    val model: String = "",
//    val color: String,
//    val partName: String,
//    val planned: Int,
//    val ob: Int,
//    val dispatch: Int,
//    val received: Int,
//    val remainingPs: Int,
//    val remainingVa: Int,
//    val produced: Int,
//    val rejection: Int,
//    val cb: Int
//)

package com.example.parttracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "dashboard_table",
    primaryKeys = ["date", "shift", "model", "color", "partName"]
)
data class DashboardRow(
    val date: String,
    val shift: String,
    val model: String = "",
    val color: String,
    val partName: String,
    val planned: Int,
    val ob: Int,
    val dispatch: Int,
    val received: Int,
    val remainingPs: Int,
    val remainingVa: Int,
    val produced: Int,
    val rejection: Int,
    val cb: Int,
    val obManuallySet: Boolean = false

) {

    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "date" to date,
            "shift" to shift,
            "model" to model,
            "color" to color,
            "partName" to partName,
            "planned" to planned,
            "ob" to ob,
            "dispatch" to dispatch,
            "received" to received,
            "remainingPs" to remainingPs,
            "remainingVa" to remainingVa,
            "produced" to produced,
            "rejection" to rejection,
            "cb" to cb,
            "obManuallySet" to obManuallySet
        )
    }

    companion object {
//        fun fromFirestoreMap(data: Map<String, Any?>): DashboardRow? {
//            return try {
//                DashboardRow(
//                    date = data["date"] as String,
//                    shift = data["shift"] as String,
//                    model = data["model"] as? String ?: "",
//                    color = data["color"] as String,
//                    partName = data["partName"] as String,
//                    planned = (data["planned"] as Long).toInt(),
//                    ob = (data["ob"] as Long).toInt(),
//                    dispatch = (data["dispatch"] as Long).toInt(),
//                    received = (data["received"] as Long).toInt(),
//                    remainingPs = (data["remainingPs"] as Long).toInt(),
//                    remainingVa = (data["remainingVa"] as Long).toInt(),
//                    produced = (data["produced"] as Long).toInt(),
//                    rejection = (data["rejection"] as Long).toInt(),
//                    cb = (data["cb"] as Long).toInt(),
//                    obManuallySet = data["obManuallySet"] as? Boolean ?: false
//                )
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
            fun fromFirestoreMap(data: Map<String, Any?>): DashboardRow? {
                return try {
                    DashboardRow(
                        date = data["date"] as? String ?: "",
                        shift = data["shift"] as? String ?: "",
                        model = data["model"] as? String ?: "",
                        color = data["color"] as? String ?: "",
                        partName = data["partName"] as? String ?: "",
                        planned = (data["planned"] as? Number)?.toInt() ?: 0,
                        ob = (data["ob"] as? Number)?.toInt() ?: 0,
                        dispatch = (data["dispatch"] as? Number)?.toInt() ?: 0,
                        received = (data["received"] as? Number)?.toInt() ?: 0,
                        remainingPs = (data["remainingPs"] as? Number)?.toInt() ?: 0,
                        remainingVa = (data["remainingVa"] as? Number)?.toInt() ?: 0,
                        produced = (data["produced"] as? Number)?.toInt() ?: 0,
                        rejection = (data["rejection"] as? Number)?.toInt() ?: 0,
                        cb = (data["cb"] as? Number)?.toInt() ?: 0,
                        obManuallySet = data["obManuallySet"] as? Boolean ?: false
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

    }

