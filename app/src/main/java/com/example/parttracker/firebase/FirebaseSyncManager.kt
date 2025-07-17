////////package com.example.parttracker.firebase
////////
////////import android.content.Context
////////import android.util.Log
////////import com.android.volley.DefaultRetryPolicy
////////import com.android.volley.toolbox.StringRequest
////////import com.android.volley.toolbox.Volley
////////import com.example.parttracker.data.UsedPart
////////import com.example.parttracker.model.DashboardRow
////////import com.example.parttracker.model.ModelProduction
////////import com.example.parttracker.model.PlanEntry
////////import com.example.parttracker.model.ScannedPart
////////import com.google.firebase.firestore.FirebaseFirestore
////////import org.json.JSONObject
////////
////////object FirebaseSyncManager {
////////
////////    private val firestore = FirebaseFirestore.getInstance()
////////
////////    private const val GOOGLE_SHEET_WEBHOOK_URL =
////////        "https://script.google.com/macros/s/AKfycbwk9HphRofyXtPUp5piF25_MGV4vQX-lEWJX_MIPv0UC2h-ntbBKySsTp5ruZvvJ9dV/exec"
////////    // ✅ Push ScannedPart to Firestore + Google Sheet
////////    fun pushScannedPart(scannedPart: ScannedPart, context: Context) {
////////        val docId = "${scannedPart.productId}_${scannedPart.timestamp}"
////////        val data = scannedPart.toFirestoreMap()
////////
////////        firestore.collection("scanned_parts")
////////            .document(docId)
////////            .set(data)
////////            .addOnSuccessListener {
////////                Log.d("FirestorePush", "ScannedPart pushed: $docId")
////////                sendToGoogleSheet(data, context, "GoogleSheet-Scanned")
////////            }
////////            .addOnFailureListener { e ->
////////                Log.e("FirestorePush", "ScannedPart push failed: $docId", e)
////////            }
////////    }
////////
////////    // ✅ Push DashboardRow to Firestore + Google Sheet
////////    fun pushDashboardRow(
////////        row: DashboardRow,
////////        context: Context,
////////        onSuccess: () -> Unit = {},
////////        onFailure: (Exception) -> Unit = {}
////////    ) {
////////        val data = mapOf(
////////            "source" to "dashboard",
////////            "date" to row.date,
////////            "shift" to row.shift,
////////            "model" to row.model,
////////            "color" to row.color,
////////            "partName" to row.partName,
////////            "planned" to row.planned,
////////            "ob" to row.ob,
////////            "dispatch" to row.dispatch,
////////            "received" to row.received,
////////            "remainingPs" to row.remainingPs,
////////            "remainingVa" to row.remainingVa,
////////            "produced" to row.produced,
////////            "rejection" to row.rejection,
////////            "cb" to row.cb
////////        )
////////
////////        val safePartName = row.partName.replace("/", "_")  // 👈 fix here
////////        val documentId = "${row.date}_${row.shift}_$safePartName"
////////
////////
////////        firestore.collection("dashboard")
////////            .document(documentId)
////////            .set(data)
////////            .addOnSuccessListener {
////////                onSuccess()
////////                sendToGoogleSheet(data, context, "GoogleSheet-Dashboard")
////////            }
////////            .addOnFailureListener { e ->
////////                onFailure(e)
////////            }
////////    }
////////
////////    // ✅ Shared function to send data to Google Sheet
////////    private fun sendToGoogleSheet(data: Map<String, Any?>, context: Context, logTag: String) {
////////        val taggedData = data.toMutableMap().apply {
////////            put("source", if (logTag.contains("Scanned", ignoreCase = true)) "scanned" else "dashboard")
////////        }
////////
////////        val requestBody = JSONObject(taggedData as Map<*, *>).toString()
////////
////////        val request = object : StringRequest(
////////            Method.POST,
////////            GOOGLE_SHEET_WEBHOOK_URL,
////////            { response -> Log.d(logTag, "Success: $response") },
////////            { error -> Log.e(logTag, "Volley Error: ${error.message}", error) }
////////        ) {
////////            override fun getBody(): ByteArray = requestBody.toByteArray(Charsets.UTF_8)
////////
////////            override fun getBodyContentType(): String = "application/json; charset=utf-8"
////////
////////            // This tells Volley not to send form-encoded parameters
////////            override fun getParams(): MutableMap<String, String> = mutableMapOf()
////////        }
////////
////////        request.retryPolicy = DefaultRetryPolicy(
////////            5000,
////////            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
////////            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
////////        )
////////
////////        Volley.newRequestQueue(context).add(request)
////////    }
////////
////////
////////    fun listenToDashboardUpdates(
////////        context: Context,
////////        onUpdate: (DashboardRow) -> Unit
////////    ) {
////////        firestore.collection("dashboard")
////////            .addSnapshotListener { snapshots, error ->
////////                if (error != null) {
////////                    Log.e("FirebaseSync", "Listen failed: ", error)
////////                    return@addSnapshotListener
////////                }
////////
////////                for (doc in snapshots!!.documents) {
////////                    val data = doc.data ?: continue
////////                    try {
////////                        val row = DashboardRow(
////////                            date = data["date"] as String,
////////                            shift = data["shift"] as String,
////////                            model = data["model"] as String,
////////                            color = data["color"] as String,
////////                            partName = data["partName"] as String,
////////                            planned = (data["planned"] as? Long ?: 0L).toInt(),
////////                            ob = (data["ob"] as? Long ?: 0L).toInt(),
////////                            dispatch = (data["dispatch"] as? Long ?: 0L).toInt(),
////////                            received = (data["received"] as? Long ?: 0L).toInt(),
////////                            remainingPs = (data["remainingPs"] as? Long ?: 0L).toInt(),
////////                            remainingVa = (data["remainingVa"] as? Long ?: 0L).toInt(),
////////                            produced = (data["produced"] as? Long ?: 0L).toInt(),
////////                            rejection = (data["rejection"] as? Long ?: 0L).toInt(),
////////                            cb = (data["cb"] as? Long ?: 0L).toInt()
////////                        )
////////                        onUpdate(row)
////////                    } catch (e: Exception) {
////////                        Log.e("FirebaseSync", "Error parsing dashboard row", e)
////////                    }
////////                }
////////            }
////////    }
////////
////////
////////    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
////////        val ref = FirebaseFirestore.getInstance().collection("scanned_parts")
////////
////////        ref.addSnapshotListener { snapshots, error ->
////////            if (error != null) {
////////                Log.e("FirebaseSync", "ScannedParts listener failed", error)
////////                return@addSnapshotListener
////////            }
////////
////////            for (doc in snapshots!!.documents) {
////////                val data = doc.data ?: continue
////////                try {
////////                    val scanned = ScannedPart.fromFirestoreMap(data)
////////                    onUpdate(scanned)
////////                } catch (e: Exception) {
////////                    Log.e("FirebaseSync", "Error parsing ScannedPart", e)
////////                }
////////            }
////////        }
////////    }
////////
////////    fun pushUsedPart(usedPart: UsedPart, context: Context) {
////////        val docId = "${usedPart.partName}_${usedPart.timestamp}"
////////        val data = mapOf(
////////            "partName" to usedPart.partName,
////////            "quantity" to usedPart.quantity,
////////            "timestamp" to usedPart.timestamp
////////        )
////////
////////        FirebaseFirestore.getInstance()
////////            .collection("used_parts")
////////            .document(docId)
////////            .set(data)
////////            .addOnSuccessListener {
////////                Log.d("FirestorePush", "UsedPart pushed: $docId")
////////            }
////////            .addOnFailureListener { e ->
////////                Log.e("FirestorePush", "UsedPart push failed: $docId", e)
////////            }
////////    }
////////
////////
////////    fun listenToUsedPartsUpdates(onUpdate: (UsedPart) -> Unit) {
////////        val ref = FirebaseFirestore.getInstance().collection("used_parts")
////////
////////        ref.addSnapshotListener { snapshots, error ->
////////            if (error != null) {
////////                Log.e("FirebaseSync", "UsedParts listener failed", error)
////////                return@addSnapshotListener
////////            }
////////
////////            for (doc in snapshots!!.documents) {
////////                val data = doc.data ?: continue
////////                try {
////////                    val usedPart = UsedPart(
////////                        partName = data["partName"] as String,
////////                        quantity = (data["quantity"] as? Long ?: 0L).toInt(),
////////                        timestamp = (data["timestamp"] as? Long ?: System.currentTimeMillis())
////////                    )
////////                    onUpdate(usedPart)
////////                } catch (e: Exception) {
////////                    Log.e("FirebaseSync", "Error parsing UsedPart", e)
////////                }
////////            }
////////        }
////////    }
////////
////////
////////    fun pushPlanEntry(plan: PlanEntry, context: Context) {
////////        val docId = "${plan.date}_${plan.shift}_${plan.model}_${plan.color}".replace(" ", "_")
////////        val data = mapOf(
////////            "sequence" to plan.sequence,
////////            "model" to plan.model,
////////            "quantity" to plan.quantity,
////////            "date" to plan.date,
////////            "shift" to plan.shift,
////////            "color" to plan.color
////////        )
////////
////////        FirebaseFirestore.getInstance()
////////            .collection("plans")
////////            .document(docId)
////////            .set(data)
////////            .addOnSuccessListener {
////////                Log.d("FirebasePush", "PlanEntry pushed: $docId")
////////            }
////////            .addOnFailureListener { e ->
////////                Log.e("FirebasePush", "Failed to push PlanEntry: $docId", e)
////////            }
////////    }
////////
////////
////////    fun listenToPlanUpdates(onUpdate: (PlanEntry) -> Unit) {
////////        val ref = FirebaseFirestore.getInstance().collection("plans")
////////
////////        ref.addSnapshotListener { snapshots, error ->
////////            if (error != null) {
////////                Log.e("FirebaseSync", "Plan listener failed", error)
////////                return@addSnapshotListener
////////            }
////////
////////            for (doc in snapshots!!.documents) {
////////                val data = doc.data ?: continue
////////                try {
////////                    val plan = PlanEntry(
////////                        sequence = (data["sequence"] as? Long ?: 0L).toInt(),
////////                        model = data["model"] as String,
////////                        quantity = (data["quantity"] as? Long ?: 0L).toInt(),
////////                        date = data["date"] as String,
////////                        shift = data["shift"] as String,
////////                        color = data["color"] as String
////////                    )
////////                    onUpdate(plan)
////////                } catch (e: Exception) {
////////                    Log.e("FirebaseSync", "Error parsing PlanEntry", e)
////////                }
////////            }
////////        }
////////    }
////////
////////
////////    fun pushModelProduction(entry: ModelProduction, context: Context) {
////////        val docId = "${entry.date}_${entry.shift}_${entry.model}_${entry.color}".replace(" ", "_")
////////        val data = mapOf(
////////            "model" to entry.model,
////////            "color" to entry.color,
////////            "date" to entry.date,
////////            "shift" to entry.shift,
////////            "openingBalance" to entry.openingBalance,
////////            "produced" to entry.produced,
////////            "rejection" to entry.rejection
////////        )
////////
////////        FirebaseFirestore.getInstance()
////////            .collection("model_production")
////////            .document(docId)
////////            .set(data)
////////            .addOnSuccessListener {
////////                Log.d("FirebasePush", "ModelProduction pushed: $docId")
////////            }
////////            .addOnFailureListener { e ->
////////                Log.e("FirebasePush", "Failed to push ModelProduction: $docId", e)
////////            }
////////    }
////////
////////    fun listenToModelProductionUpdates(onUpdate: (ModelProduction) -> Unit) {
////////        FirebaseFirestore.getInstance()
////////            .collection("model_production")
////////            .addSnapshotListener { snapshots, error ->
////////                if (error != null) {
////////                    Log.e("FirebaseSync", "ModelProduction listener failed", error)
////////                    return@addSnapshotListener
////////                }
////////
////////                for (doc in snapshots!!.documents) {
////////                    val data = doc.data ?: continue
////////                    try {
////////                        val entry = ModelProduction(
////////                            model = data["model"] as String,
////////                            color = data["color"] as String,
////////                            date = data["date"] as String,
////////                            shift = data["shift"] as String,
////////                            openingBalance = (data["openingBalance"] as? Long ?: 0L).toInt(),
////////                            produced = (data["produced"] as? Long ?: 0L).toInt(),
////////                            rejection = (data["rejection"] as? Long ?: 0L).toInt()
////////                        )
////////                        onUpdate(entry)
////////                    } catch (e: Exception) {
////////                        Log.e("FirebaseSync", "Error parsing ModelProduction", e)
////////                    }
////////                }
////////            }
////////    }
////////
////////
////////
////////
////////
////////
////////
////////
////////
////////
////////
////////
////////
////////}
//////
////////
////////package com.example.parttracker.firebase
////////
////////import android.content.Context
////////import android.util.Log
////////import com.android.volley.DefaultRetryPolicy
////////import com.android.volley.toolbox.StringRequest
////////import com.android.volley.toolbox.Volley
////////import com.example.parttracker.data.UsedPart
////////import com.example.parttracker.model.*
////////import com.google.firebase.firestore.DocumentChange
////////import com.google.firebase.firestore.FirebaseFirestore
////////import org.json.JSONObject
////////
////////object FirebaseSyncManager {
////////
////////    private val firestore = FirebaseFirestore.getInstance()
////////
////////    private const val GOOGLE_SHEET_WEBHOOK_URL =
////////        "https://script.google.com/macros/s/AKfycbwk9HphRofyXtPUp5piF25_MGV4vQX-lEWJX_MIPv0UC2h-ntbBKySsTp5ruZvvJ9dV/exec"
////////
////////    // 🔁 Loop Prevention Flags
////////    @Volatile private var isFromFirestore = false
////////
////////    // ✅ Push ScannedPart
////////    fun pushScannedPart(scannedPart: ScannedPart, context: Context) {
////////        if (isFromFirestore) return
////////        val docId = "${scannedPart.productId}_${scannedPart.timestamp}"
////////        val data = scannedPart.toFirestoreMap()
////////
////////        firestore.collection("scanned_parts")
////////            .document(docId)
////////            .set(data)
////////            .addOnSuccessListener {
////////                Log.d("FirestorePush", "ScannedPart pushed: $docId")
////////                sendToGoogleSheet(data, context, "GoogleSheet-Scanned")
////////            }
////////            .addOnFailureListener { e ->
////////                Log.e("FirestorePush", "ScannedPart push failed: $docId", e)
////////            }
////////    }
////////
//////////    fun pushDashboardRow(row: DashboardRow, context: Context, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
//////////        if (isFromFirestore) return
//////////
//////////        val data = mapOf(
//////////            "source" to "dashboard",
//////////            "date" to row.date,
//////////            "shift" to row.shift,
//////////            "model" to row.model,
//////////            "color" to row.color,
//////////            "partName" to row.partName,
//////////            "planned" to row.planned,
//////////            "ob" to row.ob,
//////////            "dispatch" to row.dispatch,
//////////            "received" to row.received,
//////////            "remainingPs" to row.remainingPs,
//////////            "remainingVa" to row.remainingVa,
//////////            "produced" to row.produced,
//////////            "rejection" to row.rejection,
//////////            "cb" to row.cb
//////////        )
//////////
//////////        val safePartName = row.partName.replace("/", "_")
//////////        val documentId = "${row.date}_${row.shift}_$safePartName"
//////////
//////////        firestore.collection("dashboard")
//////////            .document(documentId)
//////////            .set(data)
//////////            .addOnSuccessListener {
//////////                onSuccess()
//////////                sendToGoogleSheet(data, context, "GoogleSheet-Dashboard")
//////////            }
//////////            .addOnFailureListener { e -> onFailure(e) }
//////////    }
////////
////////    fun pushDashboardRow(
////////        row: DashboardRow,
////////        context: Context,
////////        onSuccess: () -> Unit = {},
////////        onFailure: (Exception) -> Unit = {}
////////    ) {
////////        if (isFromFirestore) return
////////
////////        val documentId = "${row.date}_${row.shift}_${row.model}_${row.color}_${row.partName}".replace("/", "_")
////////
////////        firestore.collection("dashboard")
////////            .document(documentId)
////////            .set(row.toFirestoreMap())
////////            .addOnSuccessListener {
////////                onSuccess()
////////                sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
////////            }
////////            .addOnFailureListener { onFailure(it) }
////////    }
////////
////////
////////    fun pushUsedPart(usedPart: UsedPart, context: Context) {
////////        if (isFromFirestore) return
////////        val docId = "${usedPart.partName}_${usedPart.timestamp}"
////////        val data = mapOf(
////////            "partName" to usedPart.partName,
////////            "quantity" to usedPart.quantity,
////////            "timestamp" to usedPart.timestamp
////////        )
////////
////////        firestore.collection("used_parts")
////////            .document(docId)
////////            .set(data)
////////            .addOnSuccessListener {
////////                Log.d("FirestorePush", "UsedPart pushed: $docId")
////////            }
////////            .addOnFailureListener { e ->
////////                Log.e("FirestorePush", "UsedPart push failed: $docId", e)
////////            }
////////    }
////////
//////////    fun pushPlanEntry(plan: PlanEntry, context: Context) {
//////////        if (isFromFirestore) return
//////////        val safeModel = plan.model.replace("/", "_")
//////////        val safeColor = plan.color.replace("/", "_")
//////////        val docId = "${plan.date}_${plan.shift}_${safeModel}_${safeColor}".replace(" ", "_")
//////////        val data = mapOf(
//////////            "sequence" to plan.sequence,
//////////            "model" to plan.model,
//////////            "quantity" to plan.quantity,
//////////            "date" to plan.date,
//////////            "shift" to plan.shift,
//////////            "color" to plan.color
//////////        )
//////////
//////////        firestore.collection("plans")
//////////            .document(docId)
//////////            .set(data)
//////////            .addOnSuccessListener {
//////////                Log.d("FirebasePush", "PlanEntry pushed: $docId")
//////////            }
//////////            .addOnFailureListener { e ->
//////////                Log.e("FirebasePush", "Failed to push PlanEntry: $docId", e)
//////////            }
//////////    }
////////
////////    fun pushPlanEntry(plan: PlanEntry, context: Context) {
////////        if (isFromFirestore) return
////////
////////        val safeModel = plan.model.replace("/", "_")
////////        val safeColor = plan.color.replace("/", "_")
////////        val docId = "${plan.date}_${plan.shift}_${safeModel}_${safeColor}".replace(" ", "_")
////////
////////        firestore.collection("plans")
////////            .document(docId)
////////            .set(plan.toFirestoreMap())
////////            .addOnSuccessListener {
////////                Log.d("FirebasePush", "PlanEntry pushed: $docId")
////////            }
////////            .addOnFailureListener { e ->
////////                Log.e("FirebasePush", "Failed to push PlanEntry: $docId", e)
////////            }
////////    }
////////
////////    fun pushModelProduction(entry: ModelProduction, context: Context) {
////////        if (isFromFirestore) return
////////        val docId = "${entry.date}_${entry.shift}_${entry.model}_${entry.color}".replace(" ", "_")
////////
////////        firestore.collection("model_production")
////////            .document(docId)
////////            .set(entry.toFirestoreMap())
////////            .addOnSuccessListener {
////////                Log.d("FirebasePush", "ModelProduction pushed: $docId")
////////            }
////////            .addOnFailureListener { e ->
////////                Log.e("FirebasePush", "Failed to push ModelProduction: $docId", e)
////////            }
////////    }
////////
////////
////////
////////
////////
//////////    fun pushModelProduction(entry: ModelProduction, context: Context) {
//////////        if (isFromFirestore) return
//////////        val docId = "${entry.date}_${entry.shift}_${entry.model}_${entry.color}".replace(" ", "_")
//////////        val data = mapOf(
//////////            "model" to entry.model,
//////////            "color" to entry.color,
//////////            "date" to entry.date,
//////////            "shift" to entry.shift,
//////////            "openingBalance" to entry.openingBalance,
//////////            "produced" to entry.produced,
//////////            "rejection" to entry.rejection
//////////        )
//////////
//////////        firestore.collection("model_production")
//////////            .document(docId)
//////////            .set(data)
//////////            .addOnSuccessListener {
//////////                Log.d("FirebasePush", "ModelProduction pushed: $docId")
//////////            }
//////////            .addOnFailureListener { e ->
//////////                Log.e("FirebasePush", "Failed to push ModelProduction: $docId", e)
//////////            }
//////////    }
////////
////////    // 🧠 Firestore Snapshot Listeners
////////
//////////    fun listenToDashboardUpdates(context: Context, onUpdate: (DashboardRow) -> Unit) {
//////////        firestore.collection("dashboard")
//////////            .addSnapshotListener { snapshots, error ->
//////////                if (error != null || snapshots == null) return@addSnapshotListener
//////////
//////////                for (change in snapshots.documentChanges) {
//////////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
//////////                        val data = change.document.data
//////////                        try {
//////////                            isFromFirestore = true
//////////                            val row = DashboardRow(
//////////                                date = data["date"] as String,
//////////                                shift = data["shift"] as String,
//////////                                model = data["model"] as String,
//////////                                color = data["color"] as String,
//////////                                partName = data["partName"] as String,
//////////                                planned = (data["planned"] as? Long ?: 0L).toInt(),
//////////                                ob = (data["ob"] as? Long ?: 0L).toInt(),
//////////                                dispatch = (data["dispatch"] as? Long ?: 0L).toInt(),
//////////                                received = (data["received"] as? Long ?: 0L).toInt(),
//////////                                remainingPs = (data["remainingPs"] as? Long ?: 0L).toInt(),
//////////                                remainingVa = (data["remainingVa"] as? Long ?: 0L).toInt(),
//////////                                produced = (data["produced"] as? Long ?: 0L).toInt(),
//////////                                rejection = (data["rejection"] as? Long ?: 0L).toInt(),
//////////                                cb = (data["cb"] as? Long ?: 0L).toInt()
//////////                            )
//////////                            onUpdate(row)
//////////                        } catch (e: Exception) {
//////////                            Log.e("FirestoreParse", "Error parsing dashboard", e)
//////////                        } finally {
//////////                            isFromFirestore = false
//////////                        }
//////////                    }
//////////                }
//////////            }
//////////    }
////////
////////
////////    fun listenToDashboardUpdates(context: Context, onUpdate: (DashboardRow) -> Unit) {
////////        firestore.collection("dashboard")
////////            .addSnapshotListener { snapshots, error ->
////////                if (error != null || snapshots == null) return@addSnapshotListener
////////
////////                for (change in snapshots.documentChanges) {
////////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////////                        try {
////////                            isFromFirestore = true
////////                            val row = DashboardRow.fromFirestoreMap(change.document.data)
////////                            if (row != null) onUpdate(row)
////////                        } catch (e: Exception) {
////////                            Log.e("FirestoreParse", "Error parsing DashboardRow", e)
////////                        } finally {
////////                            isFromFirestore = false
////////                        }
////////                    }
////////                }
////////            }
////////    }
////////
////////
////////    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
////////        firestore.collection("scanned_parts")
////////            .addSnapshotListener { snapshots, error ->
////////                if (error != null || snapshots == null) return@addSnapshotListener
////////
////////                for (change in snapshots.documentChanges) {
////////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////////                        try {
////////                            isFromFirestore = true
////////                            val scanned = ScannedPart.fromFirestoreMap(change.document.data)
////////                            onUpdate(scanned)
////////                        } catch (e: Exception) {
////////                            Log.e("FirestoreParse", "ScannedPart failed", e)
////////                        } finally {
////////                            isFromFirestore = false
////////                        }
////////                    }
////////                }
////////            }
////////    }
////////
////////    fun listenToUsedPartsUpdates(onUpdate: (UsedPart) -> Unit) {
////////        firestore.collection("used_parts")
////////            .addSnapshotListener { snapshots, error ->
////////                if (error != null || snapshots == null) return@addSnapshotListener
////////
////////                for (change in snapshots.documentChanges) {
////////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////////                        try {
////////                            isFromFirestore = true
////////                            val usedPart = UsedPart(
////////                                partName = change.document.getString("partName") ?: "",
////////                                quantity = (change.document.getLong("quantity") ?: 0).toInt(),
////////                                timestamp = change.document.getLong("timestamp") ?: System.currentTimeMillis()
////////                            )
////////                            onUpdate(usedPart)
////////                        } catch (e: Exception) {
////////                            Log.e("FirestoreParse", "UsedPart failed", e)
////////                        } finally {
////////                            isFromFirestore = false
////////                        }
////////                    }
////////                }
////////            }
////////    }
////////
//////////    fun listenToPlanUpdates(onUpdate: (PlanEntry) -> Unit) {
//////////        firestore.collection("plans")
//////////            .addSnapshotListener { snapshots, error ->
//////////                if (error != null || snapshots == null) return@addSnapshotListener
//////////
//////////                for (change in snapshots.documentChanges) {
//////////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
//////////                        try {
//////////                            isFromFirestore = true
//////////                            val plan = PlanEntry(
//////////                                sequence = (change.document.getLong("sequence") ?: 0).toInt(),
//////////                                model = change.document.getString("model") ?: "",
//////////                                quantity = (change.document.getLong("quantity") ?: 0).toInt(),
//////////                                date = change.document.getString("date") ?: "",
//////////                                shift = change.document.getString("shift") ?: "",
//////////                                color = change.document.getString("color") ?: ""
//////////                            )
//////////                            onUpdate(plan)
//////////                        } catch (e: Exception) {
//////////                            Log.e("FirestoreParse", "Plan failed", e)
//////////                        } finally {
//////////                            isFromFirestore = false
//////////                        }
//////////                    }
//////////                }
//////////            }
//////////    }
////////
////////    fun listenToPlanUpdates(onUpdate: (PlanEntry) -> Unit) {
////////        firestore.collection("plans")
////////            .addSnapshotListener { snapshots, error ->
////////                if (error != null || snapshots == null) return@addSnapshotListener
////////
////////                for (change in snapshots.documentChanges) {
////////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////////                        try {
////////                            isFromFirestore = true
////////                            val plan = PlanEntry.fromFirestoreMap(change.document.data)
////////                            if (plan != null) onUpdate(plan)
////////                        } catch (e: Exception) {
////////                            Log.e("FirestoreParse", "Plan failed", e)
////////                        } finally {
////////                            isFromFirestore = false
////////                        }
////////                    }
////////                }
////////            }
////////    }
////////    fun listenToModelProductionUpdates(onUpdate: (ModelProduction) -> Unit) {
////////        firestore.collection("model_production")
////////            .addSnapshotListener { snapshots, error ->
////////                if (error != null || snapshots == null) return@addSnapshotListener
////////
////////                for (change in snapshots.documentChanges) {
////////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////////                        try {
////////                            isFromFirestore = true
////////                            val entry = ModelProduction.fromFirestoreMap(change.document.data)
////////                            if (entry != null) onUpdate(entry)
////////                        } catch (e: Exception) {
////////                            Log.e("FirestoreParse", "ModelProduction failed", e)
////////                        } finally {
////////                            isFromFirestore = false
////////                        }
////////                    }
////////                }
////////            }
////////
////////    }
////////
////////    fun pullAllModelProduction(onResult: (List<ModelProduction>) -> Unit) {
////////        firestore.collection("model_production")
////////            .get()
////////            .addOnSuccessListener { documents ->
////////                val result = mutableListOf<ModelProduction>()
////////                for (doc in documents) {
////////                    try {
////////                        val entry = ModelProduction.fromFirestoreMap(doc.data)
////////                        if (entry != null) result.add(entry)
////////                    } catch (e: Exception) {
////////                        Log.e("ModelProdPull", "Failed to parse", e)
////////                    }
////////                }
////////                onResult(result)
////////            }
////////    }
////////
////////
////////
////////
//////////    fun listenToModelProductionUpdates(onUpdate: (ModelProduction) -> Unit) {
//////////        firestore.collection("model_production")
//////////            .addSnapshotListener { snapshots, error ->
//////////                if (error != null || snapshots == null) return@addSnapshotListener
//////////
//////////                for (change in snapshots.documentChanges) {
//////////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
//////////                        try {
//////////                            isFromFirestore = true
//////////                            val entry = ModelProduction(
//////////                                model = change.document.getString("model") ?: "",
//////////                                color = change.document.getString("color") ?: "",
//////////                                date = change.document.getString("date") ?: "",
//////////                                shift = change.document.getString("shift") ?: "",
//////////                                openingBalance = (change.document.getLong("openingBalance") ?: 0).toInt(),
//////////                                produced = (change.document.getLong("produced") ?: 0).toInt(),
//////////                                rejection = (change.document.getLong("rejection") ?: 0).toInt()
//////////                            )
//////////                            onUpdate(entry)
//////////                        } catch (e: Exception) {
//////////                            Log.e("FirestoreParse", "ModelProduction failed", e)
//////////                        } finally {
//////////                            isFromFirestore = false
//////////                        }
//////////                    }
//////////                }
//////////            }
//////////    }
////////
//////////    fun pullAllDashboardRows(onResult: (List<DashboardRow>) -> Unit) {
//////////        firestore.collection("dashboard")
//////////            .get()
//////////            .addOnSuccessListener { documents ->
//////////                val result = mutableListOf<DashboardRow>()
//////////                for (doc in documents) {
//////////                    val data = doc.data
//////////                    try {
//////////                        val row = DashboardRow(
//////////                            date = data["date"] as String,
//////////                            shift = data["shift"] as String,
//////////                            model = data["model"] as String,
//////////                            color = data["color"] as String,
//////////                            partName = data["partName"] as String,
//////////                            planned = (data["planned"] as? Long ?: 0L).toInt(),
//////////                            ob = (data["ob"] as? Long ?: 0L).toInt(),
//////////                            dispatch = (data["dispatch"] as? Long ?: 0L).toInt(),
//////////                            received = (data["received"] as? Long ?: 0L).toInt(),
//////////                            remainingPs = (data["remainingPs"] as? Long ?: 0L).toInt(),
//////////                            remainingVa = (data["remainingVa"] as? Long ?: 0L).toInt(),
//////////                            produced = (data["produced"] as? Long ?: 0L).toInt(),
//////////                            rejection = (data["rejection"] as? Long ?: 0L).toInt(),
//////////                            cb = (data["cb"] as? Long ?: 0L).toInt()
//////////                        )
//////////                        result.add(row)
//////////                    } catch (e: Exception) {
//////////                        Log.e("DashboardPull", "Failed to parse", e)
//////////                    }
//////////                }
//////////                onResult(result)
//////////            }
//////////    }
////////
////////
////////    fun pullAllDashboardRows(onResult: (List<DashboardRow>) -> Unit) {
////////        firestore.collection("dashboard")
////////            .get()
////////            .addOnSuccessListener { documents ->
////////                val result = mutableListOf<DashboardRow>()
////////                for (doc in documents) {
////////                    try {
////////                        val row = DashboardRow.fromFirestoreMap(doc.data)
////////                        if (row != null) result.add(row)
////////                    } catch (e: Exception) {
////////                        Log.e("DashboardPull", "Failed to parse DashboardRow", e)
////////                    }
////////                }
////////                onResult(result)
////////            }
////////    }
////////
////////
//////////    fun pullAllPlans(onResult: (List<PlanEntry>) -> Unit) {
//////////        firestore.collection("plans")
//////////            .get()
//////////            .addOnSuccessListener { documents ->
//////////                val result = mutableListOf<PlanEntry>()
//////////                for (doc in documents) {
//////////                    val data = doc.data
//////////                    try {
//////////                        val plan = PlanEntry(
//////////                            sequence = (data["sequence"] as? Long ?: 0L).toInt(),
//////////                            model = data["model"] as String,
//////////                            quantity = (data["quantity"] as? Long ?: 0L).toInt(),
//////////                            date = data["date"] as String,
//////////                            shift = data["shift"] as String,
//////////                            color = data["color"] as String
//////////                        )
//////////                        result.add(plan)
//////////                    } catch (e: Exception) {
//////////                        Log.e("PlanPull", "Failed to parse", e)
//////////                    }
//////////                }
//////////                onResult(result)
//////////            }
//////////    }
////////
////////
////////    fun pullAllPlans(onResult: (List<PlanEntry>) -> Unit) {
////////        firestore.collection("plans")
////////            .get()
////////            .addOnSuccessListener { documents ->
////////                val result = mutableListOf<PlanEntry>()
////////                for (doc in documents) {
////////                    try {
////////                        val plan = PlanEntry.fromFirestoreMap(doc.data)
////////                        if (plan != null) result.add(plan)
////////                    } catch (e: Exception) {
////////                        Log.e("PlanPull", "Failed to parse", e)
////////                    }
////////                }
////////                onResult(result)
////////            }
////////    }
////////
////////
////////
////////
////////
////////
////////    private fun sendToGoogleSheet(data: Map<String, Any?>, context: Context, logTag: String) {
////////        val taggedData = data.toMutableMap().apply {
////////            put("source", if (logTag.contains("Scanned", ignoreCase = true)) "scanned" else "dashboard")
//////        }
//////
//////        val requestBody = JSONObject(taggedData as Map<*, *>).toString()
//////
//////        val request = object : StringRequest(Method.POST, GOOGLE_SHEET_WEBHOOK_URL,
//////            { response -> Log.d(logTag, "Success: $response") },
//////            { error -> Log.e(logTag, "Volley Error: ${error.message}", error) }
//////        ) {
//////            override fun getBody(): ByteArray = requestBody.toByteArray(Charsets.UTF_8)
//////            override fun getBodyContentType(): String = "application/json; charset=utf-8"
//////            override fun getParams(): MutableMap<String, String> = mutableMapOf()
//////        }
//////
//////        request.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
//////        Volley.newRequestQueue(context).add(request)
//////    }
////////}
//////
////package com.example.parttracker.firebase
////
////import android.content.Context
////import android.util.Log
////import com.android.volley.DefaultRetryPolicy
////import com.android.volley.toolbox.StringRequest
////import com.android.volley.toolbox.Volley
////import com.example.parttracker.data.UsedPart
////import com.example.parttracker.model.* // Ensure all model classes are imported
////import com.google.firebase.firestore.DocumentChange
////import com.google.firebase.firestore.FirebaseFirestore
////import org.json.JSONObject
////
////object FirebaseSyncManager {
////
////    private val firestore = FirebaseFirestore.getInstance()
////
////    private const val GOOGLE_SHEET_WEBHOOK_URL =
////        "https://script.google.com/macros/s/AKfycbwk9HphRofyXtPUp5piF25_MGV4vQX-lEWJX_MIPv0UC2h-ntbBKySsTp5ruZvvJ9dV/exec"
////
////    // 🔁 Loop Prevention Flags - Keep the flag
////    @Volatile private var isFromFirestore = false
////
////    // ✅ Push ScannedPart - Keep isFromFirestore check (direct input)
////    fun pushScannedPart(scannedPart: ScannedPart, context: Context) {
////        if (isFromFirestore) return // Keep this check for direct input events
////        val docId = "${scannedPart.productId}_${scannedPart.timestamp}"
////        val data = scannedPart.toFirestoreMap()
////
////        firestore.collection("scanned_parts")
////            .document(docId)
////            .set(data)
////            .addOnSuccessListener {
////                Log.d("FirestorePush", "ScannedPart pushed: $docId")
////                sendToGoogleSheet(data, context, "GoogleSheet-Scanned")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirestorePush", "ScannedPart push failed: $docId", e)
////            }
////    }
////
////    // ✅ Push DashboardRow - REMOVED isFromFirestore check (derived data)
////    fun pushDashboardRow(
////        row: DashboardRow,
////        context: Context,
////        onSuccess: () -> Unit = {},
////        onFailure: (Exception) -> Unit = {}
////    ) {
////        // if (isFromFirestore) return // <-- REMOVED THIS LINE for derived data sync
////
////        // Updated documentId for more specificity
////        val documentId = "${row.date}_${row.shift}_${row.model}_${row.color}_${row.partName}".replace("/", "_")
////
////        firestore.collection("dashboard")
////            .document(documentId)
////            .set(row.toFirestoreMap()) // Using helper function
////            .addOnSuccessListener {
////                Log.d("FirestorePush", "DashboardRow pushed: $documentId")
////                onSuccess()
////                sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
////            }
////            .addOnFailureListener {
////                Log.e("FirestorePush", "DashboardRow push failed: $documentId", it)
////                onFailure(it)
////            }
////    }
////
////    // ✅ Push UsedPart - Keep isFromFirestore check (direct input)
////    fun pushUsedPart(usedPart: UsedPart, context: Context) {
////        if (isFromFirestore) return // Keep this check for direct input events
////        val docId = "${usedPart.partName}_${usedPart.timestamp}"
////        val data = mapOf(
////            "partName" to usedPart.partName,
////            "quantity" to usedPart.quantity,
////            "timestamp" to usedPart.timestamp
////        )
////
////        firestore.collection("used_parts")
////            .document(docId)
////            .set(data)
////            .addOnSuccessListener {
////                Log.d("FirestorePush", "UsedPart pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirestorePush", "UsedPart push failed: $docId", e)
////            }
////    }
////
////    // ✅ Push PlanEntry - REMOVED isFromFirestore check (derived data / primary sync entity)
////    fun pushPlanEntry(plan: PlanEntry, context: Context) {
////        // if (isFromFirestore) return // <-- REMOVED THIS LINE for primary sync entity
////
////        val safeModel = plan.model.replace("/", "_")
////        val safeColor = plan.color.replace("/", "_")
////        val docId = "${plan.date}_${plan.shift}_${safeModel}_${safeColor}".replace(" ", "_")
////
////        firestore.collection("plans")
////            .document(docId)
////            .set(plan.toFirestoreMap()) // Using helper function
////            .addOnSuccessListener {
////                Log.d("FirebasePush", "PlanEntry pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirebasePush", "Failed to push PlanEntry: $docId", e)
////            }
////    }
////
////    // ✅ Push ModelProduction - REMOVED isFromFirestore check (derived data / primary sync entity)
////    fun pushModelProduction(entry: ModelProduction, context: Context) {
////        // if (isFromFirestore) return // <-- REMOVED THIS LINE for primary sync entity
////        val docId = "${entry.date}_${entry.shift}_${entry.model}_${entry.color}".replace(" ", "_")
////
////        firestore.collection("model_production")
////            .document(docId)
////            .set(entry.toFirestoreMap()) // Using helper function
////            .addOnSuccessListener {
////                Log.d("FirebasePush", "ModelProduction pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirebasePush", "Failed to push ModelProduction: $docId", e)
////            }
////    }
////
////    // 🧠 Firestore Snapshot Listeners - Keep isFromFirestore logic here
////    // This is where you set the flag when receiving updates from Firestore
////    fun listenToDashboardUpdates(context: Context, onUpdate: (DashboardRow) -> Unit) {
////        firestore.collection("dashboard")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true // Set flag when update originates from Firestore
////                            val row = DashboardRow.fromFirestoreMap(change.document.data)
////                            if (row != null) onUpdate(row)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "Error parsing DashboardRow", e)
////                        } finally {
////                            isFromFirestore = false // Reset flag after processing incoming snapshot
////                        }
////                    }
////                }
////            }
////    }
////
////    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
////        firestore.collection("scanned_parts")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val scanned = ScannedPart.fromFirestoreMap(change.document.data)
////                            onUpdate(scanned)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "ScannedPart failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun listenToUsedPartsUpdates(onUpdate: (UsedPart) -> Unit) {
////        firestore.collection("used_parts")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val usedPart = UsedPart(
////                                partName = change.document.getString("partName") ?: "",
////                                quantity = (change.document.getLong("quantity") ?: 0).toInt(),
////                                timestamp = change.document.getLong("timestamp") ?: System.currentTimeMillis()
////                            )
////                            onUpdate(usedPart)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "UsedPart failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun listenToPlanUpdates(onUpdate: (PlanEntry) -> Unit) {
////        firestore.collection("plans")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val plan = PlanEntry.fromFirestoreMap(change.document.data)
////                            if (plan != null) onUpdate(plan)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "Plan failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun listenToModelProductionUpdates(onUpdate: (ModelProduction) -> Unit) {
////        firestore.collection("model_production")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val entry = ModelProduction.fromFirestoreMap(change.document.data)
////                            if (entry != null) onUpdate(entry)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "ModelProduction failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    // Pull all methods - Using fromFirestoreMap() helper functions
////    fun pullAllDashboardRows(onResult: (List<DashboardRow>) -> Unit) {
////        firestore.collection("dashboard")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<DashboardRow>()
////                for (doc in documents) {
////                    try {
////                        val row = DashboardRow.fromFirestoreMap(doc.data)
////                        if (row != null) result.add(row)
////                    } catch (e: Exception) {
////                        Log.e("DashboardPull", "Failed to parse DashboardRow", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    fun pullAllPlans(onResult: (List<PlanEntry>) -> Unit) {
////        firestore.collection("plans")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<PlanEntry>()
////                for (doc in documents) {
////                    try {
////                        val plan = PlanEntry.fromFirestoreMap(doc.data)
////                        if (plan != null) result.add(plan)
////                    } catch (e: Exception) {
////                        Log.e("PlanPull", "Failed to parse", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    fun pullAllModelProduction(onResult: (List<ModelProduction>) -> Unit) {
////        firestore.collection("model_production")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<ModelProduction>()
////                for (doc in documents) {
////                    try {
////                        val entry = ModelProduction.fromFirestoreMap(doc.data)
////                        if (entry != null) result.add(entry)
////                    } catch (e: Exception) {
////                        Log.e("ModelProdPull", "Failed to parse", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    // Google Sheet Integration
////    private fun sendToGoogleSheet(data: Map<String, Any?>, context: Context, logTag: String) {
////        val taggedData = data.toMutableMap().apply {
////            put("source", if (logTag.contains("Scanned", ignoreCase = true)) "scanned" else "dashboard")
////        }
////
////        val requestBody = JSONObject(taggedData as Map<*, *>).toString()
////
////        val request = object : StringRequest(Method.POST, GOOGLE_SHEET_WEBHOOK_URL,
////            { response -> Log.d(logTag, "Success: $response") },
////            { error -> Log.e(logTag, "Volley Error: ${error.message}", error) }
////        ) {
////            override fun getBody(): ByteArray = requestBody.toByteArray(Charsets.UTF_8)
////            override fun getBodyContentType(): String = "application/json; charset=utf-8"
////            override fun getParams(): MutableMap<String, String> = mutableMapOf()
////        }
////
////        request.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
////        Volley.newRequestQueue(context).add(request)
////    }
////}
//
//
////
////package com.example.parttracker.firebase
////
////import android.content.Context
////import android.util.Log
////import com.android.volley.DefaultRetryPolicy
////import com.android.volley.toolbox.StringRequest
////import com.android.volley.toolbox.Volley
////import com.example.parttracker.data.UsedPart
////import com.example.parttracker.model.*
////import com.google.firebase.firestore.DocumentChange
////import com.google.firebase.firestore.FirebaseFirestore
////import org.json.JSONObject
////
////object FirebaseSyncManager {
////
////    private val firestore = FirebaseFirestore.getInstance()
////    private val seenScannedDocIds = mutableSetOf<String>()
////
////
////    private const val GOOGLE_SHEET_WEBHOOK_URL =
////        "https://script.google.com/macros/s/AKfycbwk9HphRofyXtPUp5piF25_MGV4vQX-lEWJX_MIPv0UC2h-ntbBKySsTp5ruZvvJ9dV/exec"
////
////    @Volatile private var isFromFirestore = false
////
////    // ✅ De-bounce map to avoid repeat pushes
////    private val lastPushedDashboardRowMap = mutableMapOf<String, DashboardRow>()
////
////    fun pushScannedPart(scannedPart: ScannedPart, context: Context) {
////        if (isFromFirestore) return
//////
////        val docId = "${scannedPart.productId}_${scannedPart.timestamp}" // Now timestamp is Long → always unique
////
////        val data = scannedPart.toFirestoreMap()
////
////        firestore.collection("scanned_parts")
////            .document(docId)
////            .set(data)
////            .addOnSuccessListener {
////                Log.d("FirestorePush", "ScannedPart pushed: $docId")
////                sendToGoogleSheet(data, context, "GoogleSheet-Scanned")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirestorePush", "ScannedPart push failed: $docId", e)
////            }
////    }
////
////    fun pushDashboardRow(
////        row: DashboardRow,
////        context: Context,
////        onSuccess: () -> Unit = {},
////        onFailure: (Exception) -> Unit = {}
////    ) {
////        val documentId = "${row.date}_${row.shift}_${row.model}_${row.color}_${row.partName}".replace("/", "_")
////
////        val last = lastPushedDashboardRowMap[documentId]
////        if (last == row) {
////            Log.d("FirestoreDebounce", "Skipping identical DashboardRow for $documentId")
////            return
////        }
////
////        lastPushedDashboardRowMap[documentId] = row
////
////        firestore.collection("dashboard")
////            .document(documentId)
////            .set(row.toFirestoreMap())
////            .addOnSuccessListener {
////                Log.d("FirestorePush", "DashboardRow pushed: $documentId")
////                onSuccess()
////                sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
////            }
////            .addOnFailureListener {
////                Log.e("FirestorePush", "DashboardRow push failed: $documentId", it)
////                onFailure(it)
////            }
////    }
////
////    fun pushUsedPart(usedPart: UsedPart, context: Context) {
////        if (isFromFirestore) return
////        val docId = "${usedPart.partName}_${usedPart.timestamp}"
////        val data = mapOf(
////            "partName" to usedPart.partName,
////            "quantity" to usedPart.quantity,
////            "timestamp" to usedPart.timestamp
////        )
////
////        firestore.collection("used_parts")
////            .document(docId)
////            .set(data)
////            .addOnSuccessListener {
////                Log.d("FirestorePush", "UsedPart pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirestorePush", "UsedPart push failed: $docId", e)
////            }
////    }
////
////    fun pushPlanEntry(plan: PlanEntry, context: Context) {
////        val safeModel = plan.model.replace("/", "_")
////        val safeColor = plan.color.replace("/", "_")
////        val docId = "${plan.date}_${plan.shift}_${safeModel}_${safeColor}".replace(" ", "_")
////
////        firestore.collection("plans")
////            .document(docId)
////            .set(plan.toFirestoreMap())
////            .addOnSuccessListener {
////                Log.d("FirebasePush", "PlanEntry pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirebasePush", "Failed to push PlanEntry: $docId", e)
////            }
////    }
////
////    fun pushModelProduction(entry: ModelProduction, context: Context) {
////        val docId = "${entry.date}_${entry.shift}_${entry.model}_${entry.color}".replace(" ", "_")
////
////        firestore.collection("model_production")
////            .document(docId)
////            .set(entry.toFirestoreMap())
////            .addOnSuccessListener {
////                Log.d("FirebasePush", "ModelProduction pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirebasePush", "Failed to push ModelProduction: $docId", e)
////            }
////    }
////
////    fun listenToDashboardUpdates(context: Context, onUpdate: (DashboardRow) -> Unit) {
////        firestore.collection("dashboard")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val row = DashboardRow.fromFirestoreMap(change.document.data)
////                            if (row != null) onUpdate(row)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "Error parsing DashboardRow", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
//////    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
//////        firestore.collection("scanned_parts")
//////            .addSnapshotListener { snapshots, error ->
//////                if (error != null || snapshots == null) return@addSnapshotListener
//////
//////                for (change in snapshots.documentChanges) {
//////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
//////                        try {
//////                            isFromFirestore = true
//////                            val scanned = ScannedPart.fromFirestoreMap(change.document.data)
//////                            onUpdate(scanned)
//////                        } catch (e: Exception) {
//////                            Log.e("FirestoreParse", "ScannedPart failed", e)
//////                        } finally {
//////                            isFromFirestore = false
//////                        }
//////                    }
//////                }
//////            }
//////    }
////
////
////    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
////        firestore.collection("scanned_parts")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    val docId = change.document.id
////
////                    // ✅ Skip already seen documents
////                    if (seenScannedDocIds.contains(docId)) {
////                        Log.d("FirestoreSync", "Duplicate scan skipped: $docId")
////                        continue
////                    }
////
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////
////                            val scanned = ScannedPart.fromFirestoreMap(change.document.data)
////
////                            // ✅ Mark as seen
////                            seenScannedDocIds.add(docId)
////
////                            // ✅ Process only once
////                            onUpdate(scanned)
////
////                            Log.d("FirestoreSync", "Scan processed: $docId")
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "ScannedPart failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////
////    fun listenToUsedPartsUpdates(onUpdate: (UsedPart) -> Unit) {
////        firestore.collection("used_parts")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val usedPart = UsedPart(
////                                partName = change.document.getString("partName") ?: "",
////                                quantity = (change.document.getLong("quantity") ?: 0).toInt(),
////                                timestamp = change.document.getLong("timestamp") ?: System.currentTimeMillis()
////                            )
////                            onUpdate(usedPart)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "UsedPart failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun listenToPlanUpdates(onUpdate: (PlanEntry) -> Unit) {
////        firestore.collection("plans")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val plan = PlanEntry.fromFirestoreMap(change.document.data)
////                            if (plan != null) onUpdate(plan)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "Plan failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun listenToModelProductionUpdates(onUpdate: (ModelProduction) -> Unit) {
////        firestore.collection("model_production")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val entry = ModelProduction.fromFirestoreMap(change.document.data)
////                            if (entry != null) onUpdate(entry)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "ModelProduction failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun pullAllDashboardRows(onResult: (List<DashboardRow>) -> Unit) {
////        firestore.collection("dashboard")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<DashboardRow>()
////                for (doc in documents) {
////                    try {
////                        val row = DashboardRow.fromFirestoreMap(doc.data)
////                        if (row != null) result.add(row)
////                    } catch (e: Exception) {
////                        Log.e("DashboardPull", "Failed to parse DashboardRow", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    fun pullAllPlans(onResult: (List<PlanEntry>) -> Unit) {
////        firestore.collection("plans")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<PlanEntry>()
////                for (doc in documents) {
////                    try {
////                        val plan = PlanEntry.fromFirestoreMap(doc.data)
////                        if (plan != null) result.add(plan)
////                    } catch (e: Exception) {
////                        Log.e("PlanPull", "Failed to parse", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    fun pullAllModelProduction(onResult: (List<ModelProduction>) -> Unit) {
////        firestore.collection("model_production")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<ModelProduction>()
////                for (doc in documents) {
////                    try {
////                        val entry = ModelProduction.fromFirestoreMap(doc.data)
////                        if (entry != null) result.add(entry)
////                    } catch (e: Exception) {
////                        Log.e("ModelProdPull", "Failed to parse", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    private fun sendToGoogleSheet(data: Map<String, Any?>, context: Context, logTag: String) {
////        val taggedData = data.toMutableMap().apply {
////            put("source", if (logTag.contains("Scanned", ignoreCase = true)) "scanned" else "dashboard")
////        }
////
////        val requestBody = JSONObject(taggedData as Map<*, *>).toString()
////
////        val request = object : StringRequest(Method.POST, GOOGLE_SHEET_WEBHOOK_URL,
////            { response -> Log.d(logTag, "Success: $response") },
////            { error -> Log.e(logTag, "Volley Error: ${error.message}", error) }
////        ) {
////            override fun getBody(): ByteArray = requestBody.toByteArray(Charsets.UTF_8)
////            override fun getBodyContentType(): String = "application/json; charset=utf-8"
////            override fun getParams(): MutableMap<String, String> = mutableMapOf()
////        }
////
////        request.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
////        Volley.newRequestQueue(context).add(request)
////    }
////}
//
////
////package com.example.parttracker.firebase
////
////import android.content.Context
////import android.util.Log
////import androidx.room.RoomDatabase
////import com.android.volley.DefaultRetryPolicy
////import com.android.volley.toolbox.StringRequest
////import com.android.volley.toolbox.Volley
////import com.example.parttracker.data.PartDatabase
////import com.example.parttracker.data.UsedPart
////import com.example.parttracker.model.*
////import com.google.firebase.firestore.DocumentChange
////import com.google.firebase.firestore.FirebaseFirestore
////import com.google.firebase.firestore.SetOptions
////import org.json.JSONObject
////import kotlinx.coroutines.CoroutineScope
////import kotlinx.coroutines.Dispatchers
////import kotlinx.coroutines.launch
////import kotlinx.coroutines.withContext
////
////
////object FirebaseSyncManager {
////
////    private val firestore = FirebaseFirestore.getInstance()
////    private val seenScannedDocIds = mutableSetOf<String>()
////    private val lastPushedDashboardRowMap = mutableMapOf<String, DashboardRow>()
////    @Volatile private var isFromFirestore = false
////
////    private const val GOOGLE_SHEET_WEBHOOK_URL =
////        "https://script.google.com/macros/s/AKfycbwk9HphRofyXtPUp5piF25_MGV4vQX-lEWJX_MIPv0UC2h-ntbBKySsTp5ruZvvJ9dV/exec"
////
////    fun pushScannedPart(scannedPart: ScannedPart, context: Context) {
////        if (isFromFirestore) return
////        val docId = "${scannedPart.productId}_${scannedPart.trolleyNumber}_${scannedPart.date}_${scannedPart.shift}"
////        val data = scannedPart.toFirestoreMap()
////
////        firestore.collection("scanned_parts")
////            .document(docId)
////            .set(data)
////            .addOnSuccessListener {
////                Log.d("FirestorePush", "ScannedPart pushed: $docId")
////                sendToGoogleSheet(data, context, "GoogleSheet-Scanned")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirestorePush", "ScannedPart push failed: $docId", e)
////            }
////    }
////
////    fun pushDashboardRow(
////        row: DashboardRow,
////        context: Context,
////        onSuccess: () -> Unit = {},
////        onFailure: (Exception) -> Unit = {}
////    ) {
////        val documentId = "${row.date}_${row.shift}_${row.model}_${row.color}_${row.partName}".replace("/", "_")
////
////        // Fetch existing doc to compare before pushing
////        firestore.collection("dashboard")
////            .document(documentId)
////            .get()
////            .addOnSuccessListener { document ->
////                val existing = DashboardRow.fromFirestoreMap(document.data ?: emptyMap())
////                if (existing == row) {
////                    Log.d("FirestoreDebounce", "Firestore data identical, skipping: $documentId")
////                    return@addOnSuccessListener
////                }
////
////                lastPushedDashboardRowMap[documentId] = row
////
////                firestore.collection("dashboard")
////                    .document(documentId)
////                    .set(row.toFirestoreMap(), SetOptions.merge()) // use merge for safe overwrites
////                    .addOnSuccessListener {
////                        Log.d("FirestorePush", "DashboardRow pushed: $documentId")
////                        onSuccess()
////                        sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
////                    }
////                    .addOnFailureListener {
////                        Log.e("FirestorePush", "DashboardRow push failed: $documentId", it)
////                        onFailure(it)
////                    }
////
////            }
////            .addOnFailureListener {
////                Log.e("FirestoreRead", "Failed to read existing DashboardRow: $documentId", it)
////
////                // If read fails, fallback to normal push
////                firestore.collection("dashboard")
////                    .document(documentId)
////                    .set(row.toFirestoreMap(), SetOptions.merge())
////                    .addOnSuccessListener {
////                        Log.d("FirestorePush", "DashboardRow pushed (fallback): $documentId")
////                        onSuccess()
////                        sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
////                    }
////                    .addOnFailureListener {
////                        Log.e("FirestorePush", "DashboardRow push failed (fallback): $documentId", it)
////                        onFailure(it)
////                    }
////            }
////    }
////
////    fun pushUsedPart(usedPart: UsedPart, context: Context) {
////        if (isFromFirestore) return
////        val docId = "${usedPart.partName}_${usedPart.timestamp}"
////        val data = mapOf(
////            "partName" to usedPart.partName,
////            "quantity" to usedPart.quantity,
////            "timestamp" to usedPart.timestamp
////        )
////
////        firestore.collection("used_parts")
////            .document(docId)
////            .set(data)
////            .addOnSuccessListener {
////                Log.d("FirestorePush", "UsedPart pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirestorePush", "UsedPart push failed: $docId", e)
////            }
////    }
////
////    fun pushPlanEntry(plan: PlanEntry, context: Context) {
////        val docId = "${plan.date}_${plan.shift}_${plan.model.replace("/", "_")}_${plan.color.replace("/", "_")}"
////        firestore.collection("plans")
////            .document(docId)
////            .set(plan.toFirestoreMap())
////            .addOnSuccessListener {
////                Log.d("FirebasePush", "PlanEntry pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirebasePush", "Failed to push PlanEntry: $docId", e)
////            }
////    }
////
////    fun pushModelProduction(entry: ModelProduction, context: Context) {
////        val docId = "${entry.date}_${entry.shift}_${entry.model}_${entry.color}".replace(" ", "_")
////        firestore.collection("model_production")
////            .document(docId)
////            .set(entry.toFirestoreMap())
////            .addOnSuccessListener {
////                Log.d("FirebasePush", "ModelProduction pushed: $docId")
////            }
////            .addOnFailureListener { e ->
////                Log.e("FirebasePush", "Failed to push ModelProduction: $docId", e)
////            }
////    }
////
////    fun listenToDashboardUpdates(context: Context, onUpdate: (DashboardRow) -> Unit) {
////        firestore.collection("dashboard")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val row = DashboardRow.fromFirestoreMap(change.document.data)
////                            if (row != null) onUpdate(row)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "Error parsing DashboardRow", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
//////    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
//////        firestore.collection("scanned_parts")
//////            .addSnapshotListener { snapshots, error ->
//////                if (error != null || snapshots == null) return@addSnapshotListener
//////
//////                for (change in snapshots.documentChanges) {
//////                    val docId = change.document.id
//////                    if (seenScannedDocIds.contains(docId)) {
//////                        Log.d("FirestoreSync", "Duplicate scan skipped: $docId")
//////                        continue
//////                    }
//////
//////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
//////                        try {
//////                            isFromFirestore = true
//////                            val scanned = ScannedPart.fromFirestoreMap(change.document.data)
//////                            seenScannedDocIds.add(docId)
//////                            onUpdate(scanned)
//////                            Log.d("FirestoreSync", "Scan processed: $docId")
//////                        } catch (e: Exception) {
//////                            Log.e("FirestoreParse", "ScannedPart failed", e)
//////                        } finally {
//////                            isFromFirestore = false
//////                        }
//////                    }
//////                }
//////            }
//////    }
////
////
//////    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
//////        firestore.collection("scanned_parts")
//////            .addSnapshotListener { snapshots, error ->
//////                if (error != null || snapshots == null) return@addSnapshotListener
//////
//////                for (change in snapshots.documentChanges) {
//////                    val docId = change.document.id
//////                    if (seenScannedDocIds.contains(docId)) {
//////                        Log.d("FirestoreSync", "Duplicate scan skipped: $docId")
//////                        continue
//////                    }
//////
//////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
//////                        try {
//////                            isFromFirestore = true
//////                            val scanned = ScannedPart.fromFirestoreMap(change.document.data)
//////
//////                            // 🔒 Add this time-based check BEFORE calling onUpdate()
//////                            if (System.currentTimeMillis() - scanned.timestamp < 2000) {
//////                                Log.d("FirestoreSync", "Skipped self-sent scan: $docId")
//////                                continue
//////                            }
//////
//////                            seenScannedDocIds.add(docId)
//////                            onUpdate(scanned)
//////                            Log.d("FirestoreSync", "Scan processed: $docId")
//////                        } catch (e: Exception) {
//////                            Log.e("FirestoreParse", "ScannedPart failed", e)
//////                        } finally {
//////                            isFromFirestore = false
//////                        }
//////                    }
//////                }
//////            }
//////    }
////
////    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
////        firestore.collection("scanned_parts")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    val docId = change.document.id
////                    if (seenScannedDocIds.contains(docId)) {
////                        Log.d("FirestoreSync", "Duplicate scan skipped by docId: $docId")
////                        continue
////                    }
////
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val scanned = ScannedPart.fromFirestoreMap(change.document.data)
////
////                            // ⏱️ Optional: avoid echo for very recent self-scans (2 sec)
////                            if (System.currentTimeMillis() - scanned.timestamp < 2000) {
////                                Log.d("FirestoreSync", "Skipped self-sent scan: $docId")
////                                continue
////                            }
////
////                            // ✅ Check if this scanned part already exists in Room
////                            CoroutineScope(Dispatchers.IO).launch {
////                                val dao = PartDatabase.getRoomDatabase(context).scannedPartDao()
////                                val exists = dao.getExistingScanWithTolerance(
////                                    partName = scanned.partName,
////                                    productId = scanned.productId,
////                                    trolleyName = scanned.trolleyName,
////                                    trolleyNumber = scanned.trolleyNumber,
////                                    location = scanned.location,
////                                    model = scanned.model,
////                                    color = scanned.color,
////                                    date = scanned.date,
////                                    shift = scanned.shift,
////                                    timestamp = scanned.timestamp
////                                )
////
////                                if (exists == null) {
////                                    seenScannedDocIds.add(docId)
////
////                                    withContext(Dispatchers.Main) {
////                                        onUpdate(scanned)
////                                        Log.d("FirestoreSync", "Scan processed and updated: $docId")
////                                    }
////                                } else {
////                                    Log.d("FirestoreSync", "Scan already exists in Room, skipped: $docId")
////                                }
////                            }
////
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "ScannedPart parse failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////
////
////    fun listenToUsedPartsUpdates(onUpdate: (UsedPart) -> Unit) {
////        firestore.collection("used_parts")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val usedPart = UsedPart(
////                                partName = change.document.getString("partName") ?: "",
////                                quantity = (change.document.getLong("quantity") ?: 0).toInt(),
////                                timestamp = change.document.getLong("timestamp") ?: System.currentTimeMillis()
////                            )
////                            onUpdate(usedPart)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "UsedPart failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun listenToPlanUpdates(onUpdate: (PlanEntry) -> Unit) {
////        firestore.collection("plans")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val plan = PlanEntry.fromFirestoreMap(change.document.data)
////                            if (plan != null) onUpdate(plan)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "Plan failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun listenToModelProductionUpdates(onUpdate: (ModelProduction) -> Unit) {
////        firestore.collection("model_production")
////            .addSnapshotListener { snapshots, error ->
////                if (error != null || snapshots == null) return@addSnapshotListener
////
////                for (change in snapshots.documentChanges) {
////                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
////                        try {
////                            isFromFirestore = true
////                            val entry = ModelProduction.fromFirestoreMap(change.document.data)
////                            if (entry != null) onUpdate(entry)
////                        } catch (e: Exception) {
////                            Log.e("FirestoreParse", "ModelProduction failed", e)
////                        } finally {
////                            isFromFirestore = false
////                        }
////                    }
////                }
////            }
////    }
////
////    fun pullAllDashboardRows(onResult: (List<DashboardRow>) -> Unit) {
////        firestore.collection("dashboard")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<DashboardRow>()
////                for (doc in documents) {
////                    try {
////                        val row = DashboardRow.fromFirestoreMap(doc.data)
////                        if (row != null) result.add(row)
////                    } catch (e: Exception) {
////                        Log.e("DashboardPull", "Failed to parse DashboardRow", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    fun pullAllPlans(onResult: (List<PlanEntry>) -> Unit) {
////        firestore.collection("plans")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<PlanEntry>()
////                for (doc in documents) {
////                    try {
////                        val plan = PlanEntry.fromFirestoreMap(doc.data)
////                        if (plan != null) result.add(plan)
////                    } catch (e: Exception) {
////                        Log.e("PlanPull", "Failed to parse", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    fun pullAllModelProduction(onResult: (List<ModelProduction>) -> Unit) {
////        firestore.collection("model_production")
////            .get()
////            .addOnSuccessListener { documents ->
////                val result = mutableListOf<ModelProduction>()
////                for (doc in documents) {
////                    try {
////                        val entry = ModelProduction.fromFirestoreMap(doc.data)
////                        if (entry != null) result.add(entry)
////                    } catch (e: Exception) {
////                        Log.e("ModelProdPull", "Failed to parse", e)
////                    }
////                }
////                onResult(result)
////            }
////    }
////
////    private fun sendToGoogleSheet(data: Map<String, Any?>, context: Context, logTag: String) {
////        val taggedData = data.toMutableMap().apply {
////            put("source", if (logTag.contains("Scanned", ignoreCase = true)) "scanned" else "dashboard")
////        }
////
////        val requestBody = JSONObject(taggedData as Map<*, *>).toString()
////
////        val request = object : StringRequest(Method.POST, GOOGLE_SHEET_WEBHOOK_URL,
////            { response -> Log.d(logTag, "Success: $response") },
////            { error -> Log.e(logTag, "Volley Error: ${error.message}", error) }
////        ) {
////            override fun getBody(): ByteArray = requestBody.toByteArray(Charsets.UTF_8)
////            override fun getBodyContentType(): String = "application/json; charset=utf-8"
////            override fun getParams(): MutableMap<String, String> = mutableMapOf()
////        }
////
////        request.retryPolicy = DefaultRetryPolicy(
////            5000,
////            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
////            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
////        )
////        Volley.newRequestQueue(context).add(request)
////    }
////}
//

package com.example.parttracker.firebase

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.parttracker.data.PartDatabase
import com.example.parttracker.data.UsedPart
import com.example.parttracker.model.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

object FirebaseSyncManager {

    private val firestore = FirebaseFirestore.getInstance()
    private val seenScannedDocIds = mutableSetOf<String>()
    private val lastPushedDashboardRowMap = mutableMapOf<String, DashboardRow>()
    @Volatile private var isFromFirestore = false

    private const val GOOGLE_SHEET_WEBHOOK_URL =
        "https://script.google.com/macros/s/AKfycbwk9HphRofyXtPUp5piF25_MGV4vQX-lEWJX_MIPv0UC2h-ntbBKySsTp5ruZvvJ9dV/exec"

    fun pushScannedPart(scannedPart: ScannedPart, context: Context) {
        Log.d("FirestorePush", "pushScannedPart called | isFromFirestore = $isFromFirestore")
        if (isFromFirestore) return
        val docId = "${scannedPart.productId}_${scannedPart.trolleyNumber}_${scannedPart.date}_${scannedPart.shift}"
        val data = scannedPart.toFirestoreMap()

        firestore.collection("scanned_parts")
            .document(docId)
            .set(data)
            .addOnSuccessListener {
                Log.d("FirestorePush", "ScannedPart pushed: $docId")
                sendToGoogleSheet(data, context, "GoogleSheet-Scanned")
            }
            .addOnFailureListener { e ->
                Log.e("FirestorePush", "ScannedPart push failed: $docId", e)
            }
    }




    fun pushDashboardRow(
        row: DashboardRow,
        context: Context,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {

        val documentId = "${row.date}_${row.shift}_${row.model}_${row.color}_${row.partName}".replace("/", "_")

        firestore.collection("dashboard")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                val existing = DashboardRow.fromFirestoreMap(document.data ?: emptyMap())
//                if (existing == row) {
//                    Log.d("FirestoreDebounce", "Firestore data identical, skipping: $documentId")
//                    return@addOnSuccessListener
//                }

                if (existing == row) {
                    Log.d("FirestoreDebounce", "Firestore data identical, but forcing Room update: $documentId")

                    CoroutineScope(Dispatchers.IO).launch {
                        PartDatabase.getRoomDatabase(context).dashboardEntryDao().insert(row)

                        withContext(Dispatchers.Main) {
                            // Optional: refresh UI
                            Log.d("FirestoreDebounce", "Forced Room update for: $documentId")
                        }
                    }

                    return@addOnSuccessListener
                }


                lastPushedDashboardRowMap[documentId] = row

                firestore.collection("dashboard")
                    .document(documentId)
                    .set(row.toFirestoreMap(), SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("FirestorePush", "DashboardRow pushed: $documentId")
                        onSuccess()
                        sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
                    }
                    .addOnFailureListener {
                        Log.e("FirestorePush", "DashboardRow push failed: $documentId", it)
                        onFailure(it)
                    }

            }
            .addOnFailureListener {
                firestore.collection("dashboard")
                    .document(documentId)
                    .set(row.toFirestoreMap(), SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("FirestorePush", "DashboardRow pushed (fallback): $documentId")
                        onSuccess()
                        sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
                    }
                    .addOnFailureListener {
                        Log.e("FirestorePush", "DashboardRow push failed (fallback): $documentId", it)
                        onFailure(it)
                    }
            }
    }

    fun pushUsedPart(usedPart: UsedPart, context: Context) {
        if (isFromFirestore) return
        val docId = "${usedPart.partName}_${usedPart.timestamp}"
        val data = mapOf(
            "partName" to usedPart.partName,
            "quantity" to usedPart.quantity,
            "timestamp" to usedPart.timestamp
        )

        firestore.collection("used_parts")
            .document(docId)
            .set(data)
            .addOnSuccessListener {
                Log.d("FirestorePush", "UsedPart pushed: $docId")
            }
            .addOnFailureListener { e ->
                Log.e("FirestorePush", "UsedPart push failed: $docId", e)
            }
    }

    fun pushPlanEntry(plan: PlanEntry, context: Context) {
        val docId = "${plan.date}_${plan.shift}_${plan.model.replace("/", "_")}_${plan.color.replace("/", "_")}"
        firestore.collection("plans")
            .document(docId)
            .set(plan.toFirestoreMap())
            .addOnSuccessListener {
                Log.d("FirebasePush", "PlanEntry pushed: $docId")
            }
            .addOnFailureListener { e ->
                Log.e("FirebasePush", "Failed to push PlanEntry: $docId", e)
            }
    }


    fun pushModelProduction(entry: ModelProduction, context: Context) {
        val docId = "${entry.date}_${entry.shift}_${entry.model}_${entry.color}".replace(" ", "_")
        firestore.collection("model_production")
            .document(docId)
            .set(entry.toFirestoreMap())
            .addOnSuccessListener {
                Log.d("FirebasePush", "ModelProduction pushed: $docId")
            }
            .addOnFailureListener { e ->
                Log.e("FirebasePush", "Failed to push ModelProduction: $docId", e)
            }
    }

    fun listenToDashboardUpdates(context: Context, onUpdate: (DashboardRow) -> Unit) {
        firestore.collection("dashboard")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener
                for (change in snapshots.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
                        try {
                            isFromFirestore = true
                            val row = DashboardRow.fromFirestoreMap(change.document.data)
                            if (row != null) onUpdate(row)
                        } catch (e: Exception) {
                            Log.e("FirestoreParse", "Error parsing DashboardRow", e)
                        } finally {
                            isFromFirestore = false
                        }
                    }
                }
            }
    }

    fun listenToUsedPartsUpdates(onUpdate: (UsedPart) -> Unit) {
        firestore.collection("used_parts")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener
                for (change in snapshots.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
                        try {
                            isFromFirestore = true
                            val usedPart = UsedPart(
                                partName = change.document.getString("partName") ?: "",
                                quantity = (change.document.getLong("quantity") ?: 0).toInt(),
                                timestamp = change.document.getLong("timestamp") ?: System.currentTimeMillis()
                            )
                            onUpdate(usedPart)
                        } catch (e: Exception) {
                            Log.e("FirestoreParse", "UsedPart failed", e)
                        } finally {
                            isFromFirestore = false
                        }
                    }
                }
            }
    }

    fun listenToPlanUpdates(onUpdate: (PlanEntry) -> Unit) {
        firestore.collection("plans")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener
                for (change in snapshots.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
                        try {
                            isFromFirestore = true
                            val plan = PlanEntry.fromFirestoreMap(change.document.data)
                            if (plan != null) onUpdate(plan)
                        } catch (e: Exception) {
                            Log.e("FirestoreParse", "Plan failed", e)
                        } finally {
                            isFromFirestore = false
                        }
                    }
                }
            }
    }

    fun listenToModelProductionUpdates(onUpdate: (ModelProduction) -> Unit) {
        firestore.collection("model_production")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener
                for (change in snapshots.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
                        try {
                            isFromFirestore = true
                            val entry = ModelProduction.fromFirestoreMap(change.document.data)
                            if (entry != null) onUpdate(entry)
                        } catch (e: Exception) {
                            Log.e("FirestoreParse", "ModelProduction failed", e)
                        } finally {
                            isFromFirestore = false
                        }
                    }
                }
            }
    }

    fun listenToScannedPartsUpdates(context: Context, onUpdate: (ScannedPart) -> Unit) {
        firestore.collection("scanned_parts")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener

                for (change in snapshots.documentChanges) {
                    val docId = change.document.id
                    if (seenScannedDocIds.contains(docId)) {
                        Log.d("FirestoreSync", "Duplicate scan skipped by docId: $docId")
                        continue
                    }

                    if (change.type == DocumentChange.Type.ADDED || change.type == DocumentChange.Type.MODIFIED) {
                        try {
                            isFromFirestore = true
                            val scanned = ScannedPart.fromFirestoreMap(change.document.data)

                            // Optional: skip echo of recently sent data
                            if (System.currentTimeMillis() - scanned.timestamp < 2000) {
                                Log.d("FirestoreSync", "Skipped self-sent scan: $docId")
                                continue
                            }

                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = PartDatabase.getRoomDatabase(context).scannedPartDao()
                                val exists = dao.getExistingScanWithTolerance(
                                    partName = scanned.partName,
                                    productId = scanned.productId,
                                    trolleyName = scanned.trolleyName,
                                    trolleyNumber = scanned.trolleyNumber,
                                    location = scanned.location,
                                    model = scanned.model,
                                    color = scanned.color,
                                    date = scanned.date,
                                    shift = scanned.shift,
                                    timestamp = scanned.timestamp
                                )

                                if (exists == null) {
                                    seenScannedDocIds.add(docId)
                                    withContext(Dispatchers.Main) {
                                        onUpdate(scanned)
                                        Log.d("FirestoreSync", "Scan processed and updated: $docId")
                                    }
                                } else {
                                    Log.d("FirestoreSync", "Scan already exists in Room, skipped: $docId")
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("FirestoreParse", "ScannedPart parse failed", e)
                        } finally {
                            isFromFirestore = false
                        }
                    }
                }
            }
    }


    fun pullAllDashboardRows(onResult: (List<DashboardRow>) -> Unit) {
        firestore.collection("dashboard")
            .get()
            .addOnSuccessListener { documents ->
                val result = mutableListOf<DashboardRow>()
                for (doc in documents) {
                    try {
                        val row = DashboardRow.fromFirestoreMap(doc.data)
                        if (row != null) result.add(row)
                    } catch (e: Exception) {
                        Log.e("DashboardPull", "Failed to parse DashboardRow", e)
                    }
                }
                onResult(result)
            }
    }

    fun pullAllPlans(onResult: (List<PlanEntry>) -> Unit) {
        firestore.collection("plans")
            .get()
            .addOnSuccessListener { documents ->
                val result = mutableListOf<PlanEntry>()
                for (doc in documents) {
                    try {
                        val plan = PlanEntry.fromFirestoreMap(doc.data)
                        if (plan != null) result.add(plan)
                    } catch (e: Exception) {
                        Log.e("PlanPull", "Failed to parse", e)
                    }
                }
                onResult(result)
            }
    }

    fun pullAllModelProduction(onResult: (List<ModelProduction>) -> Unit) {
        firestore.collection("model_production")
            .get()
            .addOnSuccessListener { documents ->
                val result = mutableListOf<ModelProduction>()
                for (doc in documents) {
                    try {
                        val entry = ModelProduction.fromFirestoreMap(doc.data)
                        if (entry != null) result.add(entry)
                    } catch (e: Exception) {
                        Log.e("ModelProdPull", "Failed to parse", e)
                    }
                }
                onResult(result)
            }
    }

    fun pullAllScannedParts(onResult: (List<ScannedPart>) -> Unit) {
        firestore.collection("scanned_parts")
            .get()
            .addOnSuccessListener { documents ->
                val result = mutableListOf<ScannedPart>()
                for (doc in documents) {
                    try {
                        val part = ScannedPart.fromFirestoreMap(doc.data)
                        result.add(part)
                    } catch (e: Exception) {
                        Log.e("ScannedPull", "Failed to parse ScannedPart", e)
                    }
                }
                onResult(result)
            }
            .addOnFailureListener { error ->
                Log.e("ScannedPull", "Firestore error", error)
            }
    }



    fun pullAllUsedParts(onResult: (List<UsedPart>) -> Unit) {
        firestore.collection("used_parts")
            .get()
            .addOnSuccessListener { documents ->
                val result = mutableListOf<UsedPart>()
                for (doc in documents) {
                    try {
                        val used = UsedPart(
                            partName = doc.getString("partName") ?: "",
                            quantity = (doc.getLong("quantity") ?: 0).toInt(),
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                        result.add(used)
                    } catch (e: Exception) {
                        Log.e("UsedPartsPull", "Failed to parse UsedPart", e)
                    }
                }
                onResult(result)
            }
            .addOnFailureListener { error ->
                Log.e("UsedPartsPull", "Firestore error", error)
            }
    }



//    fun pushOBEntry(row: DashboardRow, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
//        val db = FirebaseFirestore.getInstance()
//
//        // Firestore document path (adjust this as per your Firestore structure)
//        val docRef = db.collection("dashboard")
//            .document("${row.date}_${row.shift}_${row.model}_${row.color}_${row.partName}")
//
//        val obData = mapOf(
//            "ob" to row.ob,
//            "obManuallySet" to row.obManuallySet
//        )
//
//        docRef.update(obData)
//            .addOnSuccessListener {
//                Log.d("pushOBEntry", "OB value updated for ${row.partName}")
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                Log.e("pushOBEntry", "Failed to update OB value for ${row.partName}", e)
//                onFailure(e)
//            }
//    }
//
//    fun listenToObUpdates(
//        context: Context,
//        onObUpdated: (DashboardRow) -> Unit
//    ) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("dashboard")
//            .addSnapshotListener { snapshots, error ->
//                if (error != null || snapshots == null) {
//                    Log.e("FirebaseSync", "OB Listener error: ", error)
//                    return@addSnapshotListener
//                }
//
//                for (change in snapshots.documentChanges) {
//                    val data = change.document.data
//                    val row = DashboardRow.fromFirestoreMap(data) ?: continue
//
//                    if (change.type == DocumentChange.Type.MODIFIED) {
//                        // Only proceed if OB was updated
//                        val previousOb = change.document.getMetadata("ob") as? Long // You may not get old value like this
//                        val newOb = row.ob
//
//                        // You can skip this check if unsure about previous value
//                        if (newOb >= 0) {
//                            Log.d("FirebaseSync", "OB updated for ${row.partName}: $newOb")
//                            onObUpdated(row)
//                        }
//                    }
//                }
//            }
//    }
//
//






    fun startListening(context: Context) {
        listenToScannedPartsUpdates(context) { scannedPart ->
            CoroutineScope(Dispatchers.IO).launch {
                PartDatabase.getRoomDatabase(context).scannedPartDao().upsert(scannedPart)
            }
        }

        listenToDashboardUpdates(context) { dashboardRow ->
            CoroutineScope(Dispatchers.IO).launch {
                PartDatabase.getRoomDatabase(context).dashboardEntryDao().insert(dashboardRow)
            }
        }

        listenToUsedPartsUpdates { usedPart ->
            CoroutineScope(Dispatchers.IO).launch {
                PartDatabase.getRoomDatabase(context).usedPartDao().insertUsedPart(usedPart)
            }
        }

        listenToPlanUpdates { planEntry ->
            CoroutineScope(Dispatchers.IO).launch {
                PartDatabase.getRoomDatabase(context).planDao().insert(planEntry)
            }
        }

        listenToModelProductionUpdates { modelProduction ->
            CoroutineScope(Dispatchers.IO).launch {
                PartDatabase.getRoomDatabase(context).modelProductionDao().insertOrUpdate(modelProduction)
            }
        }




    }



    private fun sendToGoogleSheet(data: Map<String, Any?>, context: Context, logTag: String) {
        val taggedData = data.toMutableMap().apply {
            put("source", if (logTag.contains("Scanned", ignoreCase = true)) "scanned" else "dashboard")
        }

        val requestBody = JSONObject(taggedData as Map<*, *>).toString()

        val request = object : StringRequest(Method.POST, GOOGLE_SHEET_WEBHOOK_URL,
            { response -> Log.d(logTag, "Success: $response") },
            { error -> Log.e(logTag, "Volley Error: ${error.message}", error) }
        ) {
            override fun getBody(): ByteArray = requestBody.toByteArray(Charsets.UTF_8)
            override fun getBodyContentType(): String = "application/json; charset=utf-8"
            override fun getParams(): MutableMap<String, String> = mutableMapOf()
        }

        request.retryPolicy = DefaultRetryPolicy(
            5000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(context).add(request)
    }
}


//
//package com.example.parttracker.firebase
//
//import android.content.Context
//import android.util.Log
//import com.android.volley.DefaultRetryPolicy
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.example.parttracker.data.PartDatabase
//import com.example.parttracker.data.UsedPart
//import com.example.parttracker.model.*
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.DocumentChange
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.SetOptions
//import kotlinx.coroutines.*
//import org.json.JSONObject
//
//object FirebaseSyncManager {
//
//    private val firestore = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//
//    var currentUserRole: String? = null
//    private val seenScannedDocIds = mutableSetOf<String>()
//    private val lastPushedDashboardRowMap = mutableMapOf<String, DashboardRow>()
//    @Volatile private var isFromFirestore = false
//
//    private const val GOOGLE_SHEET_WEBHOOK_URL =
//        "https://script.google.com/macros/s/AKfycbwk9HphRofyXtPUp5piF25_MGV4vQX-lEWJX_MIPv0UC2h-ntbBKySsTp5ruZvvJ9dV/exec"
//
//    fun fetchUserRole(userId: String, onComplete: (String?) -> Unit) {
//        firestore.collection("users").document(userId)
//            .get()
//            .addOnSuccessListener { document ->
//                val role = document.getString("role")
//                currentUserRole = role
//                onComplete(role)
//            }
//            .addOnFailureListener {
//                Log.e("RoleFetch", "Failed to fetch role", it)
//                onComplete(null)
//            }
//    }
//
//    fun pushScannedPart(scannedPart: ScannedPart, context: Context) {
//        if (isFromFirestore) return
//
//        // 🔐 Role guard
//        if (currentUserRole != "PaintshopOperator" && currentUserRole != "CTLOperator" && currentUserRole != "Admin") {
//            Log.w("AccessDenied", "User $currentUserRole cannot push scanned parts")
//            return
//        }
//
//        val docId = "${scannedPart.productId}_${scannedPart.trolleyNumber}_${scannedPart.date}_${scannedPart.shift}"
//        val data = scannedPart.toFirestoreMap()
//
//        firestore.collection("scanned_parts")
//            .document(docId)
//            .set(data)
//            .addOnSuccessListener {
//                Log.d("FirestorePush", "ScannedPart pushed: $docId")
//                sendToGoogleSheet(data, context, "GoogleSheet-Scanned")
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirestorePush", "ScannedPart push failed: $docId", e)
//            }
//    }
//
//    fun pushDashboardRow(row: DashboardRow, context: Context, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
//        // 🔐 Role check: all users may be allowed for now; change if needed
//        val documentId = "${row.date}_${row.shift}_${row.model}_${row.color}_${row.partName}".replace("/", "_")
//
//        firestore.collection("dashboard")
//            .document(documentId)
//            .get()
//            .addOnSuccessListener { document ->
//                val existing = DashboardRow.fromFirestoreMap(document.data ?: emptyMap())
//                if (existing == row) {
//                    Log.d("FirestoreDebounce", "Identical data, skipping: $documentId")
//                    return@addOnSuccessListener
//                }
//
//                lastPushedDashboardRowMap[documentId] = row
//
//                firestore.collection("dashboard")
//                    .document(documentId)
//                    .set(row.toFirestoreMap(), SetOptions.merge())
//                    .addOnSuccessListener {
//                        Log.d("FirestorePush", "DashboardRow pushed: $documentId")
//                        onSuccess()
//                        sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
//                    }
//                    .addOnFailureListener {
//                        Log.e("FirestorePush", "DashboardRow push failed: $documentId", it)
//                        onFailure(it)
//                    }
//            }
//            .addOnFailureListener {
//                firestore.collection("dashboard")
//                    .document(documentId)
//                    .set(row.toFirestoreMap(), SetOptions.merge())
//                    .addOnSuccessListener {
//                        Log.d("FirestorePush", "DashboardRow pushed (fallback): $documentId")
//                        onSuccess()
//                        sendToGoogleSheet(row.toFirestoreMap(), context, "GoogleSheet-Dashboard")
//                    }
//                    .addOnFailureListener {
//                        Log.e("FirestorePush", "DashboardRow push failed (fallback): $documentId", it)
//                        onFailure(it)
//                    }
//            }
//    }
//
//    fun pushUsedPart(usedPart: UsedPart, context: Context) {
//        if (isFromFirestore) return
//
//        // 🔐 Optional role guard
//        if (currentUserRole != "Admin") {
//            Log.w("AccessDenied", "User $currentUserRole cannot push used parts")
//            return
//        }
//
//        val docId = "${usedPart.partName}_${usedPart.timestamp}"
//        val data = mapOf(
//            "partName" to usedPart.partName,
//            "quantity" to usedPart.quantity,
//            "timestamp" to usedPart.timestamp
//        )
//
//        firestore.collection("used_parts")
//            .document(docId)
//            .set(data)
//            .addOnSuccessListener {
//                Log.d("FirestorePush", "UsedPart pushed: $docId")
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirestorePush", "UsedPart push failed: $docId", e)
//            }
//    }
//
//    fun pushPlanEntry(plan: PlanEntry, context: Context) {
//        // 🔐 Only PlanManager or Admin can add plans
//        if (currentUserRole != "PlanManager" && currentUserRole != "Admin") {
//            Log.w("AccessDenied", "User $currentUserRole cannot push plan entry")
//            return
//        }
//
//        val docId = "${plan.date}_${plan.shift}_${plan.model.replace("/", "_")}_${plan.color.replace("/", "_")}"
//        firestore.collection("plans")
//            .document(docId)
//            .set(plan.toFirestoreMap())
//            .addOnSuccessListener {
//                Log.d("FirebasePush", "PlanEntry pushed: $docId")
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirebasePush", "Failed to push PlanEntry: $docId", e)
//            }
//    }
//
//    fun pushModelProduction(entry: ModelProduction, context: Context) {
//        // 🔐 Only ProductionTeam or Admin can update MEL
//        if (currentUserRole != "ProductionTeam" && currentUserRole != "Admin") {
//            Log.w("AccessDenied", "User $currentUserRole cannot push model production")
//            return
//        }
//
//        val docId = "${entry.date}_${entry.shift}_${entry.model}_${entry.color}".replace(" ", "_")
//        firestore.collection("model_production")
//            .document(docId)
//            .set(entry.toFirestoreMap())
//            .addOnSuccessListener {
//                Log.d("FirebasePush", "ModelProduction pushed: $docId")
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirebasePush", "Failed to push ModelProduction: $docId", e)
//            }
//    }
//
//    // ... 🔁 All the listener/puller methods can remain unchanged (no role required for read-only ops)
//
//    private fun sendToGoogleSheet(data: Map<String, Any?>, context: Context, logTag: String) {
//        val taggedData = data.toMutableMap().apply {
//            put("source", if (logTag.contains("Scanned", ignoreCase = true)) "scanned" else "dashboard")
//        }
//
//        val requestBody = JSONObject(taggedData as Map<*, *>).toString()
//
//        val request = object : StringRequest(Method.POST, GOOGLE_SHEET_WEBHOOK_URL,
//            { response -> Log.d(logTag, "Success: $response") },
//            { error -> Log.e(logTag, "Volley Error: ${error.message}", error) }
//        ) {
//            override fun getBody(): ByteArray = requestBody.toByteArray(Charsets.UTF_8)
//            override fun getBodyContentType(): String = "application/json; charset=utf-8"
//            override fun getParams(): MutableMap<String, String> = mutableMapOf()
//        }
//
//        request.retryPolicy = DefaultRetryPolicy(
//            5000,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        )
//        Volley.newRequestQueue(context).add(request)
//    }
//}

