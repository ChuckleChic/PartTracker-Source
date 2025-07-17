//////////package com.example.parttracker.viewmodel
//////////
//////////import android.content.Context
//////////import android.os.Build
//////////import android.util.Log
//////////import androidx.annotation.RequiresApi
//////////import androidx.lifecycle.*
//////////import com.example.parttracker.model.*
//////////import com.example.parttracker.repository.PartRepository
//////////import kotlinx.coroutines.Dispatchers
//////////import kotlinx.coroutines.launch
//////////import kotlinx.coroutines.withContext
//////////import java.time.LocalDate
//////////import java.time.LocalTime
//////////import com.example.parttracker.constants.modelToParts
//////////
//////////
//////////@RequiresApi(Build.VERSION_CODES.O)
//////////class DashboardViewModel(private val repository: PartRepository) : ViewModel() {
//////////
//////////    private val _dashboardRows = MutableLiveData<List<DashboardRow>>()
//////////    val dashboardRows: LiveData<List<DashboardRow>> = _dashboardRows
//////////
//////////    private val _sequenceGroups = MutableLiveData<List<SequenceGroup>>()
//////////    val sequenceGroups: LiveData<List<SequenceGroup>> = _sequenceGroups
//////////
//////////    private fun getCurrentShift(): Pair<String, String> {
//////////        val now = LocalTime.now()
//////////        val today = LocalDate.now()
//////////        return when {
//////////            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
//////////            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
//////////            else -> today.minusDays(1).toString() to "B"
//////////        }
//////////    }
//////////
//////////    fun loadDashboardData(context: Context) {
//////////        viewModelScope.launch(Dispatchers.IO) {
//////////            val (date, shift) = getCurrentShift()
//////////            val plans = repository.planDao.getTodayPlans(date, shift)
//////////
//////////            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()
//////////
//////////            for (plan in plans) {
//////////                val parts = modelToParts[plan.model] ?: emptyList()
//////////                for (part in parts) {
//////////                    val key = Triple(plan.model, plan.color, part)
//////////                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
//////////                }
//////////            }
//////////
//////////            val dashboardList = mutableListOf<DashboardRow>()
//////////
//////////            for ((key, plannedQty) in partPlanMap) {
//////////                val (model, color, partName) = key
//////////                val prodEntry = repository.getModelProduction(model, color, date, shift)
//////////
//////////
//////////                val lastRow = repository.getLastDashboardRowForPart(model, color, partName, date, shift)
//////////                val ob = lastRow?.cb ?: 0
//////////
//////////                // Track which model+color already used produced/rejection
//////////                val producedUsedMap = mutableMapOf<Pair<String, String>, Boolean>()
//////////                ...
//////////                val produced = if (producedUsedMap.getOrDefault(model to color, false)) 0 else prodEntry?.produced ?: 0
//////////                val rejection = if (producedUsedMap.getOrDefault(model to color, false)) 0 else prodEntry?.rejection ?: 0
//////////                producedUsedMap[model to color] = true
//////////
//////////
//////////                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0
//////////                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)
//////////
//////////                val dispatch = scannedParts
//////////                    .filter { it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence }
//////////                    .sumOf { it.quantity }
//////////                val received = scannedParts
//////////                    .filter { it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence }
//////////                    .sumOf { it.quantity }
//////////
//////////                val remainingPs = plannedQty - dispatch
//////////                val remainingVa = plannedQty - received
//////////                val cb = ob + received - produced - rejection
//////////
//////////                val row = DashboardRow(
//////////                    date = date,
//////////                    shift = shift,
//////////                    model = model,
//////////                    color = color,
//////////                    partName = partName,
//////////                    planned = plannedQty,
//////////                    ob = ob,
//////////                    dispatch = dispatch,
//////////                    received = received,
//////////                    remainingPs = remainingPs,
//////////                    remainingVa = remainingVa,
//////////                    produced = produced,
//////////                    rejection = rejection,
//////////                    cb = cb
//////////                )
//////////
//////////                dashboardList.add(row)
//////////
//////////                // ✅ Save to Room and sync to Firebase
//////////                repository.insertOrUpdateDashboard(row, context)
//////////            }
//////////
//////////            val sortedRows = dashboardList.sortedBy { it.partName }
//////////            _dashboardRows.postValue(sortedRows)
//////////
//////////            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
//////////
//////////            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
//////////                val (_, rows) = entry
//////////                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
//////////                    ColorGroupedDashboard(color = color, rows = colorRows)
//////////                }
//////////                val totalPlanned = rows.sumOf { it.planned }
//////////                val totalReceived = rows.sumOf { it.received }
//////////
//////////                SequenceGroup(
//////////                    sequence = index + 1,
//////////                    totalPlanned = totalPlanned,
//////////                    totalCompleted = totalReceived,
//////////                    colorGroups = colorGroups
//////////                )
//////////            }
//////////
//////////            _sequenceGroups.postValue(sequenceList)
//////////        }
//////////    }
//////////
//////////    fun updateDashboardRow(updated: DashboardRow, context: Context) {
//////////        viewModelScope.launch(Dispatchers.IO) {
//////////            try {
//////////                // Update Room + Firebase
//////////                repository.insertOrUpdateDashboard(updated, context)
//////////
//////////                // Update locally in sequenceGroups LiveData
//////////                val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
//////////                    val updatedColorGroups = group.colorGroups.map { colorGroup ->
//////////                        val updatedRows = colorGroup.rows.map { row ->
//////////                            if (row.model == updated.model &&
//////////                                row.color == updated.color &&
//////////                                row.partName == updated.partName
//////////                            ) {
//////////                                updated
//////////                            } else row
//////////                        }
//////////                        colorGroup.copy(rows = updatedRows)
//////////                    }
//////////                    group.copy(colorGroups = updatedColorGroups)
//////////                }
//////////
//////////                withContext(Dispatchers.Main) {
//////////                    _sequenceGroups.value = updatedGroups
//////////                }
//////////
//////////                Log.d("DashboardViewModel", "OB updated locally and remotely for ${updated.partName}")
//////////
//////////            } catch (e: Exception) {
//////////                Log.e("DashboardViewModel", "Error updating DashboardRow: ${e.message}", e)
//////////            }
//////////        }
//////////    }
//////////
//////////    fun matchUnmatchedScannedPartsThenLoadDashboard(context: Context) {
//////////        viewModelScope.launch(Dispatchers.IO) {
//////////            repository.matchScannedPartsToPlans()
//////////            loadDashboardData(context)
//////////        }
//////////    }
//////////
//////////    fun saveModelProduction(
//////////        model: String,
//////////        color: String,
//////////        openingBalance: Int,
//////////        produced: Int,
//////////        rejection: Int,
//////////        context: Context
//////////    ) {
//////////        viewModelScope.launch(Dispatchers.IO) {
//////////            val (date, shift) = getCurrentShift()
//////////            val entry = ModelProduction(
//////////                model = model,
//////////                color = color,
//////////                date = date,
//////////                shift = shift,
//////////                openingBalance = openingBalance,
//////////                produced = produced,
//////////                rejection = rejection
//////////            )
//////////            repository.insertModelProduction(entry)
//////////            loadDashboardData(context)
//////////        }
//////////    }
//////////}
//////////
////////
////////
////////package com.example.parttracker.viewmodel
////////
////////import android.content.Context
////////import android.os.Build
////////import android.util.Log
////////import androidx.annotation.RequiresApi
////////import androidx.lifecycle.*
////////import com.example.parttracker.model.*
////////import com.example.parttracker.repository.PartRepository
////////import kotlinx.coroutines.Dispatchers
////////import kotlinx.coroutines.launch
////////import kotlinx.coroutines.withContext
////////import java.time.LocalDate
////////import java.time.LocalTime
////////import com.example.parttracker.constants.modelToParts
////////import com.example.parttracker.model.CBPartEntry
////////import com.example.parttracker.model.ModelCB
////////import com.example.parttracker.model.ModelGroup
////////
////////
////////@RequiresApi(Build.VERSION_CODES.O)
////////class DashboardViewModel(private val repository: PartRepository) : ViewModel() {
////////
////////    private val _dashboardRows = MutableLiveData<List<DashboardRow>>()
////////    val dashboardRows: LiveData<List<DashboardRow>> = _dashboardRows
////////
////////    private val _sequenceGroups = MutableLiveData<List<SequenceGroup>>()
////////    val sequenceGroups: LiveData<List<SequenceGroup>> = _sequenceGroups
////////
////////    private val _inventoryCBList = MutableLiveData<List<ModelGroup>>()
////////    val inventoryCBList: LiveData<List<ModelGroup>> = _inventoryCBList
////////
////////    private fun buildInventoryStructure(rows: List<DashboardRow>): List<ModelGroup> {
////////        return rows.groupBy { it.model }.map { (model, modelRows) ->
////////            val colors = modelRows.groupBy { it.color }.map { (color, partRows) ->
////////                val parts = partRows.map {
////////                    CBPartEntry(partName = it.partName, cb = it.cb)
////////                }
////////                ModelColorCB(color = color, partsCB = parts)
////////            }
////////            ModelGroup(model = model, colors = colors)
////////        }
////////    }
////////
////////
////////
////////    private fun getCurrentShift(): Pair<String, String> {
////////        val now = LocalTime.now()
////////        val today = LocalDate.now()
////////        return when {
////////            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
////////            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
////////            else -> today.minusDays(1).toString() to "B"
////////        }
////////    }
////////
////////    fun loadDashboardData(context: Context) {
////////        viewModelScope.launch(Dispatchers.IO) {
////////            val (date, shift) = getCurrentShift()
////////            val plans = repository.planDao.getTodayPlans(date, shift)
////////
////////            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()
////////
////////            for (plan in plans) {
////////                val parts = modelToParts[plan.model] ?: emptyList()
////////                for (part in parts) {
////////                    val key = Triple(plan.model, plan.color, part)
////////                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
////////                }
////////            }
////////
////////            val dashboardList = mutableListOf<DashboardRow>()
////////
////////            for ((key, plannedQty) in partPlanMap) {
////////                val (model, color, partName) = key
////////                val prodEntry = repository.getModelProduction(model, color, date, shift)
////////
//////////                val lastRow = repository.getLastDashboardRowForPart(model, color, partName, date, shift)
//////////                val ob = prodEntry?.openingBalance?.takeIf { it != 0 } ?: lastRow?.cb ?: 0
////////
////////                val existingTodayRow = repository.getDashboardRow(date, shift, model, color, partName)
////////                val ob = existingTodayRow?.ob
////////                    ?: run {
////////                        val yesterday = LocalDate.parse(date).minusDays(1).toString()
////////                        val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
////////                        lastRow?.cb ?: 0
////////                    }
////////
////////
////////                val produced = prodEntry?.produced ?: 0
////////                val rejection = prodEntry?.rejection ?: 0
////////
////////
////////                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0
////////                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)
////////
////////                val dispatch = scannedParts
////////                    .filter { it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence }
////////                    .sumOf { it.quantity }
////////
////////                val received = scannedParts
////////                    .filter { it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence }
////////                    .sumOf { it.quantity }
////////
////////                val remainingPs = plannedQty - dispatch
////////                val remainingVa = plannedQty - received
////////                val cb = ob + received - produced - rejection
////////
////////                val row = DashboardRow(
////////                    date = date,
////////                    shift = shift,
////////                    model = model,
////////                    color = color,
////////                    partName = partName,
////////                    planned = plannedQty,
////////                    ob = ob,
////////                    dispatch = dispatch,
////////                    received = received,
////////                    remainingPs = remainingPs,
////////                    remainingVa = remainingVa,
////////                    produced = produced,
////////                    rejection = rejection,
////////                    cb = cb
////////                )
////////
////////                dashboardList.add(row)
////////
////////                repository.insertOrUpdateDashboard(row, context)
////////            }
////////
////////            val sortedRows = dashboardList.sortedBy { it.partName }
////////            _dashboardRows.postValue(sortedRows)
////////
////////            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
////////
////////            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
////////                val (_, rows) = entry
////////                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
////////                    ColorGroupedDashboard(color = color, rows = colorRows)
////////                }
////////                val totalPlanned = rows.sumOf { it.planned }
////////                val totalReceived = rows.sumOf { it.received }
////////
////////                SequenceGroup(
////////                    sequence = index + 1,
////////                    totalPlanned = totalPlanned,
////////                    totalCompleted = totalReceived,
////////                    colorGroups = colorGroups
////////                )
////////            }
////////
////////            _sequenceGroups.postValue(sequenceList)
////////        }
////////    }
////////
////////    fun updateDashboardRow(updated: DashboardRow, context: Context) {
////////        viewModelScope.launch(Dispatchers.IO) {
////////            try {
////////                repository.insertOrUpdateDashboard(updated, context)
////////
////////                val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
////////                    val updatedColorGroups = group.colorGroups.map { colorGroup ->
////////                        val updatedRows = colorGroup.rows.map { row ->
////////                            if (row.model == updated.model &&
////////                                row.color == updated.color &&
////////                                row.partName == updated.partName
////////                            ) {
////////                                updated
////////                            } else row
////////                        }
////////                        colorGroup.copy(rows = updatedRows)
////////                    }
////////                    group.copy(colorGroups = updatedColorGroups)
////////                }
////////
////////                withContext(Dispatchers.Main) {
////////                    _sequenceGroups.value = updatedGroups
////////                }
////////
////////                Log.d("DashboardViewModel", "OB updated locally and remotely for ${updated.partName}")
////////
////////            } catch (e: Exception) {
////////                Log.e("DashboardViewModel", "Error updating DashboardRow: ${e.message}", e)
////////            }
////////        }
////////    }
////////
////////    fun matchUnmatchedScannedPartsThenLoadDashboard(context: Context) {
////////        viewModelScope.launch(Dispatchers.IO) {
////////            repository.matchScannedPartsToPlans()
////////            loadDashboardData(context)
////////        }
////////    }
////////
////////
////////
////////    fun saveModelProduction(
////////        model: String,
////////        color: String,
////////        produced: Int,
////////        rejection: Int,
////////        context: Context
////////    ) {
////////        viewModelScope.launch(Dispatchers.IO) {
////////            val (date, shift) = getCurrentShift()
////////
////////            val existingEntry = repository.getModelProduction(model, color, date, shift)
////////
////////            val preservedOB = if (existingEntry != null) {
////////                existingEntry.openingBalance
////////            } else {
////////                // ⚠️ Get OB from latest dashboard row instead of defaulting to 0
////////                val parts = modelToParts[model] ?: emptyList()
////////                if (parts.isNotEmpty()) {
////////                    val lastRow = repository.getLastDashboardRowForPart(model, color, parts[0], date, shift)
////////                    lastRow?.cb ?: 0
////////                } else 0
////////            }
////////
////////            val updatedEntry = ModelProduction(
////////                model = model,
////////                color = color,
////////                date = date,
////////                shift = shift,
////////                openingBalance = preservedOB,
////////                produced = produced,
////////                rejection = rejection
////////            )
////////
////////            repository.insertModelProduction(updatedEntry)
////////            loadDashboardData(context)
////////        }
////////    }
////////
//////////
////////}
//////
//////
//////package com.example.parttracker.viewmodel
//////
//////import android.content.Context
//////import android.os.Build
//////import android.util.Log
//////import androidx.annotation.RequiresApi
//////import androidx.lifecycle.*
//////import com.example.parttracker.constants.modelToParts
//////import com.example.parttracker.model.*
//////import com.example.parttracker.repository.PartRepository
//////import kotlinx.coroutines.Dispatchers
//////import kotlinx.coroutines.launch
//////import kotlinx.coroutines.withContext
//////import java.time.LocalDate
//////import java.time.LocalTime
//////import com.example.parttracker.firebase.FirebaseSyncManager
//////
//////
//////@RequiresApi(Build.VERSION_CODES.O)
//////class DashboardViewModel(private val repository: PartRepository) : ViewModel() {
//////
//////    private val _dashboardRows = MutableLiveData<List<DashboardRow>>()
//////    val dashboardRows: LiveData<List<DashboardRow>> = _dashboardRows
//////
//////    private val _sequenceGroups = MutableLiveData<List<SequenceGroup>>()
//////    val sequenceGroups: LiveData<List<SequenceGroup>> = _sequenceGroups
//////
//////    private val _inventoryCBList = MutableLiveData<List<ModelGroup>>()
//////    val inventoryCBList: LiveData<List<ModelGroup>> = _inventoryCBList
//////
//////    private fun buildInventoryStructure(rows: List<DashboardRow>): List<ModelGroup> {
//////        return rows.groupBy { it.model }.map { (model, modelRows) ->
//////            val colors = modelRows.groupBy { it.color }.map { (color, partRows) ->
//////                val parts = partRows.map {
//////                    CBPartEntry(partName = it.partName, cb = it.cb)
//////                }
//////                ColorGroup(color = color, partsCB = parts)
//////            }
//////            ModelGroup(model = model, colorGroups = colors)
//////        }
//////    }
//////
//////    private fun getCurrentShift(): Pair<String, String> {
//////        val now = LocalTime.now()
//////        val today = LocalDate.now()
//////        return when {
//////            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
//////            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
//////            else -> today.minusDays(1).toString() to "B"
//////        }
//////    }
//////
//////    fun loadDashboardData(context: Context) {
//////        viewModelScope.launch(Dispatchers.IO) {
//////            val (date, shift) = getCurrentShift()
//////            val plans = repository.planDao.getTodayPlans(date, shift)
//////
//////            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()
//////
//////            for (plan in plans) {
//////                val parts = modelToParts[plan.model] ?: emptyList()
//////                for (part in parts) {
//////                    val key = Triple(plan.model, plan.color, part)
//////                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
//////                }
//////            }
//////
//////
//////            val dashboardList = mutableListOf<DashboardRow>()
//////
//////            for ((key, plannedQty) in partPlanMap) {
//////                val (model, color, partName) = key
//////
//////                // ✅ Corrected filter logic to skip Shield LH/RH for Bro. Black of specific models
//////                val isShield = partName == "Shield LH/RH"
//////                val isBroBlackOfSpecificModels = model in listOf("GA-3501", "DA-3503", "LA-3502") &&
//////                        color.equals("Bro. Black", ignoreCase = true)
//////
//////                if (isShield && isBroBlackOfSpecificModels) {
//////                    Log.d("DashboardViewModel", "Skipping $partName for $model - $color")
//////                    continue
//////                }
//////
//////                val prodEntry = repository.getModelProduction(model, color, date, shift)
//////
//////                val existingTodayRow = repository.getDashboardRow(date, shift, model, color, partName)
//////                val ob = existingTodayRow?.ob
//////                    ?: run {
//////                        val yesterday = LocalDate.parse(date).minusDays(1).toString()
//////                        val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
//////                        lastRow?.cb ?: 0
//////                    }
//////
//////                val produced = prodEntry?.produced ?: 0
//////                val rejection = prodEntry?.rejection ?: 0
//////
//////                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0
//////                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)
//////
//////                val dispatch = scannedParts
//////                    .filter { it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence }
//////                    .sumOf { it.quantity }
//////
//////                val received = scannedParts
//////                    .filter { it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence }
//////                    .sumOf { it.quantity }
//////
//////                val remainingPs = plannedQty - dispatch - ob
//////                val remainingVa = plannedQty - received - ob
//////                val cb = ob + received - produced - rejection
//////
//////                val row = DashboardRow(
//////                    date = date,
//////                    shift = shift,
//////                    model = model,
//////                    color = color,
//////                    partName = partName,
//////                    planned = plannedQty,
//////                    ob = ob,
//////                    dispatch = dispatch,
//////                    received = received,
//////                    remainingPs = remainingPs,
//////                    remainingVa = remainingVa,
//////                    produced = produced,
//////                    rejection = rejection,
//////                    cb = cb
//////                )
//////
//////                dashboardList.add(row)
//////
//////                repository.insertOrUpdateDashboard(row, context)
//////            }
//////
//////            val sortedRows = dashboardList.sortedBy { it.partName }
//////            _dashboardRows.postValue(sortedRows)
//////
//////            // Set expanded CB layout
//////            val inventoryGroups = buildInventoryStructure(sortedRows)
//////            _inventoryCBList.postValue(inventoryGroups)
//////
//////            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
//////
//////            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
//////                val (_, rows) = entry
//////                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
//////                    ColorGroupedDashboard(color = color, rows = colorRows)
//////                }
//////                val totalPlanned = rows.sumOf { it.planned }
//////                val totalReceived = rows.sumOf { it.received }
//////
//////                SequenceGroup(
//////                    sequence = index + 1,
//////                    totalPlanned = totalPlanned,
//////                    totalCompleted = totalReceived,
//////                    colorGroups = colorGroups
//////                )
//////            }
//////
//////            _sequenceGroups.postValue(sequenceList)
//////        }
//////    }
//////
//////    fun updateDashboardRow(updated: DashboardRow, context: Context) {
//////        viewModelScope.launch(Dispatchers.IO) {
//////            try {
//////                repository.insertOrUpdateDashboard(updated, context)
//////
//////                val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
//////                    val updatedColorGroups = group.colorGroups.map { colorGroup ->
//////                        val updatedRows = colorGroup.rows.map { row ->
//////                            if (row.model == updated.model &&
//////                                row.color == updated.color &&
//////                                row.partName == updated.partName
//////                            ) {
//////                                updated
//////                            } else row
//////                        }
//////                        colorGroup.copy(rows = updatedRows)
//////                    }
//////                    group.copy(colorGroups = updatedColorGroups)
//////                }
//////
//////                withContext(Dispatchers.Main) {
//////                    _sequenceGroups.value = updatedGroups
//////                }
//////
//////                Log.d("DashboardViewModel", "OB updated locally and remotely for ${updated.partName}")
//////
//////            } catch (e: Exception) {
//////                Log.e("DashboardViewModel", "Error updating DashboardRow: ${e.message}", e)
//////            }
//////        }
//////    }
//////
//////    fun matchUnmatchedScannedPartsThenLoadDashboard(context: Context) {
//////        viewModelScope.launch(Dispatchers.IO) {
//////            repository.matchScannedPartsToPlans()
//////            loadDashboardData(context)
//////        }
//////    }
//////
//////    fun startListeningForDashboardChanges(context: Context) {
//////        FirebaseSyncManager.listenToDashboardUpdates(context) { updatedRow ->
//////            viewModelScope.launch {
//////                repository.insertOrUpdateDashboard(updatedRow, context)
//////                loadDashboardData(context)
//////            }
//////        }
//////    }
//////
//////
//////    fun startListeningForScannedParts(context: Context) {
//////        FirebaseSyncManager.listenToScannedPartsUpdates(context) { scannedPart ->
//////            viewModelScope.launch {
//////                repository.insert(scannedPart, context)
//////                loadDashboardData(context)
//////            }
//////        }
//////    }
//////
//////
//////    fun startListeningForUsedParts(context: Context) {
//////        FirebaseSyncManager.listenToUsedPartsUpdates { usedPart ->
//////            viewModelScope.launch {
//////                repository.insertUsedPart(usedPart.partName, usedPart.quantity, context)
//////                loadDashboardData(context)
//////            }
//////        }
//////    }
//////
//////
//////    fun startListeningForPlans(context: Context) {
//////        FirebaseSyncManager.listenToPlanUpdates { plan ->
//////            viewModelScope.launch {
//////                repository.insertPlan(plan, context)
//////                loadDashboardData(context)
//////            }
//////        }
//////    }
//////
//////
//////    fun startListeningForModelProduction(context: Context) {
//////        FirebaseSyncManager.listenToModelProductionUpdates { entry ->
//////            viewModelScope.launch {
//////                repository.insertModelProduction(entry, context)
//////                loadDashboardData(context)
//////            }
//////        }
//////    }
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////    fun saveModelProduction(
//////        model: String,
//////        color: String,
//////        produced: Int,
//////        rejection: Int,
//////        context: Context
//////    ) {
//////        viewModelScope.launch(Dispatchers.IO) {
//////            val (date, shift) = getCurrentShift()
//////
//////            val existingEntry = repository.getModelProduction(model, color, date, shift)
//////
//////            val preservedOB = if (existingEntry != null) {
//////                existingEntry.openingBalance
//////            } else {
//////                val parts = modelToParts[model] ?: emptyList()
//////                if (parts.isNotEmpty()) {
//////                    val lastRow = repository.getLastDashboardRowForPart(model, color, parts[0], date, shift)
//////                    lastRow?.cb ?: 0
//////                } else 0
//////            }
//////
//////            val updatedEntry = ModelProduction(
//////                model = model,
//////                color = color,
//////                date = date,
//////                shift = shift,
//////                openingBalance = preservedOB,
//////                produced = produced,
//////                rejection = rejection
//////            )
//////
//////            repository.insertModelProduction(updatedEntry)
//////            loadDashboardData(context)
//////        }
//////    }
//////}
//////
////
////
////package com.example.parttracker.viewmodel
////
////import android.content.Context
////import android.os.Build
////import android.util.Log
////import androidx.annotation.RequiresApi
////import androidx.lifecycle.*
////import com.example.parttracker.constants.modelToParts
////import com.example.parttracker.model.*
////import com.example.parttracker.repository.PartRepository
////import kotlinx.coroutines.Dispatchers
////import kotlinx.coroutines.launch
////import kotlinx.coroutines.withContext
////import java.time.LocalDate
////import java.time.LocalTime
////import com.example.parttracker.firebase.FirebaseSyncManager
////import kotlinx.coroutines.delay
////
////
////@RequiresApi(Build.VERSION_CODES.O)
////class DashboardViewModel(private val repository: PartRepository) : ViewModel() {
////
////    private val _dashboardRows = MutableLiveData<List<DashboardRow>>()
////    val dashboardRows: LiveData<List<DashboardRow>> = _dashboardRows
////
////    private val _sequenceGroups = MutableLiveData<List<SequenceGroup>>()
////    val sequenceGroups: LiveData<List<SequenceGroup>> = _sequenceGroups
////
////    private val _inventoryCBList = MutableLiveData<List<ModelGroup>>()
////    val inventoryCBList: LiveData<List<ModelGroup>> = _inventoryCBList
////
////    private fun buildInventoryStructure(rows: List<DashboardRow>): List<ModelGroup> {
////        return rows.groupBy { it.model }.map { (model, modelRows) ->
////            val colors = modelRows.groupBy { it.color }.map { (color, partRows) ->
////                val parts = partRows.map {
////                    CBPartEntry(partName = it.partName, cb = it.cb)
////                }
////                ColorGroup(color = color, partsCB = parts)
////            }
////            ModelGroup(model = model, colorGroups = colors)
////        }
////    }
////
////    private fun getCurrentShift(): Pair<String, String> {
////        val now = LocalTime.now()
////        val today = LocalDate.now()
////        return when {
////            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
////            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
////            else -> today.minusDays(1).toString() to "B"
////        }
////    }
////
////    fun loadDashboardData(context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            val (date, shift) = getCurrentShift()
////            val plans = repository.planDao.getTodayPlans(date, shift)
////
////            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()
////            for (plan in plans) {
////                val parts = modelToParts[plan.model] ?: emptyList()
////                for (part in parts) {
////                    val key = Triple(plan.model, plan.color, part)
////                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
////                }
////            }
////
////            val dashboardList = mutableListOf<DashboardRow>()
////            for ((key, plannedQty) in partPlanMap) {
////                val (model, color, partName) = key
////                val isShield = partName == "Shield LH/RH"
////                val isBroBlackOfSpecificModels = model in listOf("GA-3501", "DA-3503", "LA-3502") &&
////                        color.equals("Bro. Black", ignoreCase = true)
////
////                if (isShield && isBroBlackOfSpecificModels) {
////                    Log.d("DashboardViewModel", "Skipping $partName for $model - $color")
////                    continue
////                }
////
////                val prodEntry = repository.getModelProduction(model, color, date, shift)
////                val existingTodayRow = repository.getDashboardRow(date, shift, model, color, partName)
////                val ob = existingTodayRow?.ob ?: run {
////                    val yesterday = LocalDate.parse(date).minusDays(1).toString()
////                    val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
////                    lastRow?.cb ?: 0
////                }
////
////                val produced = prodEntry?.produced ?: 0
////                val rejection = prodEntry?.rejection ?: 0
////                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0
////
////                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)
////                val dispatch = scannedParts
////                    .filter { it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence }
////                    .sumOf { it.quantity }
////
////                val received = scannedParts
////                    .filter { it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence }
////                    .sumOf { it.quantity }
////
////                val remainingPs = plannedQty - dispatch - ob
////                val remainingVa = plannedQty - received - ob
////                val cb = ob + received - produced - rejection
////
////                val row = DashboardRow(
////                    date = date,
////                    shift = shift,
////                    model = model,
////                    color = color,
////                    partName = partName,
////                    planned = plannedQty,
////                    ob = ob,
////                    dispatch = dispatch,
////                    received = received,
////                    remainingPs = remainingPs,
////                    remainingVa = remainingVa,
////                    produced = produced,
////                    rejection = rejection,
////                    cb = cb
////                )
////
////                dashboardList.add(row)
////                repository.insertOrUpdateDashboard(row, context)
////            }
////
////            val sortedRows = dashboardList.sortedBy { it.partName }
////            _dashboardRows.postValue(sortedRows)
////            _inventoryCBList.postValue(buildInventoryStructure(sortedRows))
////
////            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
////            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
////                val (_, rows) = entry
////                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
////                    ColorGroupedDashboard(color = color, rows = colorRows)
////                }
////                val totalPlanned = rows.sumOf { it.planned }
////                val totalReceived = rows.sumOf { it.received }
////
////                SequenceGroup(
////                    sequence = index + 1,
////                    totalPlanned = totalPlanned,
////                    totalCompleted = totalReceived,
////                    colorGroups = colorGroups
////                )
////            }
////            _sequenceGroups.postValue(sequenceList)
////        }
////    }
////
////    fun updateDashboardRow(updated: DashboardRow, context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            try {
////                repository.insertOrUpdateDashboard(updated, context)
////                val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
////                    val updatedColorGroups = group.colorGroups.map { colorGroup ->
////                        val updatedRows = colorGroup.rows.map { row ->
////                            if (row.model == updated.model &&
////                                row.color == updated.color &&
////                                row.partName == updated.partName
////                            ) updated else row
////                        }
////                        colorGroup.copy(rows = updatedRows)
////                    }
////                    group.copy(colorGroups = updatedColorGroups)
////                }
////
////                withContext(Dispatchers.Main) {
////                    _sequenceGroups.value = updatedGroups
////                }
////
////                Log.d("DashboardViewModel", "OB updated locally and remotely for ${updated.partName}")
////
////            } catch (e: Exception) {
////                Log.e("DashboardViewModel", "Error updating DashboardRow: ${e.message}", e)
////            }
////        }
////    }
////
////    fun syncFirestoreDataIfEmpty(context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            val localData = repository.getAllDashboardRows().value ?: emptyList()
////            if (localData.isEmpty()) {
////                loadDashboardData(context)
////            }
////        }
////    }
////
////
////
////
////    fun matchUnmatchedScannedPartsThenLoadDashboard(context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            repository.matchScannedPartsToPlans()
////            loadDashboardData(context)
////        }
////    }
////
////    fun startListeningForDashboardChanges(context: Context) {
////        FirebaseSyncManager.listenToDashboardUpdates(context) { updatedRow ->
////            viewModelScope.launch {
////                repository.insertOrUpdateDashboard(updatedRow, context)
////                loadDashboardData(context)
////            }
////        }
////    }
////
////
////
////    fun startListeningForScannedParts(context: Context) {
////        FirebaseSyncManager.listenToScannedPartsUpdates(context) { scannedPart ->
////            viewModelScope.launch {
////                repository.insert(scannedPart, context)
////                loadDashboardData(context)
////            }
////        }
////    }
////
////    fun startListeningForUsedParts(context: Context) {
////        FirebaseSyncManager.listenToUsedPartsUpdates { usedPart ->
////            viewModelScope.launch {
////                repository.insertUsedPart(usedPart.partName, usedPart.quantity, context)
////                loadDashboardData(context)
////            }
////        }
////    }
////
//////    fun startListeningForPlans(context: Context) {
//////        FirebaseSyncManager.listenToPlanUpdates { plan ->
//////            viewModelScope.launch {
//////                repository.insertPlan(plan, context)
//////                loadDashboardData(context)
//////            }
//////        }
//////    }
////
////    fun startListeningForPlans(context: Context) {
////        FirebaseSyncManager.listenToPlanUpdates { plan ->
////            viewModelScope.launch {
////                repository.insertPlan(plan, context)
////                delay(300) // wait 300ms
////                loadDashboardData(context)
////            }
////        }
////    }
////
////
////    fun startListeningForModelProduction(context: Context) {
////        FirebaseSyncManager.listenToModelProductionUpdates { entry ->
////            viewModelScope.launch {
////                repository.insertModelProduction(entry, context)
////                loadDashboardData(context)
////            }
////        }
////    }
////
////    fun saveModelProduction(
////        model: String,
////        color: String,
////        produced: Int,
////        rejection: Int,
////        context: Context
////    ) {
////        viewModelScope.launch(Dispatchers.IO) {
////            val (date, shift) = getCurrentShift()
////            val existingEntry = repository.getModelProduction(model, color, date, shift)
////
////            val preservedOB = if (existingEntry != null) {
////                existingEntry.openingBalance
////            } else {
////                val parts = modelToParts[model] ?: emptyList()
////                if (parts.isNotEmpty()) {
////                    val lastRow = repository.getLastDashboardRowForPart(model, color, parts[0], date, shift)
////                    lastRow?.cb ?: 0
////                } else 0
////            }
////
////            val updatedEntry = ModelProduction(
////                model = model,
////                color = color,
////                date = date,
////                shift = shift,
////                openingBalance = preservedOB,
////                produced = produced,
////                rejection = rejection
////            )
////
////            // ✅ THIS LINE IS THE FIX:
////            repository.insertModelProduction(updatedEntry, context)
////
////            loadDashboardData(context)
////        }
////    }
////}
//
////
////package com.example.parttracker.viewmodel
////
////import android.content.Context
////import android.os.Build
////import android.util.Log
////import androidx.annotation.RequiresApi
////import androidx.lifecycle.*
////import com.example.parttracker.constants.modelToParts
////import com.example.parttracker.model.*
////import com.example.parttracker.repository.PartRepository
////import com.example.parttracker.firebase.FirebaseSyncManager
////import kotlinx.coroutines.Dispatchers
////import kotlinx.coroutines.delay
////import kotlinx.coroutines.launch
////import kotlinx.coroutines.withContext
////import java.time.LocalDate
////import java.time.LocalTime
////
////@RequiresApi(Build.VERSION_CODES.O)
////class DashboardViewModel(private val repository: PartRepository) : ViewModel() {
////
////    private val _dashboardRows = MutableLiveData<List<DashboardRow>>()
////    val dashboardRows: LiveData<List<DashboardRow>> = _dashboardRows
////
////    private val _sequenceGroups = MutableLiveData<List<SequenceGroup>>()
////    val sequenceGroups: LiveData<List<SequenceGroup>> = _sequenceGroups
////
////    private val _inventoryCBList = MutableLiveData<List<ModelGroup>>()
////    val inventoryCBList: LiveData<List<ModelGroup>> = _inventoryCBList
////
////    private fun buildInventoryStructure(rows: List<DashboardRow>): List<ModelGroup> {
////        return rows.groupBy { it.model }.map { (model, modelRows) ->
////            val colors = modelRows.groupBy { it.color }.map { (color, partRows) ->
////                val parts = partRows.map {
////                    CBPartEntry(partName = it.partName, cb = it.cb)
////                }
////                ColorGroup(color = color, partsCB = parts)
////            }
////            ModelGroup(model = model, colorGroups = colors)
////        }
////    }
////
////    private fun getCurrentShift(): Pair<String, String> {
////        val now = LocalTime.now()
////        val today = LocalDate.now()
////        return when {
////            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
////            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
////            else -> today.minusDays(1).toString() to "B"
////        }
////    }
////
////    fun loadDashboardData(context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            val (date, shift) = getCurrentShift()
////            val plans = repository.planDao.getTodayPlans(date, shift)
////            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()
////
////            for (plan in plans) {
////                val parts = modelToParts[plan.model] ?: emptyList()
////                for (part in parts) {
////                    val key = Triple(plan.model, plan.color, part)
////                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
////                }
////            }
////
////            val dashboardList = mutableListOf<DashboardRow>()
////            for ((key, plannedQty) in partPlanMap) {
////                val (model, color, partName) = key
////
////                // Skip specific shields
////                if (partName == "Shield LH/RH" &&
////                    model in listOf("GA-3501", "DA-3503", "LA-3502") &&
////                    color.equals("Bro. Black", ignoreCase = true)
////                ) continue
////
////                val prodEntry = repository.getModelProduction(model, color, date, shift)
////                val existingTodayRow = repository.getDashboardRow(date, shift, model, color, partName)
////
////                val ob = existingTodayRow?.ob ?: run {
////                    val yesterday = LocalDate.parse(date).minusDays(1).toString()
////                    val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
////                    lastRow?.cb ?: 0
////                }
////
////                val produced = prodEntry?.produced ?: 0
////                val rejection = prodEntry?.rejection ?: 0
////                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0
////
////                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)
////                val dispatch = scannedParts.filter {
////                    it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence
////                }.sumOf { it.quantity }
////
////                val received = scannedParts.filter {
////                    it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence
////                }.sumOf { it.quantity }
////
////                val remainingPs = plannedQty - dispatch - ob
////                val remainingVa = plannedQty - received - ob
////                val cb = ob + received - produced - rejection
////
////                val row = DashboardRow(
////                    date, shift, model, color, partName, plannedQty, ob,
////                    dispatch, received, remainingPs, remainingVa, produced, rejection, cb
////                )
////
////                dashboardList.add(row)
////                repository.insertOrUpdateDashboard(row, context)
////            }
////
////            val sortedRows = dashboardList.sortedBy { it.partName }
////            _dashboardRows.postValue(sortedRows)
////            _inventoryCBList.postValue(buildInventoryStructure(sortedRows))
////
////            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
////            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
////                val (_, rows) = entry
////                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
////                    ColorGroupedDashboard(color = color, rows = colorRows)
////                }
////                val totalPlanned = rows.sumOf { it.planned }
////                val totalReceived = rows.sumOf { it.received }
////
////                SequenceGroup(index + 1, totalPlanned, totalReceived, colorGroups)
////            }
////
////            _sequenceGroups.postValue(sequenceList)
////        }
////    }
////
////    fun updateDashboardRow(updated: DashboardRow, context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            repository.insertOrUpdateDashboard(updated, context)
////            val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
////                val updatedColorGroups = group.colorGroups.map { colorGroup ->
////                    val updatedRows = colorGroup.rows.map { row ->
////                        if (row.model == updated.model && row.color == updated.color && row.partName == updated.partName) {
////                            updated
////                        } else row
////                    }
////                    colorGroup.copy(rows = updatedRows)
////                }
////                group.copy(colorGroups = updatedColorGroups)
////            }
////
////            withContext(Dispatchers.Main) {
////                _sequenceGroups.value = updatedGroups
////            }
////
////            Log.d("DashboardViewModel", "OB updated for ${updated.partName}")
////        }
////    }
////
////    fun matchUnmatchedScannedPartsThenLoadDashboard(context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            repository.matchScannedPartsToPlans()
////            loadDashboardData(context)
////        }
////    }
////
//////    fun syncFirestoreDataIfEmpty(context: Context) {
//////        viewModelScope.launch(Dispatchers.IO) {
//////            withContext(Dispatchers.Main) {
//////                val localData = dashboardRows.value ?: emptyList()
//////                if (localData.isEmpty()) {
//////                    loadDashboardData(context)
//////                }
//////            }
//////        }
//////    }
////
////    fun syncFirestoreDataIfEmpty(context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            // Assuming dashboardDao.getAllRows() returns List<DashboardRow> or similar plain List
////            val localDashboardRows = repository.dashboardDao.getAllRows()
////
////            // *** CORRECTED LINE HERE ***
////            // Use getAllPlansRaw() or getAllPlansNow() which return List<PlanEntry> directly.
////            val localPlans = repository.planDao.getAllPlansRaw() // Or getAllPlansNow()
////
////            if (localDashboardRows.isEmpty()) { // This is fine if getAllRows() returns a non-nullable List
////                FirebaseSyncManager.pullAllDashboardRows { rows ->
////                    viewModelScope.launch {
////                        for (row in rows) {
////                            repository.insertOrUpdateDashboard(row, context)
////                        }
////                        loadDashboardData(context)
////                    }
////                }
////            }
////
////            if (localPlans.isEmpty()) { // Now localPlans is List<PlanEntry>, so isEmpty() works
////                FirebaseSyncManager.pullAllPlans { plans ->
////                    viewModelScope.launch {
////                        for (plan in plans) {
////                            repository.insertPlan(plan, context)
////                        }
////                        loadDashboardData(context)
////                    }
////                }
////            }
////        }
////    }
////
////
////
////    fun startListeningForDashboardChanges(context: Context) {
////        FirebaseSyncManager.listenToDashboardUpdates(context) { updatedRow ->
////            viewModelScope.launch {
////                repository.insertOrUpdateDashboard(updatedRow, context)
////                loadDashboardData(context)
////            }
////        }
////    }
////
////    fun startListeningForScannedParts(context: Context) {
////        FirebaseSyncManager.listenToScannedPartsUpdates(context) { scannedPart ->
////            viewModelScope.launch {
////                repository.insert(scannedPart, context)
////                loadDashboardData(context)
////            }
////        }
////    }
////
////    fun startListeningForUsedParts(context: Context) {
////        FirebaseSyncManager.listenToUsedPartsUpdates { usedPart ->
////            viewModelScope.launch {
////                repository.insertUsedPart(usedPart.partName, usedPart.quantity, context)
////                loadDashboardData(context)
////            }
////        }
////    }
////
////    fun startListeningForPlans(context: Context) {
////        FirebaseSyncManager.listenToPlanUpdates { plan ->
////            viewModelScope.launch {
////                repository.insertPlan(plan, context)
////                delay(300) // delay helps to sync Firebase writes fully
////                loadDashboardData(context)
////            }
////        }
////    }
////
////    fun startListeningForModelProduction(context: Context) {
////        FirebaseSyncManager.listenToModelProductionUpdates { entry ->
////            viewModelScope.launch {
////                repository.insertModelProduction(entry, context)
////                loadDashboardData(context)
////            }
////        }
////    }
////
////    fun saveModelProduction(
////        model: String,
////        color: String,
////        produced: Int,
////        rejection: Int,
////        context: Context
////    ) {
////        viewModelScope.launch(Dispatchers.IO) {
////            val (date, shift) = getCurrentShift()
////            val existingEntry = repository.getModelProduction(model, color, date, shift)
////
////            val preservedOB = existingEntry?.openingBalance ?: run {
////                val parts = modelToParts[model] ?: emptyList()
////                if (parts.isNotEmpty()) {
////                    val lastRow = repository.getLastDashboardRowForPart(model, color, parts[0], date, shift)
////                    lastRow?.cb ?: 0
////                } else 0
////            }
////
////            val updatedEntry = ModelProduction(
////                model, color, date, shift, preservedOB, produced, rejection
////            )
////
////            repository.insertModelProduction(updatedEntry, context)
////            loadDashboardData(context)
////        }
////    }
////
////
////}
//
//package com.example.parttracker.viewmodel
//
//import android.content.Context
//import android.os.Build
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.lifecycle.*
//import com.example.parttracker.constants.modelToParts
//import com.example.parttracker.model.*
//import com.example.parttracker.repository.PartRepository
//import com.example.parttracker.firebase.FirebaseSyncManager
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.time.LocalDate
//import java.time.LocalTime
//
//@RequiresApi(Build.VERSION_CODES.O)
//class DashboardViewModel(private val repository: PartRepository) : ViewModel() {
//
//    private val _dashboardRows = MutableLiveData<List<DashboardRow>>()
//    val dashboardRows: LiveData<List<DashboardRow>> = _dashboardRows
//
//    private val _sequenceGroups = MutableLiveData<List<SequenceGroup>>()
//    val sequenceGroups: LiveData<List<SequenceGroup>> = _sequenceGroups
//
//    private val _inventoryCBList = MutableLiveData<List<ModelGroup>>()
//    val inventoryCBList: LiveData<List<ModelGroup>> = _inventoryCBList
//
//    private fun buildInventoryStructure(rows: List<DashboardRow>): List<ModelGroup> {
//        return rows.groupBy { it.model }.map { (model, modelRows) ->
//            val colors = modelRows.groupBy { it.color }.map { (color, partRows) ->
//                val parts = partRows.map {
//                    CBPartEntry(partName = it.partName, cb = it.cb)
//                }
//                ColorGroup(color = color, partsCB = parts)
//            }
//            ModelGroup(model = model, colorGroups = colors)
//        }
//    }
//
//    private fun getCurrentShift(): Pair<String, String> {
//        val now = LocalTime.now()
//        val today = LocalDate.now()
//        return when {
//            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
//            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
//            else -> today.minusDays(1).toString() to "B"
//        }
//    }
//
//    fun loadDashboardData(context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val (date, shift) = getCurrentShift()
//            val plans = repository.planDao.getTodayPlans(date, shift)
//            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()
//
//            for (plan in plans) {
//                val parts = modelToParts[plan.model] ?: emptyList()
//                for (part in parts) {
//                    val key = Triple(plan.model, plan.color, part)
//                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
//                }
//            }
//
//            val dashboardList = mutableListOf<DashboardRow>()
//            for ((key, plannedQty) in partPlanMap) {
//                val (model, color, partName) = key
//
//                if (partName == "Shield LH/RH" &&
//                    model in listOf("GA-3501", "DA-3503", "LA-3502") &&
//                    color.equals("Bro. Black", ignoreCase = true)
//                ) continue
//
//                val prodEntry = repository.getModelProduction(model, color, date, shift)
//                val existingTodayRow = repository.getDashboardRow(date, shift, model, color, partName)
//
//                val ob = existingTodayRow?.ob ?: run {
//                    val yesterday = LocalDate.parse(date).minusDays(1).toString()
//                    val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
//                    lastRow?.cb ?: 0
//                }
//
//                val produced = prodEntry?.produced ?: 0
//                val rejection = prodEntry?.rejection ?: 0
//                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0
//
//                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)
//                val dispatch = scannedParts.filter {
//                    it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence
//                }.sumOf { it.quantity }
//
//                val received = scannedParts.filter {
//                    it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence
//                }.sumOf { it.quantity }
//
//                val remainingPs = plannedQty - dispatch - ob
//                val remainingVa = plannedQty - received - ob
//                val cb = ob + received - produced - rejection
//
//                val row = DashboardRow(
//                    date, shift, model, color, partName, plannedQty, ob,
//                    dispatch, received, remainingPs, remainingVa, produced, rejection, cb
//                )
//
//                dashboardList.add(row)
//                repository.insertOrUpdateDashboard(row, context)
//
//                // ✅ Push to Firebase so other devices get it
//                FirebaseSyncManager.pushDashboardRow(row, context)
//            }
//
//            val sortedRows = dashboardList.sortedBy { it.partName }
//            _dashboardRows.postValue(sortedRows)
//            _inventoryCBList.postValue(buildInventoryStructure(sortedRows))
//
//            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
//            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
//                val (_, rows) = entry
//                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
//                    ColorGroupedDashboard(color = color, rows = colorRows)
//                }
//                val totalPlanned = rows.sumOf { it.planned }
//                val totalReceived = rows.sumOf { it.received }
//
//                SequenceGroup(index + 1, totalPlanned, totalReceived, colorGroups)
//            }
//
//            _sequenceGroups.postValue(sequenceList)
//        }
//    }
//
////    fun updateDashboardRow(updated: DashboardRow, context: Context) {
////        viewModelScope.launch(Dispatchers.IO) {
////            repository.insertOrUpdateDashboard(updated, context)
////
////            // ✅ Push to Firebase
////            FirebaseSyncManager.pushDashboardRow(updated, context)
////
////            val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
////                val updatedColorGroups = group.colorGroups.map { colorGroup ->
////                    val updatedRows = colorGroup.rows.map { row ->
////                        if (row.model == updated.model && row.color == updated.color && row.partName == updated.partName) {
////                            updated
////                        } else row
////                    }
////                    colorGroup.copy(rows = updatedRows)
////                }
////                group.copy(colorGroups = updatedColorGroups)
////            }
////
////            withContext(Dispatchers.Main) {
////                _sequenceGroups.value = updatedGroups
////            }
////
////            Log.d("DashboardViewModel", "OB updated and pushed for ${updated.partName}")
////        }
////    }
//
//    fun updateDashboardRow(updated: DashboardRow, context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insertOrUpdateDashboard(updated, context)
//
//            // 🆕 Add this line to sync update to Firestore
//            FirebaseSyncManager.pushDashboardRow(updated, context)
//
//            val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
//                val updatedColorGroups = group.colorGroups.map { colorGroup ->
//                    val updatedRows = colorGroup.rows.map { row ->
//                        if (row.model == updated.model && row.color == updated.color && row.partName == updated.partName) {
//                            updated
//                        } else row
//                    }
//                    colorGroup.copy(rows = updatedRows)
//                }
//                group.copy(colorGroups = updatedColorGroups)
//            }
//
//            withContext(Dispatchers.Main) {
//                _sequenceGroups.value = updatedGroups
//            }
//
//            Log.d("DashboardViewModel", "OB updated and synced for ${updated.partName}")
//        }
//    }
//
//
//    fun matchUnmatchedScannedPartsThenLoadDashboard(context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.matchScannedPartsToPlans()
//            loadDashboardData(context)
//        }
//    }
//
//    fun syncFirestoreDataIfEmpty(context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val localDashboardRows = repository.dashboardDao.getAllRows()
//            val localPlans = repository.planDao.getAllPlansRaw()
//
//            if (localDashboardRows.isEmpty()) {
//                FirebaseSyncManager.pullAllDashboardRows { rows ->
//                    viewModelScope.launch {
//                        for (row in rows) {
//                            repository.insertOrUpdateDashboard(row, context)
//                        }
//                        loadDashboardData(context)
//                    }
//                }
//            }
//
//            if (localPlans.isEmpty()) {
//                FirebaseSyncManager.pullAllPlans { plans ->
//                    viewModelScope.launch {
//                        for (plan in plans) {
//                            repository.insertPlan(plan, context)
//                        }
//                        loadDashboardData(context)
//                    }
//                }
//            }
//        }
//    }
//
//    fun startListeningForDashboardChanges(context: Context) {
//        FirebaseSyncManager.listenToDashboardUpdates(context) { updatedRow ->
//            viewModelScope.launch {
//                repository.insertOrUpdateDashboard(updatedRow, context)
//                loadDashboardData(context)
//            }
//        }
//    }
//
//    fun startListeningForScannedParts(context: Context) {
//        FirebaseSyncManager.listenToScannedPartsUpdates(context) { scannedPart ->
//            viewModelScope.launch {
//                repository.insert(scannedPart, context)
//                loadDashboardData(context)
//            }
//        }
//    }
//
//    fun startListeningForUsedParts(context: Context) {
//        FirebaseSyncManager.listenToUsedPartsUpdates { usedPart ->
//            viewModelScope.launch {
//                repository.insertUsedPart(usedPart.partName, usedPart.quantity, context)
//                loadDashboardData(context)
//            }
//        }
//    }
//
//    fun startListeningForPlans(context: Context) {
//        FirebaseSyncManager.listenToPlanUpdates { plan ->
//            viewModelScope.launch {
//                repository.insertPlan(plan, context)
//                delay(300)
//                loadDashboardData(context)
//            }
//        }
//    }
//
//    fun startListeningForModelProduction(context: Context) {
//        FirebaseSyncManager.listenToModelProductionUpdates { entry ->
//            viewModelScope.launch {
//                repository.insertModelProduction(entry, context)
//                loadDashboardData(context)
//            }
//        }
//    }
//
//    fun saveModelProduction(
//        model: String,
//        color: String,
//        produced: Int,
//        rejection: Int,
//        context: Context
//    ) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val (date, shift) = getCurrentShift()
//            val existingEntry = repository.getModelProduction(model, color, date, shift)
//
//            val preservedOB = existingEntry?.openingBalance ?: run {
//                val parts = modelToParts[model] ?: emptyList()
//                if (parts.isNotEmpty()) {
//                    val lastRow = repository.getLastDashboardRowForPart(model, color, parts[0], date, shift)
//                    lastRow?.cb ?: 0
//                } else 0
//            }
//
//            val updatedEntry = ModelProduction(
//                model, color, date, shift, preservedOB, produced, rejection
//            )
//
//            repository.insertModelProduction(updatedEntry, context)
//
//            // ✅ Push to Firebase
//            FirebaseSyncManager.pushModelProduction(updatedEntry, context)
//
//            loadDashboardData(context)
//        }
//    }
//}
//
//

package com.example.parttracker.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.parttracker.constants.modelToParts
import com.example.parttracker.model.*
import com.example.parttracker.repository.PartRepository
import com.example.parttracker.firebase.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.asFlow


@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(private val repository: PartRepository) : ViewModel() {

    private val _dashboardRows = MutableLiveData<List<DashboardRow>>()
    val dashboardRows: LiveData<List<DashboardRow>> = _dashboardRows

    private val _sequenceGroups = MutableLiveData<List<SequenceGroup>>()
    val sequenceGroups: LiveData<List<SequenceGroup>> = _sequenceGroups

    private val _inventoryCBList = MutableLiveData<List<ModelGroup>>()
    val inventoryCBList: LiveData<List<ModelGroup>> = _inventoryCBList

    private fun buildInventoryStructure(rows: List<DashboardRow>): List<ModelGroup> {
        return rows.groupBy { it.model }.map { (model, modelRows) ->
            val colors = modelRows.groupBy { it.color }.map { (color, partRows) ->
                val parts = partRows.map {
                    CBPartEntry(partName = it.partName, cb = it.cb)
                }
                ColorGroup(color = color, partsCB = parts)
            }
            ModelGroup(model = model, colorGroups = colors)
        }
    }

    private fun getCurrentShift(): Pair<String, String> {
        val now = LocalTime.now()
        val today = LocalDate.now()
        return when {
            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
            else -> today.minusDays(1).toString() to "B"
        }
    }

//    fun loadDashboardData(context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val (date, shift) = getCurrentShift()
//            val plans = repository.planDao.getTodayPlans(date, shift)
//            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()
//
//            for (plan in plans) {
//                val parts = modelToParts[plan.model] ?: emptyList()
//                for (part in parts) {
//                    val key = Triple(plan.model, plan.color, part)
//                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
//                }
//            }
//
//            val dashboardList = mutableListOf<DashboardRow>()
//            for ((key, plannedQty) in partPlanMap) {
//                val (model, color, partName) = key
//
//                if (partName == "Shield LH/RH" &&
//                    model in listOf("GA-3501", "DA-3503", "LA-3502") &&
//                    color.equals("Bro. Black", ignoreCase = true)
//                ) continue
//
//                val prodEntry = repository.getModelProduction(model, color, date, shift)
//                val existingTodayRow = repository.getDashboardRow(date, shift, model, color, partName)
//
//                val ob = existingTodayRow?.ob ?: run {
//                    val yesterday = LocalDate.parse(date).minusDays(1).toString()
//                    val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
//                    lastRow?.cb ?: 0
//                }
//
//                val produced = prodEntry?.produced ?: 0
//                val rejection = prodEntry?.rejection ?: 0
//                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0
//
//                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)
//                val dispatch = scannedParts.filter {
//                    it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence
//                }.sumOf { it.quantity }
//
//                val received = scannedParts.filter {
//                    it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence
//                }.sumOf { it.quantity }
//
//                val remainingPs = plannedQty - dispatch - ob
//                val remainingVa = plannedQty - received - ob
//                val cb = ob + received - produced - rejection
//
//                val row = DashboardRow(
//                    date, shift, model, color, partName, plannedQty, ob,
//                    dispatch, received, remainingPs, remainingVa, produced, rejection, cb
//                )
//
//                Log.d("OB_CALC", "OB for $partName = $ob | CB = $cb")
//
//                dashboardList.add(row)
//                repository.insertOrUpdateDashboard(row, context)
//
//                FirebaseSyncManager.pushDashboardRow(row, context)
//            }
//
//            val sortedRows = dashboardList.sortedBy { it.partName }
//            _dashboardRows.postValue(sortedRows)
//            _inventoryCBList.postValue(buildInventoryStructure(sortedRows))
//
//            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
//            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
//                val (_, rows) = entry
//                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
//                    ColorGroupedDashboard(color = color, rows = colorRows)
//                }
//                val totalPlanned = rows.sumOf { it.planned }
//                val totalReceived = rows.sumOf { it.received }
//
//                SequenceGroup(index + 1, totalPlanned, totalReceived, colorGroups)
//            }
//
//            _sequenceGroups.postValue(sequenceList.toList()) // ✅ Force UI update with new reference
//        }
//    }

//    fun loadDashboardData(context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val (date, shift) = getCurrentShift()
//            val plans = repository.planDao.getTodayPlans(date, shift)
//            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()
//
//            for (plan in plans) {
//                val parts = modelToParts[plan.model] ?: emptyList()
//                for (part in parts) {
//                    val key = Triple(plan.model, plan.color, part)
//                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
//                }
//            }
//
//            val dashboardList = mutableListOf<DashboardRow>()
//            for ((key, plannedQty) in partPlanMap) {
//                val (model, color, partName) = key
//
//                // Skip condition
//                if (partName == "Shield LH/RH" &&
//                    model in listOf("GA-3501", "DA-3503", "LA-3502") &&
//                    color.equals("Bro. Black", ignoreCase = true)
//                ) continue
//
//                val existingTodayRow = repository.getDashboardRow(date, shift, model, color, partName)
//
//                if (existingTodayRow != null) {
//                    dashboardList.add(existingTodayRow) // ✅ Use synced data
//                    continue
//                }
//
//                val yesterday = LocalDate.parse(date).minusDays(1).toString()
//                val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
//                val ob = lastRow?.cb ?: 0
//
//                val prodEntry = repository.getModelProduction(model, color, date, shift)
//                val produced = prodEntry?.produced ?: 0
//                val rejection = prodEntry?.rejection ?: 0
//                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0
//
//                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)
//                val dispatch = scannedParts.filter {
//                    it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence
//                }.sumOf { it.quantity }
//
//                val received = scannedParts.filter {
//                    it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence
//                }.sumOf { it.quantity }
//
//                val remainingPs = plannedQty - dispatch - ob
//                val remainingVa = plannedQty - received - ob
//                val cb = ob + received - produced - rejection
//
//                val row = DashboardRow(
//                    date, shift, model, color, partName, plannedQty, ob,
//                    dispatch, received, remainingPs, remainingVa, produced, rejection, cb
//                )
//
//                Log.d("OB_CALC", "OB for $partName = $ob | CB = $cb")
//
//                dashboardList.add(row)
//                repository.insertOrUpdateDashboard(row, context)
//                FirebaseSyncManager.pushDashboardRow(row, context) // ✅ Push to Firestore
//            }
//
//            val sortedRows = dashboardList.sortedBy { it.partName }
//            _dashboardRows.postValue(sortedRows)
//            _inventoryCBList.postValue(buildInventoryStructure(sortedRows))
//
//            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
//            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
//                val (_, rows) = entry
//                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
//                    ColorGroupedDashboard(color = color, rows = colorRows)
//                }
//                val totalPlanned = rows.sumOf { it.planned }
//                val totalReceived = rows.sumOf { it.received }
//
//                SequenceGroup(index + 1, totalPlanned, totalReceived, colorGroups)
//            }
//
//            _sequenceGroups.postValue(sequenceList.toList())
//        }
//    }


    fun loadDashboardData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val (date, shift) = getCurrentShift()
            val plans = repository.planDao.getTodayPlans(date, shift)
                .sortedBy { it.sequence }



            val partPlanMap = mutableMapOf<Triple<String, String, String>, Int>()

            // Build plan map from model and its parts
            for (plan in plans) {
                val parts = modelToParts[plan.model] ?: emptyList()
                for (part in parts) {
                    val key = Triple(plan.model, plan.color, part)
                    partPlanMap[key] = partPlanMap.getOrDefault(key, 0) + plan.quantity
                }
            }

            val dashboardList = mutableListOf<DashboardRow>()

            for ((key, plannedQty) in partPlanMap) {
                val (model, color, partName) = key

                // Skip logic
                if (partName == "Shield LH/RH" &&
                    model in listOf("GA-3501", "DA-3503", "LA-3502") &&
                    color.equals("Bro. Black", ignoreCase = true)
                ) continue

                val prodEntry = repository.getModelProduction(model, color, date, shift)
                val existingTodayRow = repository.getDashboardRow(date, shift, model, color, partName)

                // Determine OB: from existing or last row
//                val ob = existingTodayRow?.ob ?: run {
//                    val yesterday = LocalDate.parse(date).minusDays(1).toString()
//                    val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
//                    lastRow?.cb ?: 0
//                }

                val ob = if (existingTodayRow?.obManuallySet == true) {
                    existingTodayRow.ob
                } else {
                    val yesterday = LocalDate.parse(date).minusDays(1).toString()
                    val lastRow = repository.getLastDashboardRowForPart(model, color, partName, yesterday, shift)
                    lastRow?.cb ?: 0
                }


                val produced = prodEntry?.produced ?: 0
                val rejection = prodEntry?.rejection ?: 0
                val sequence = plans.find { it.model == model && it.color == color }?.sequence ?: 0

                val scannedParts = repository.getPartsByModelColorDateShift(model, color, date, shift)

//                val dispatch = scannedParts.filter {
//                    it.location == "Paintshop" && it.partName == partName && it.sequenceNumber == sequence
//                }.sumOf { it.quantity }
//
//                val received = scannedParts.filter {
//                    it.location == "CTL" && it.partName == partName && it.sequenceNumber == sequence
//                }.sumOf { it.quantity }


                val dispatch = scannedParts.filter {
                    it.location == "Paint Shop" && it.partName == partName
                }.sumOf { it.quantity }

                val received = scannedParts.filter {
                    it.location == "CTL" && it.partName == partName
                }.sumOf { it.quantity }


                val remainingPs = plannedQty - dispatch - ob
                val remainingVa = plannedQty - received - ob
                val cb = ob + received - produced - rejection

                val row = DashboardRow(
                    date = date,
                    shift = shift,
                    model = model,
                    color = color,
                    partName = partName,
                    planned = plannedQty,
                    ob = ob,
                    dispatch = dispatch,
                    received = received,
                    remainingPs = remainingPs,
                    remainingVa = remainingVa,
                    produced = produced,
                    rejection = rejection,
                    cb = cb,
                    obManuallySet = existingTodayRow?.obManuallySet == true

                )

                Log.d("OB_CALC", "OB for $partName = $ob | CB = $cb")

                // Always update DB + Firebase
                repository.insertOrUpdateDashboard(row, context)
                FirebaseSyncManager.pushDashboardRow(row, context)

                dashboardList.add(row)
            }

            // Update LiveData with new list
            val sortedRows = dashboardList.sortedBy { it.partName }
            _dashboardRows.postValue(sortedRows)
            _inventoryCBList.postValue(buildInventoryStructure(sortedRows))

            // Grouping by model-color for sequence visualization
            val groupedByModelColor = sortedRows.groupBy { it.model to it.color }
            val sequenceList = groupedByModelColor.entries.mapIndexed { index, entry ->
                val (_, rows) = entry
                val colorGroups = rows.groupBy { it.color }.map { (color, colorRows) ->
                    ColorGroupedDashboard(color = color, rows = colorRows)
                }
                val totalPlanned = rows.sumOf { it.planned }
                val totalReceived = rows.sumOf { it.received }

                SequenceGroup(index + 1, totalPlanned, totalReceived, colorGroups)
            }

            _sequenceGroups.postValue(sequenceList)
        }
    }



//    fun updateDashboardRow(updated: DashboardRow, context: Context) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insertOrUpdateDashboard(updated, context)
//            FirebaseSyncManager.pushDashboardRow(updated, context)
//
//            val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
//                val updatedColorGroups = group.colorGroups.map { colorGroup ->
//                    val updatedRows = colorGroup.rows.map { row ->
//                        if (row.model == updated.model && row.color == updated.color && row.partName == updated.partName) {
//                            updated
//                        } else row
//                    }
//                    colorGroup.copy(rows = updatedRows)
//                }
//                group.copy(colorGroups = updatedColorGroups)
//            }
//
//            withContext(Dispatchers.Main) {
//                _sequenceGroups.value = updatedGroups.toList() // ✅ Trigger LiveData change
//            }
//
//            Log.d("DashboardViewModel", "OB updated and synced for ${updated.partName}")
//        }
//    }


    fun updateDashboardRow(updated: DashboardRow, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            // ✅ Mark OB as manually set
            val updatedRow = updated.copy(obManuallySet = true)

            // ✅ Save to local DB and sync to Firebase
            repository.insertOrUpdateDashboard(updatedRow, context)
            FirebaseSyncManager.pushDashboardRow(updatedRow, context)

            // ✅ Update LiveData
            val updatedGroups = _sequenceGroups.value.orEmpty().map { group ->
                val updatedColorGroups = group.colorGroups.map { colorGroup ->
                    val updatedRows = colorGroup.rows.map { row ->
                        if (
                            row.model == updatedRow.model &&
                            row.color == updatedRow.color &&
                            row.partName == updatedRow.partName
                        ) {
                            updatedRow
                        } else row
                    }
                    colorGroup.copy(rows = updatedRows)
                }
                group.copy(colorGroups = updatedColorGroups)
            }

            withContext(Dispatchers.Main) {
                _sequenceGroups.value = updatedGroups
            }

            Log.d("DashboardViewModel", "OB manually updated and synced for ${updatedRow.partName}")
        }
    }



    fun matchUnmatchedScannedPartsThenLoadDashboard(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.matchScannedPartsToPlans()
            loadDashboardData(context)
        }
    }

    fun syncFirestoreDataIfEmpty(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val localDashboardRows = repository.dashboardDao.getAllRows()
            val localPlans = repository.planDao.getAllPlansRaw()

            if (localDashboardRows.isEmpty()) {
                FirebaseSyncManager.pullAllDashboardRows { rows ->
                    viewModelScope.launch {
                        for (row in rows) {
                            repository.insertOrUpdateDashboard(row, context)
                        }
                        loadDashboardData(context)
                    }
                }
            }

            if (localPlans.isEmpty()) {
                FirebaseSyncManager.pullAllPlans { plans ->
                    viewModelScope.launch {
                        for (plan in plans) {
                            repository.insertPlan(plan, context)
                        }
                        loadDashboardData(context)
                    }
                }
            }
        }
    }

    fun startListeningForDashboardChanges(context: Context) {
        FirebaseSyncManager.listenToDashboardUpdates(context) { updatedRow ->
            viewModelScope.launch {
                repository.insertOrUpdateDashboard(updatedRow, context)
                loadDashboardData(context)
            }
        }
    }

    fun startListeningForScannedParts(context: Context) {
        FirebaseSyncManager.listenToScannedPartsUpdates(context) { scannedPart ->
            viewModelScope.launch {
                repository.insert(scannedPart, context)
                repository.matchScannedPartsToPlans()
                loadDashboardData(context)
            }
        }
    }

    fun startListeningForUsedParts(context: Context) {
        FirebaseSyncManager.listenToUsedPartsUpdates { usedPart ->
            viewModelScope.launch {
                repository.insertUsedPart(usedPart.partName, usedPart.quantity, context)
                loadDashboardData(context)
            }
        }
    }

//    fun startListeningForPlans(context: Context) {
//        FirebaseSyncManager.listenToPlanUpdates { plan ->
//            viewModelScope.launch {
//                repository.insertPlan(plan, context)
//                delay(300)
//                loadDashboardData(context)
//            }
//        }
//    }

    fun startListeningForPlans(context: Context) {
        FirebaseSyncManager.listenToPlanUpdates { plan ->
            viewModelScope.launch {
                val existing = repository.planDao.getPlan(
                    plan.model, plan.color, plan.date, plan.shift, plan.sequence
                )

                if (existing == null) {
                    repository.insertPlan(plan, context)
                    delay(300)
                    loadDashboardData(context)
                } else {
                    Log.d("PlanListener", "Duplicate plan skipped: ${plan.model} - ${plan.color} - ${plan.sequence}")
                }
            }
        }
    }


    fun startListeningForModelProduction(context: Context) {
        FirebaseSyncManager.listenToModelProductionUpdates { entry ->
            viewModelScope.launch {
                repository.insertModelProduction(entry, context)
                loadDashboardData(context)
            }
        }
    }


    fun fullSyncFromFirebase(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val localDashboardRows = repository.dashboardDao.getAllRows()
            val localPlans = repository.planDao.getAllPlansRaw()
            val localScannedParts = repository.scannedPartDao.getAllScannedParts()
            //val localUsedParts = repository.usedPartDao.getAllUsedParts()
            val localUsedParts = repository.usedPartDao.getAllUsedParts().asFlow().first()


            val localModelProductions = repository.modelProductionDao.getAllModelProductions()

            // 🔄 Dashboard rows
            if (localDashboardRows.isEmpty()) {
                FirebaseSyncManager.pullAllDashboardRows { rows ->
                    viewModelScope.launch {
                        for (row in rows) {
                            repository.insertOrUpdateDashboard(row, context)
                        }
                        loadDashboardData(context)
                    }
                }
            }

            // 📦 Plans
            if (localPlans.isEmpty()) {
                FirebaseSyncManager.pullAllPlans { plans ->
                    viewModelScope.launch {
                        for (plan in plans) {
                            repository.insertPlan(plan, context)
                        }
                        loadDashboardData(context)
                    }
                }
            }

            // 📥 Scanned parts
            if (localScannedParts.isEmpty()) {
                FirebaseSyncManager.pullAllScannedParts { scannedParts ->
                    viewModelScope.launch {
                        for (part in scannedParts) {
                            repository.insert(part, context)
                        }
                        loadDashboardData(context)
                    }
                }
            }

            // 🧾 Used parts

            if (localUsedParts.isEmpty()) {
                FirebaseSyncManager.pullAllUsedParts { usedParts ->
                    viewModelScope.launch {
                        for (used in usedParts) {
                            repository.insertUsedPart(used.partName, used.quantity, context)
                        }
                        loadDashboardData(context)
                    }
                }
            }


            // ⚙️ Model production
            if (localModelProductions.isEmpty()) {
                FirebaseSyncManager.pullAllModelProduction { entries ->
                    viewModelScope.launch {
                        for (entry in entries) {
                            repository.insertModelProduction(entry, context)
                        }
                        loadDashboardData(context)
                    }
                }
            }
        }
    }


    fun saveModelProduction(
        model: String,
        color: String,
        produced: Int,
        rejection: Int,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val (date, shift) = getCurrentShift()
            val existingEntry = repository.getModelProduction(model, color, date, shift)

            val preservedOB = existingEntry?.openingBalance ?: run {
                val parts = modelToParts[model] ?: emptyList()
                if (parts.isNotEmpty()) {
                    val lastRow = repository.getLastDashboardRowForPart(model, color, parts[0], date, shift)
                    lastRow?.cb ?: 0
                } else 0
            }

            val updatedEntry = ModelProduction(
                model, color, date, shift, preservedOB, produced, rejection
            )

            repository.insertModelProduction(updatedEntry, context)
            FirebaseSyncManager.pushModelProduction(updatedEntry, context)
            loadDashboardData(context)
        }
    }
}
