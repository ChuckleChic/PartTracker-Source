////////////////////package com.example.parttracker.ui
////////////////////
////////////////////import android.os.Build
////////////////////import android.os.Bundle
////////////////////import android.view.LayoutInflater
////////////////////import android.view.View
////////////////////import android.view.ViewGroup
////////////////////import android.widget.*
////////////////////import androidx.annotation.RequiresApi
////////////////////import androidx.appcompat.app.AlertDialog
////////////////////import androidx.fragment.app.Fragment
////////////////////import androidx.fragment.app.viewModels
////////////////////import androidx.lifecycle.lifecycleScope
////////////////////import androidx.navigation.fragment.findNavController
////////////////////import androidx.recyclerview.widget.LinearLayoutManager
////////////////////import androidx.recyclerview.widget.RecyclerView
////////////////////import com.example.parttracker.R
////////////////////import com.example.parttracker.adapter.PlanAdapter
////////////////////import com.example.parttracker.model.PlanEntry
////////////////////import com.example.parttracker.model.PlanWithProduced
////////////////////import com.example.parttracker.viewmodel.PlanViewModel
////////////////////import com.google.android.material.floatingactionbutton.FloatingActionButton
////////////////////import kotlinx.coroutines.flow.combine
////////////////////import kotlinx.coroutines.launch
////////////////////import com.example.parttracker.model.DashboardRow
////////////////////
////////////////////
////////////////////class PlanFragment : Fragment() {
////////////////////
////////////////////    private val planViewModel: PlanViewModel by viewModels()
////////////////////
////////////////////    @RequiresApi(Build.VERSION_CODES.O)
////////////////////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////////////////////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
////////////////////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
////////////////////
////////////////////        // Updated adapter to use PlanWithProduced
////////////////////        val adapter = PlanAdapter(emptyList()) { planWithProduced ->
////////////////////            val action = PlanFragmentDirections.actionPlanFragmentToDashboardFragment(
////////////////////                selectedDate = planWithProduced.plan.date,
////////////////////                selectedShift = planWithProduced.plan.shift
////////////////////            )
////////////////////            findNavController().navigate(action)
////////////////////        }
////////////////////
////////////////////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////////////////////        recyclerView.adapter = adapter
////////////////////
////////////////////        // Combine plan data and dashboard data
////////////////////        lifecycleScope.launch {
////////////////////            combine(
////////////////////                planViewModel.allPlansFlow,           // Flow<List<PlanEntry>>
////////////////////                planViewModel.allDashboardFlow        // Flow<List<DashboardRow>>
////////////////////            ) { plans, dashboardRows ->
////////////////////                plans.map { plan ->
////////////////////                    val totalProduced = dashboardRows
////////////////////                        .filter { it.date == plan.date && it.shift == plan.shift && it.model == plan.model }
////////////////////                        .sumOf { it.planned }
////////////////////
////////////////////                    PlanWithProduced(plan, totalProduced)
////////////////////                }
////////////////////            }.collect { planWithProducedList ->
////////////////////                adapter.updateData(planWithProducedList)
////////////////////            }
////////////////////        }
////////////////////
////////////////////        fabAdd.setOnClickListener {
////////////////////            showAddPlanDialog()
////////////////////        }
////////////////////    }
////////////////////
////////////////////    @RequiresApi(Build.VERSION_CODES.O)
////////////////////    private fun showAddPlanDialog() {
////////////////////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
////////////////////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
////////////////////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
////////////////////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
////////////////////        val etModel = dialogView.findViewById<EditText>(R.id.etModel)
////////////////////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
////////////////////
////////////////////        val sequences = listOf(1, 2, 3, 4)
////////////////////        val modelMap = mapOf(1 to "3501", 2 to "3502", 3 to "3503", 4 to "3001")
////////////////////
////////////////////        val seqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences)
////////////////////        etSequence.setAdapter(seqAdapter)
////////////////////
////////////////////        etSequence.setOnItemClickListener { _, _, position, _ ->
////////////////////            val selected = sequences[position]
////////////////////            etModel.setText(modelMap[selected])
////////////////////        }
////////////////////
////////////////////        AlertDialog.Builder(requireContext())
////////////////////            .setView(dialogView)
////////////////////            .setPositiveButton("Add") { _, _ ->
////////////////////                val entry = PlanEntry(
////////////////////                    date = etDate.text.toString(),
////////////////////                    shift = etShift.text.toString(),
////////////////////                    sequence = etSequence.text.toString().toInt(),
////////////////////                    model = etModel.text.toString(),
////////////////////                    quantity = etQuantity.text.toString().toInt()
////////////////////                )
////////////////////                planViewModel.addPlan(entry)
////////////////////            }
////////////////////            .setNegativeButton("Cancel", null)
////////////////////            .create()
////////////////////            .show()
////////////////////    }
////////////////////
////////////////////    override fun onCreateView(
////////////////////        inflater: LayoutInflater, container: ViewGroup?,
////////////////////        savedInstanceState: Bundle?
////////////////////    ): View? {
////////////////////        return inflater.inflate(R.layout.fragment_plan, container, false)
////////////////////    }
////////////////////}
//////////////////
//////////////////
//////////////////package com.example.parttracker.ui
//////////////////
//////////////////import android.os.Build
//////////////////import android.os.Bundle
//////////////////import android.view.LayoutInflater
//////////////////import android.view.View
//////////////////import android.view.ViewGroup
//////////////////import android.widget.*
//////////////////import androidx.annotation.RequiresApi
//////////////////import androidx.appcompat.app.AlertDialog
//////////////////import androidx.fragment.app.Fragment
//////////////////import androidx.fragment.app.viewModels
//////////////////import androidx.lifecycle.lifecycleScope
//////////////////import androidx.navigation.fragment.findNavController
//////////////////import androidx.recyclerview.widget.LinearLayoutManager
//////////////////import androidx.recyclerview.widget.RecyclerView
//////////////////import com.example.parttracker.R
//////////////////import com.example.parttracker.adapter.PlanAdapter
//////////////////import com.example.parttracker.model.PlanEntry
//////////////////import com.example.parttracker.model.PlanWithProduced
//////////////////import com.example.parttracker.viewmodel.PlanViewModel
//////////////////import com.google.android.material.floatingactionbutton.FloatingActionButton
//////////////////import kotlinx.coroutines.flow.combine
//////////////////import kotlinx.coroutines.launch
//////////////////import com.example.parttracker.model.DashboardRow
//////////////////
//////////////////class PlanFragment : Fragment() {
//////////////////
//////////////////    private val planViewModel: PlanViewModel by viewModels()
//////////////////
//////////////////    @RequiresApi(Build.VERSION_CODES.O)
//////////////////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//////////////////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
//////////////////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
//////////////////
//////////////////        val adapter = PlanAdapter(emptyList()) { planWithProduced ->
//////////////////            val action = PlanFragmentDirections.actionPlanFragmentToDashboardFragment(
//////////////////                selectedDate = planWithProduced.plan.date,
//////////////////                selectedShift = planWithProduced.plan.shift
//////////////////            )
//////////////////            findNavController().navigate(action)
//////////////////        }
//////////////////
//////////////////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//////////////////        recyclerView.adapter = adapter
//////////////////
//////////////////        lifecycleScope.launch {
//////////////////            combine(
//////////////////                planViewModel.allPlansFlow,
//////////////////                planViewModel.allDashboardFlow
//////////////////            ) { plans, dashboardRows ->
//////////////////                plans.map { plan ->
//////////////////                    val totalProduced = dashboardRows
//////////////////                        .filter {
//////////////////                            it.date == plan.date &&
//////////////////                                    it.shift == plan.shift &&
//////////////////                                    it.model == plan.model
//////////////////                        }
//////////////////                        .sumOf { it.produced }
//////////////////
//////////////////                    PlanWithProduced(plan, totalProduced)
//////////////////                }
//////////////////            }.collect { planWithProducedList ->
//////////////////                adapter.updateData(planWithProducedList)
//////////////////            }
//////////////////        }
//////////////////
//////////////////        fabAdd.setOnClickListener {
//////////////////            showAddPlanDialog()
//////////////////        }
//////////////////    }
//////////////////
//////////////////    @RequiresApi(Build.VERSION_CODES.O)
//////////////////    private fun showAddPlanDialog() {
//////////////////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
//////////////////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
//////////////////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
//////////////////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
//////////////////        val etModel = dialogView.findViewById<EditText>(R.id.etModel)
//////////////////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
//////////////////
//////////////////        val sequences = listOf(1, 2, 3, 4)
//////////////////        val modelMap = mapOf(1 to "3501", 2 to "3502", 3 to "3503", 4 to "3001")
//////////////////
//////////////////        val seqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences)
//////////////////        etSequence.setAdapter(seqAdapter)
//////////////////
//////////////////        etSequence.setOnItemClickListener { _, _, position, _ ->
//////////////////            val selected = sequences[position]
//////////////////            etModel.setText(modelMap[selected])
//////////////////        }
//////////////////
//////////////////        AlertDialog.Builder(requireContext())
//////////////////            .setView(dialogView)
//////////////////            .setPositiveButton("Add") { _, _ ->
//////////////////                try {
//////////////////                    val entry = PlanEntry(
//////////////////                        date = etDate.text.toString(),
//////////////////                        shift = etShift.text.toString(),
//////////////////                        sequence = etSequence.text.toString().toInt(),
//////////////////                        model = etModel.text.toString(),
//////////////////                        quantity = etQuantity.text.toString().toInt()
//////////////////                    )
//////////////////                    planViewModel.addPlan(entry)
//////////////////                } catch (e: Exception) {
//////////////////                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
//////////////////                }
//////////////////            }
//////////////////            .setNegativeButton("Cancel", null)
//////////////////            .create()
//////////////////            .show()
//////////////////    }
//////////////////
//////////////////    override fun onCreateView(
//////////////////        inflater: LayoutInflater, container: ViewGroup?,
//////////////////        savedInstanceState: Bundle?
//////////////////    ): View? {
//////////////////        return inflater.inflate(R.layout.fragment_plan, container, false)
//////////////////    }
//////////////////}
////////////////
////////////////
////////////////package com.example.parttracker.ui
////////////////
////////////////import android.os.Build
////////////////import android.os.Bundle
////////////////import android.view.LayoutInflater
////////////////import android.view.View
////////////////import android.view.ViewGroup
////////////////import android.widget.*
////////////////import androidx.annotation.RequiresApi
////////////////import androidx.appcompat.app.AlertDialog
////////////////import androidx.fragment.app.Fragment
////////////////import androidx.fragment.app.viewModels
////////////////import androidx.lifecycle.lifecycleScope
////////////////import androidx.navigation.fragment.findNavController
////////////////import androidx.recyclerview.widget.LinearLayoutManager
////////////////import androidx.recyclerview.widget.RecyclerView
////////////////import com.example.parttracker.R
////////////////import com.example.parttracker.ui.adapter.PlanAdapter
////////////////import com.example.parttracker.model.PlanEntry
////////////////import com.example.parttracker.model.PlanWithProduced
////////////////import com.example.parttracker.viewmodel.PlanViewModel
////////////////import com.google.android.material.floatingactionbutton.FloatingActionButton
////////////////import kotlinx.coroutines.flow.combine
////////////////import kotlinx.coroutines.launch
////////////////import com.example.parttracker.model.DashboardEntry
////////////////
////////////////class PlanFragment : Fragment() {
////////////////
////////////////    private val planViewModel: PlanViewModel by viewModels()
////////////////
////////////////    @RequiresApi(Build.VERSION_CODES.O)
////////////////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////////////////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
////////////////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
////////////////
////////////////        val adapter = PlanAdapter(emptyList()) { planWithProduced ->
////////////////            val action = PlanFragmentDirections.actionPlanFragmentToDashboardFragment(
////////////////                selectedDate = planWithProduced.plan.date,
////////////////                selectedShift = planWithProduced.plan.shift
////////////////            )
////////////////            findNavController().navigate(action)
////////////////        }
////////////////
////////////////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////////////////        recyclerView.adapter = adapter
////////////////
////////////////        lifecycleScope.launch {
////////////////            combine<List<PlanEntry>, List<DashboardEntry>, List<PlanWithProduced>>(
////////////////                planViewModel.allPlansFlow,
////////////////                planViewModel.allDashboardFlow
////////////////            ) { plans, dashboardEntry ->
////////////////                plans.map { plan ->
////////////////                    val totalProduced = dashboardEntry
////////////////                        .filter { it.date == plan.date && it.shift == plan.shift && it.model == plan.model }
////////////////                        .sumOf { it.produced }
////////////////
////////////////                    PlanWithProduced(plan, totalProduced)
////////////////                }
////////////////            }.collect { planWithProducedList ->
////////////////                adapter.updateData(planWithProducedList)
////////////////            }
////////////////        }
////////////////
////////////////        fabAdd.setOnClickListener {
////////////////            showAddPlanDialog()
////////////////        }
////////////////    }
////////////////
////////////////    @RequiresApi(Build.VERSION_CODES.O)
////////////////    private fun showAddPlanDialog() {
////////////////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
////////////////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
////////////////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
////////////////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
////////////////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
////////////////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
////////////////
////////////////        val sequences = listOf(1, 2, 3, 4)
////////////////        val modelMap = mapOf(
////////////////            1 to "GA-3501",
////////////////            2 to "LA-3502",
////////////////            3 to "DA-3503",
////////////////            4 to "DA-3001"
////////////////        )
////////////////
////////////////        // Adapter for Sequence Dropdown
////////////////        val seqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences)
////////////////        etSequence.setAdapter(seqAdapter)
////////////////
////////////////        // Adapter for Model Dropdown
////////////////        val modelList = modelMap.values.distinct()
////////////////        val modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelList)
////////////////        etModel.setAdapter(modelAdapter)
////////////////
////////////////        // Auto-fill model based on selected sequence
////////////////        etSequence.setOnItemClickListener { _, _, position, _ ->
////////////////            val selectedSeq = sequences[position]
////////////////            etModel.setText(modelMap[selectedSeq] ?: "")
////////////////        }
////////////////
////////////////        AlertDialog.Builder(requireContext())
////////////////            .setView(dialogView)
////////////////            .setPositiveButton("Add") { _, _ ->
////////////////                try {
////////////////                    val entry = PlanEntry(
////////////////                        date = etDate.text.toString(),
////////////////                        shift = etShift.text.toString(),
////////////////                        sequence = etSequence.text.toString().toInt(),
////////////////                        model = etModel.text.toString(),
////////////////                        quantity = etQuantity.text.toString().toInt()
////////////////                    )
////////////////                    planViewModel.addPlan(entry)
////////////////
////////////////                    lifecycleScope.launch {
////////////////                        planViewModel.insertOrUpdateDashboard(entry.model, entry.quantity, entry.date, entry.shift)
////////////////                    }
////////////////
////////////////                } catch (e: Exception) {
////////////////                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
////////////////                }
////////////////            }
////////////////            .setNegativeButton("Cancel", null)
////////////////            .create()
////////////////            .show()
////////////////    }
////////////////
////////////////
////////////////    override fun onCreateView(
////////////////        inflater: LayoutInflater, container: ViewGroup?,
////////////////        savedInstanceState: Bundle?
////////////////    ): View? {
////////////////        return inflater.inflate(R.layout.fragment_plan, container, false)
////////////////    }
////////////////}
//////////////
//////////////package com.example.parttracker.ui
//////////////
//////////////import android.os.Build
//////////////import android.os.Bundle
//////////////import android.view.LayoutInflater
//////////////import android.view.View
//////////////import android.view.ViewGroup
//////////////import android.widget.*
//////////////import androidx.annotation.RequiresApi
//////////////import androidx.appcompat.app.AlertDialog
//////////////import androidx.fragment.app.Fragment
//////////////import androidx.fragment.app.viewModels
//////////////import androidx.lifecycle.lifecycleScope
//////////////import androidx.navigation.fragment.findNavController
//////////////import androidx.recyclerview.widget.LinearLayoutManager
//////////////import androidx.recyclerview.widget.RecyclerView
//////////////import com.example.parttracker.R
//////////////import com.example.parttracker.ui.adapter.PlanAdapter
//////////////import com.example.parttracker.model.PlanEntry
//////////////import com.example.parttracker.viewmodel.PlanViewModel
//////////////import com.google.android.material.floatingactionbutton.FloatingActionButton
//////////////import kotlinx.coroutines.flow.combine
//////////////import kotlinx.coroutines.launch
//////////////import com.example.parttracker.model.DashboardRow
//////////////
//////////////class PlanFragment : Fragment() {
//////////////
//////////////    private val planViewModel: PlanViewModel by viewModels()
//////////////
//////////////    @RequiresApi(Build.VERSION_CODES.O)
//////////////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//////////////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
//////////////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
//////////////
//////////////        // Use DashboardRow for new PlanAdapter
//////////////        val adapter = PlanAdapter(emptyList()) { row ->
//////////////            val action = PlanFragmentDirections.actionPlanFragmentToDashboardFragment(
//////////////                selectedDate = row.date,
//////////////                selectedShift = row.shift
//////////////            )
//////////////            findNavController().navigate(action)
//////////////        }
//////////////
//////////////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//////////////        recyclerView.adapter = adapter
//////////////
//////////////        lifecycleScope.launch {
//////////////            combine(
//////////////                planViewModel.allPlansFlow,
//////////////                planViewModel.allDashboardFlow
//////////////            ) { plans, dashboardEntries ->
//////////////                // Convert each PlanEntry to multiple DashboardRows
//////////////                plans.flatMap { plan ->
//////////////                    plan.getPartsForModel().map { part ->
//////////////                        val matching = dashboardEntries.find {
//////////////                            it.date == plan.date && it.shift == plan.shift &&
//////////////                                    it.model == plan.model && it.partName == part
//////////////                        }
//////////////                        DashboardRow(
//////////////                            date = plan.date,
//////////////                            shift = plan.shift,
//////////////                            model = plan.model,
//////////////                            quantity = plan.quantity,
//////////////                            partName = part,
//////////////                            openingBalance = matching?.openingBalance ?: 0,
//////////////                            dispatch = matching?.dispatch ?: 0,
//////////////                            received = matching?.received ?: 0,
//////////////                            remainingPs = matching?.remainingPs ?: 0,
//////////////                            remainingVa = matching?.remainingVa ?: 0,
//////////////                            produced = matching?.produced ?: 0,
//////////////                            rejection = matching?.rejection ?: 0,
//////////////                            cb = matching?.cb ?: 0
//////////////                        )
//////////////                    }
//////////////                }
//////////////            }.collect { dashboardRowList ->
//////////////                adapter.updateData(dashboardRowList)
//////////////            }
//////////////        }
//////////////
//////////////        fabAdd.setOnClickListener {
//////////////            showAddPlanDialog()
//////////////        }
//////////////    }
//////////////
//////////////    @RequiresApi(Build.VERSION_CODES.O)
//////////////    private fun showAddPlanDialog() {
//////////////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
//////////////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
//////////////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
//////////////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
//////////////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
//////////////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
//////////////
//////////////        val sequences = listOf(1, 2, 3, 4)
//////////////        val modelMap = mapOf(
//////////////            1 to "GA-3501",
//////////////            2 to "LA-3502",
//////////////            3 to "DA-3503",
//////////////            4 to "DA-3001"
//////////////        )
//////////////
//////////////        val seqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences)
//////////////        etSequence.setAdapter(seqAdapter)
//////////////
//////////////        val modelList = modelMap.values.distinct()
//////////////        val modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelList)
//////////////        etModel.setAdapter(modelAdapter)
//////////////
//////////////        etSequence.setOnItemClickListener { _, _, position, _ ->
//////////////            val selectedSeq = sequences[position]
//////////////            etModel.setText(modelMap[selectedSeq] ?: "")
//////////////        }
//////////////
//////////////        AlertDialog.Builder(requireContext())
//////////////            .setView(dialogView)
//////////////            .setPositiveButton("Add") { _, _ ->
//////////////                try {
//////////////                    val entry = PlanEntry(
//////////////                        date = etDate.text.toString(),
//////////////                        shift = etShift.text.toString(),
//////////////                        sequence = etSequence.text.toString().toInt(),
//////////////                        model = etModel.text.toString(),
//////////////                        quantity = etQuantity.text.toString().toInt()
//////////////                    )
//////////////                    planViewModel.addPlan(entry)
//////////////                    lifecycleScope.launch {
//////////////                        planViewModel.insertOrUpdateDashboard(
//////////////                            entry.model, entry.quantity, entry.date, entry.shift
//////////////                        )
//////////////                    }
//////////////                } catch (e: Exception) {
//////////////                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
//////////////                }
//////////////            }
//////////////            .setNegativeButton("Cancel", null)
//////////////            .create()
//////////////            .show()
//////////////    }
//////////////
//////////////    override fun onCreateView(
//////////////        inflater: LayoutInflater, container: ViewGroup?,
//////////////        savedInstanceState: Bundle?
//////////////    ): View? {
//////////////        return inflater.inflate(R.layout.fragment_plan, container, false)
//////////////    }
//////////////}
////////////
////////////
////////////package com.example.parttracker.ui
////////////
////////////import android.os.Build
////////////import android.os.Bundle
////////////import android.view.LayoutInflater
////////////import android.view.View
////////////import android.view.ViewGroup
////////////import android.widget.*
////////////import androidx.annotation.RequiresApi
////////////import androidx.appcompat.app.AlertDialog
////////////import androidx.fragment.app.Fragment
////////////import androidx.fragment.app.viewModels
////////////import androidx.lifecycle.lifecycleScope
////////////import androidx.navigation.fragment.findNavController
////////////import androidx.recyclerview.widget.LinearLayoutManager
////////////import androidx.recyclerview.widget.RecyclerView
////////////import com.example.parttracker.R
////////////import com.example.parttracker.model.PlanEntry
////////////import com.example.parttracker.viewmodel.PlanViewModel
////////////import com.example.parttracker.viewmodel.DashboardViewModel
////////////import com.example.parttracker.ui.adapter.PlanAdapter
////////////import com.google.android.material.floatingactionbutton.FloatingActionButton
////////////import kotlinx.coroutines.launch
////////////
////////////class PlanFragment : Fragment() {
////////////
////////////    private val planViewModel: PlanViewModel by viewModels()
////////////    private val dashboardViewModel: DashboardViewModel by viewModels()
////////////
////////////    private lateinit var adapter: PlanAdapter
////////////
////////////    @RequiresApi(Build.VERSION_CODES.O)
////////////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////////////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
////////////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
////////////
////////////        adapter = PlanAdapter(emptyList()) { row ->
////////////            val action = PlanFragmentDirections.actionPlanFragmentToDashboardFragment(
////////////                selectedDate = row.date,
////////////                selectedShift = row.shift
////////////            )
////////////            findNavController().navigate(action)
////////////        }
////////////
////////////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////////////        recyclerView.adapter = adapter
////////////
////////////        // Observe dashboard entries directly
////////////        dashboardViewModel.dashboardRow.observe(viewLifecycleOwner) { rows ->
////////////            adapter.updateData(rows)
////////////        }
////////////
////////////        fabAdd.setOnClickListener {
////////////            showAddPlanDialog()
////////////        }
////////////    }
////////////
////////////    @RequiresApi(Build.VERSION_CODES.O)
////////////    private fun showAddPlanDialog() {
////////////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
////////////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
////////////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
////////////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
////////////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
////////////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
////////////
////////////        val sequences = listOf(1, 2, 3, 4)
////////////        val modelMap = mapOf(
////////////            1 to "3501",
////////////            2 to "3502",
////////////            3 to "3503",
////////////            4 to "3001"
////////////        )
////////////
////////////        val seqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences)
////////////        etSequence.setAdapter(seqAdapter)
////////////
////////////        val modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelMap.values.toList())
////////////        etModel.setAdapter(modelAdapter)
////////////
////////////        etSequence.setOnItemClickListener { _, _, position, _ ->
////////////            etModel.setText(modelMap[sequences[position]] ?: "")
////////////        }
////////////
////////////        AlertDialog.Builder(requireContext())
////////////            .setTitle("Add Plan")
////////////            .setView(dialogView)
////////////            .setPositiveButton("Add") { _, _ ->
////////////                try {
////////////                    val plan = PlanEntry(
////////////                        date = etDate.text.toString(),
////////////                        shift = etShift.text.toString(),
////////////                        sequence = etSequence.text.toString().toInt(),
////////////                        model = etModel.text.toString(),
////////////                        quantity = etQuantity.text.toString().toInt()
////////////                    )
////////////                    planViewModel.addPlan(plan)
////////////
////////////                    lifecycleScope.launch {
////////////                        planViewModel.insertOrUpdateDashboard(
////////////                            model = plan.model,
////////////                            quantity = plan.quantity,
////////////                            date = plan.date,
////////////                            shift = plan.shift
////////////                        )
////////////                    }
////////////
////////////                } catch (e: Exception) {
////////////                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
////////////                }
////////////            }
////////////            .setNegativeButton("Cancel", null)
////////////            .create()
////////////            .show()
////////////    }
////////////
////////////    override fun onCreateView(
////////////        inflater: LayoutInflater, container: ViewGroup?,
////////////        savedInstanceState: Bundle?
////////////    ): View {
////////////        return inflater.inflate(R.layout.fragment_plan, container, false)
////////////    }
////////////}
//////////
//////////package com.example.parttracker.ui
//////////
//////////import android.os.Build
//////////import android.os.Bundle
//////////import android.view.LayoutInflater
//////////import android.view.View
//////////import android.view.ViewGroup
//////////import android.widget.*
//////////import androidx.annotation.RequiresApi
//////////import androidx.appcompat.app.AlertDialog
//////////import androidx.fragment.app.Fragment
//////////import androidx.fragment.app.viewModels
//////////import androidx.lifecycle.lifecycleScope
//////////import androidx.navigation.fragment.findNavController
//////////import androidx.recyclerview.widget.LinearLayoutManager
//////////import androidx.recyclerview.widget.RecyclerView
//////////import com.example.parttracker.R
//////////import com.example.parttracker.model.PlanEntry
//////////import com.example.parttracker.model.PlanWithProduced
//////////import com.example.parttracker.model.DashboardEntry
//////////import com.example.parttracker.ui.adapter.PlanAdapter
//////////import com.example.parttracker.viewmodel.PlanViewModel
//////////import com.google.android.material.floatingactionbutton.FloatingActionButton
//////////import kotlinx.coroutines.flow.combine
//////////import kotlinx.coroutines.launch
//////////
//////////class PlanFragment : Fragment() {
//////////
//////////    private val planViewModel: PlanViewModel by viewModels()
//////////
//////////    override fun onCreateView(
//////////        inflater: LayoutInflater, container: ViewGroup?,
//////////        savedInstanceState: Bundle?
//////////    ): View {
//////////        return inflater.inflate(R.layout.fragment_plan, container, false)
//////////    }
//////////
//////////    @RequiresApi(Build.VERSION_CODES.O)
//////////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//////////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
//////////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
//////////
//////////        val adapter = PlanAdapter(emptyList()) { planWithProduced ->
//////////            val action = PlanFragmentDirections.actionPlanFragmentToDashboardFragment(
//////////                selectedDate = planWithProduced.plan.date,
//////////                selectedShift = planWithProduced.plan.shift
//////////            )
//////////            findNavController().navigate(action)
//////////        }
//////////
//////////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//////////        recyclerView.adapter = adapter
//////////
//////////        // Combine plans + dashboard entries
//////////        lifecycleScope.launch {
//////////            combine(
//////////                planViewModel.allPlansFlow,
//////////                planViewModel.allDashboardFlow
//////////            ) { plans, dashboardEntries ->
//////////                plans.map { plan ->
//////////                    val produced = dashboardEntries
//////////                        .filter { it.date == plan.date && it.shift == plan.shift && it.model == plan.model }
//////////                        .sumOf { it.produced }
//////////
//////////                    PlanWithProduced(plan, produced)
//////////                }
//////////            }.collect { combinedList ->
//////////                adapter.updateData(combinedList)
//////////            }
//////////        }
//////////
//////////        fabAdd.setOnClickListener {
//////////            showAddPlanDialog()
//////////        }
//////////    }
//////////
//////////    @RequiresApi(Build.VERSION_CODES.O)
//////////    private fun showAddPlanDialog() {
//////////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
//////////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
//////////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
//////////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
//////////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
//////////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
//////////
//////////        val sequences = listOf(1, 2, 3, 4)
//////////        val modelMap = mapOf(
//////////            1 to "GA-3501",
//////////            2 to "LA-3502",
//////////            3 to "DA-3503",
//////////            4 to "DA-3001"
//////////        )
//////////
//////////        etSequence.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences))
//////////        etModel.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelMap.values.toList()))
//////////
//////////        etSequence.setOnItemClickListener { _, _, position, _ ->
//////////            etModel.setText(modelMap[sequences[position]])
//////////        }
//////////
//////////        AlertDialog.Builder(requireContext())
//////////            .setView(dialogView)
//////////            .setPositiveButton("Add") { _, _ ->
//////////                try {
//////////                    val entry = PlanEntry(
//////////                        date = etDate.text.toString(),
//////////                        shift = etShift.text.toString(),
//////////                        sequence = etSequence.text.toString().toInt(),
//////////                        model = etModel.text.toString(),
//////////                        quantity = etQuantity.text.toString().toInt()
//////////                    )
//////////
//////////                    // Add to plan and update dashboard
//////////                    planViewModel.addPlanAndUpdateDashboard(entry)
//////////
//////////                } catch (e: Exception) {
//////////                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
//////////                }
//////////            }
//////////            .setNegativeButton("Cancel", null)
//////////            .create()
//////////            .show()
//////////    }
//////////}
////////
////////
////////// PlanFragment.kt
////////package com.example.parttracker.ui
////////
////////import android.os.Build
////////import android.os.Bundle
////////import android.view.LayoutInflater
////////import android.view.View
////////import android.view.ViewGroup
////////import android.widget.*
////////import androidx.annotation.RequiresApi
////////import androidx.appcompat.app.AlertDialog
////////import androidx.fragment.app.Fragment
////////import androidx.fragment.app.viewModels
////////import androidx.lifecycle.lifecycleScope
////////import androidx.navigation.fragment.findNavController
////////import androidx.recyclerview.widget.LinearLayoutManager
////////import androidx.recyclerview.widget.RecyclerView
////////import com.example.parttracker.R
////////import com.example.parttracker.ui.adapter.PlanAdapter
////////import com.example.parttracker.model.PlanEntry
////////import com.example.parttracker.model.DashboardRow
////////import com.example.parttracker.viewmodel.PlanViewModel
////////import com.google.android.material.floatingactionbutton.FloatingActionButton
////////
////////class PlanFragment : Fragment() {
////////
////////    private val planViewModel: PlanViewModel by viewModels()
////////
////////    @RequiresApi(Build.VERSION_CODES.O)
////////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
////////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
////////
////////        val adapter = PlanAdapter(emptyList()) { dashboardRow ->
////////            val action = PlanFragmentDirections.actionPlanFragmentToDashboardFragment(
////////                selectedDate = dashboardRow.date,
////////                selectedShift = dashboardRow.shift
////////            )
////////            findNavController().navigate(action)
////////        }
////////
////////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////////        recyclerView.adapter = adapter
////////
////////        planViewModel.allDashboardRows.observe(viewLifecycleOwner) { dashboardList ->
////////            adapter.updateData(dashboardList)
////////        }
////////
////////        fabAdd.setOnClickListener {
////////            showAddPlanDialog()
////////        }
////////    }
////////
////////    @RequiresApi(Build.VERSION_CODES.O)
////////    private fun showAddPlanDialog() {
////////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
////////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
////////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
////////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
////////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
////////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
////////
////////        val sequences = listOf(1, 2, 3, 4)
////////        val modelMap = mapOf(
////////            1 to "GA-3501",
////////            2 to "LA-3502",
////////            3 to "DA-3503",
////////            4 to "DA-3001"
////////        )
////////
////////        val seqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences)
////////        etSequence.setAdapter(seqAdapter)
////////
////////        val modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelMap.values.toList())
////////        etModel.setAdapter(modelAdapter)
////////
////////        etSequence.setOnItemClickListener { _, _, position, _ ->
////////            val selectedSeq = sequences[position]
////////            etModel.setText(modelMap[selectedSeq] ?: "")
////////        }
////////
////////        AlertDialog.Builder(requireContext())
////////            .setView(dialogView)
////////            .setPositiveButton("Add") { _, _ ->
////////                try {
////////                    val entry = PlanEntry(
////////                        date = etDate.text.toString(),
////////                        shift = etShift.text.toString(),
////////                        sequence = etSequence.text.toString().toInt(),
////////                        model = etModel.text.toString(),
////////                        quantity = etQuantity.text.toString().toInt()
////////                    )
////////                    planViewModel.addPlanAndUpdateDashboard(entry)
////////                } catch (e: Exception) {
////////                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
////////                }
////////            }
////////            .setNegativeButton("Cancel", null)
////////            .create()
////////            .show()
////////    }
////////
////////    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
////////        return inflater.inflate(R.layout.fragment_plan, container, false)
////////    }
////////}
//////
//////
//////package com.example.parttracker.ui
//////
//////import android.os.Build
//////import android.os.Bundle
//////import android.view.LayoutInflater
//////import android.view.View
//////import android.view.ViewGroup
//////import android.widget.*
//////import androidx.annotation.RequiresApi
//////import androidx.appcompat.app.AlertDialog
//////import androidx.fragment.app.Fragment
//////import androidx.fragment.app.viewModels
//////import androidx.lifecycle.Observer
//////import androidx.navigation.fragment.findNavController
//////import androidx.recyclerview.widget.LinearLayoutManager
//////import androidx.recyclerview.widget.RecyclerView
//////import com.example.parttracker.R
//////import com.example.parttracker.model.DashboardRow
//////import com.example.parttracker.model.PlanEntry
//////import com.example.parttracker.ui.adapter.PlanAdapter
//////import com.example.parttracker.viewmodel.PlanViewModel
//////import com.google.android.material.floatingactionbutton.FloatingActionButton
//////
//////class PlanFragment : Fragment() {
//////
//////    private val planViewModel: PlanViewModel by viewModels()
//////
//////    @RequiresApi(Build.VERSION_CODES.O)
//////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
//////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
//////
//////        // Create adapter using DashboardRow
//////        val adapter = PlanAdapter(emptyList()) { dashboardRow ->
//////            val action = PlanFragmentDirections.actionPlanFragmentToDashboardFragment(
//////                selectedDate = dashboardRow.date,
//////                selectedShift = dashboardRow.shift
//////            )
//////            findNavController().navigate(action)
//////        }
//////
//////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//////        recyclerView.adapter = adapter
//////
//////        // Observe dashboard rows and update RecyclerView
//////        planViewModel.allDashboardRows.observe(viewLifecycleOwner, Observer { dashboardList ->
//////            adapter.updateData(dashboardList)
//////        })
//////
//////        fabAdd.setOnClickListener {
//////            showAddPlanDialog()
//////        }
//////    }
//////
//////    @RequiresApi(Build.VERSION_CODES.O)
//////    private fun showAddPlanDialog() {
//////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
//////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
//////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
//////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
//////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
//////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
//////
//////        val sequences = listOf(1, 2, 3, 4)
//////        val modelMap = mapOf(
//////            1 to "GA-3501",
//////            2 to "LA-3502",
//////            3 to "DA-3503",
//////            4 to "DA-3001"
//////        )
//////
//////        val seqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences)
//////        etSequence.setAdapter(seqAdapter)
//////
//////        val modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelMap.values.toList())
//////        etModel.setAdapter(modelAdapter)
//////
//////        etSequence.setOnItemClickListener { _, _, position, _ ->
//////            val selectedSeq = sequences[position]
//////            etModel.setText(modelMap[selectedSeq] ?: "")
//////        }
//////
//////        AlertDialog.Builder(requireContext())
//////            .setView(dialogView)
//////            .setPositiveButton("Add") { _, _ ->
//////                try {
//////                    val entry = PlanEntry(
//////                        date = etDate.text.toString(),
//////                        shift = etShift.text.toString(),
//////                        sequence = etSequence.text.toString().toInt(),
//////                        model = etModel.text.toString(),
//////                        quantity = etQuantity.text.toString().toInt()
//////                    )
//////                    planViewModel.addPlanAndUpdateDashboard(entry)
//////                    Toast.makeText(requireContext(), "Plan added successfully!", Toast.LENGTH_SHORT).show()
//////                } catch (e: Exception) {
//////                    Toast.makeText(requireContext(), "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
//////                }
//////            }
//////            .setNegativeButton("Cancel", null)
//////            .create()
//////            .show()
//////    }
//////
//////    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//////        return inflater.inflate(R.layout.fragment_plan, container, false)
//////    }
//////}
//////
////
////
////package com.example.parttracker.ui
////
////import android.os.Build
////import android.os.Bundle
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.*
////import androidx.annotation.RequiresApi
////import androidx.appcompat.app.AlertDialog
////import androidx.fragment.app.Fragment
////import androidx.fragment.app.viewModels
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.example.parttracker.R
////import com.example.parttracker.model.PlanEntry
////import com.example.parttracker.ui.adapter.PlanAdapter
////import com.example.parttracker.viewmodel.PlanViewModel
////import com.example.parttracker.viewmodel.DashboardViewModel
////import com.example.parttracker.viewmodel.DashboardViewModelFactory
////import com.example.parttracker.viewmodel.PlanViewModelFactory
////import com.google.android.material.floatingactionbutton.FloatingActionButton
////import java.time.LocalDate
////import java.time.LocalTime
////
////class PlanFragment : Fragment() {
////
////    private val planViewModel: PlanViewModel by viewModels {
////        PlanViewModelFactory(requireContext())
////    }
////
////    private val dashboardViewModel: DashboardViewModel by viewModels {
////        DashboardViewModelFactory(requireContext())
////    }
////
////    private lateinit var adapter: PlanAdapter
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
////
////        adapter = PlanAdapter(emptyList())
////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////        recyclerView.adapter = adapter
////
////        planViewModel.plans.observe(viewLifecycleOwner) { planList ->
////            adapter.updateData(planList)
////        }
////
////        // Load today's data
////        val (date, shift) = getCurrentDateAndShift()
////        planViewModel.loadPlansFor(date, shift)
////
////        fabAdd.setOnClickListener {
////            showAddPlanDialog()
////        }
////    }
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun showAddPlanDialog() {
////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
////
////        val (date, shift) = getCurrentDateAndShift()
////        etDate.setText(date)
////        etShift.setText(shift)
////
////        val sequences = listOf(1, 2, 3, 4)
////        val modelMap = mapOf(
////            1 to "GA-3501",
////            2 to "LA-3502",
////            3 to "DA-3503",
////            4 to "DA-3001"
////        )
////
////        val seqAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences)
////        etSequence.setAdapter(seqAdapter)
////
////        val modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelMap.values.toList())
////        etModel.setAdapter(modelAdapter)
////
////        etSequence.setOnItemClickListener { _, _, position, _ ->
////            val selectedSeq = sequences[position]
////            etModel.setText(modelMap[selectedSeq] ?: "")
////        }
////
////        AlertDialog.Builder(requireContext())
////            .setView(dialogView)
////            .setPositiveButton("Add") { _, _ ->
////                try {
////                    val entry = PlanEntry(
////                        date = etDate.text.toString(),
////                        shift = etShift.text.toString(),
////                        sequence = etSequence.text.toString().toInt(),
////                        model = etModel.text.toString(),
////                        quantity = etQuantity.text.toString().toInt()
////                    )
////                    planViewModel.insertPlan(entry)
////                    dashboardViewModel.loadDashboardData()
////                    Toast.makeText(requireContext(), "Plan added successfully!", Toast.LENGTH_SHORT).show()
////                } catch (e: Exception) {
////                    Toast.makeText(requireContext(), "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
////                }
////            }
////            .setNegativeButton("Cancel", null)
////            .create()
////            .show()
////    }
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun getCurrentDateAndShift(): Pair<String, String> {
////        val now = LocalTime.now()
////        val today = LocalDate.now()
////        return when {
////            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
////            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
////            else -> today.minusDays(1).toString() to "B"
////        }
////    }
////
////    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
////        return inflater.inflate(R.layout.fragment_plan, container, false)
////    }
////}
////
////package com.example.parttracker.ui
////
////import android.os.Build
////import android.os.Bundle
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.*
////import androidx.annotation.RequiresApi
////import androidx.appcompat.app.AlertDialog
////import androidx.fragment.app.Fragment
////import androidx.fragment.app.viewModels
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.example.parttracker.R
////import com.example.parttracker.model.PlanEntry
////import com.example.parttracker.ui.adapter.PlanAdapter
////import com.example.parttracker.viewmodel.DashboardViewModel
////import com.example.parttracker.viewmodel.DashboardViewModelFactory
////import com.example.parttracker.viewmodel.PlanViewModel
////import com.example.parttracker.viewmodel.PlanViewModelFactory
////import com.google.android.material.floatingactionbutton.FloatingActionButton
////import java.time.LocalDate
////import java.time.LocalTime
////
////class PlanFragment : Fragment() {
////
////    private val planViewModel: PlanViewModel by viewModels {
////        PlanViewModelFactory(requireContext())
////    }
////
////    private val dashboardViewModel: DashboardViewModel by viewModels {
////        DashboardViewModelFactory(requireContext())
////    }
////
////    private lateinit var adapter: PlanAdapter
////    private lateinit var selectedDate: String
////    private lateinit var selectedShift: String
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
////
////        adapter = PlanAdapter(emptyList())
////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////        recyclerView.adapter = adapter
////
////        // Determine current date and shift
////        val (date, shift) = getCurrentDateAndShift()
////        selectedDate = date
////        selectedShift = shift
////
////        // Observe LiveData from ViewModel
//////        planViewModel.getPlansFor(date, shift).observe(viewLifecycleOwner) { planList ->
//////            adapter.updateData(planList)
//////        }
//////
////        fabAdd.setOnClickListener {
////            showAddPlanDialog()
////        }
////
////
////        planViewModel.plans.observe(viewLifecycleOwner) { planList ->
////            adapter.updateData(planList)
////        }
////
////
////
////
////
////    }
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun showAddPlanDialog() {
////        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
////        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
////        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
////        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
////        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
////
////        etDate.setText(selectedDate)
////        etShift.setText(selectedShift)
////
////        val sequences = listOf(1, 2, 3, 4)
////        val modelMap = mapOf(
////            1 to "GA-3501",
////            2 to "LA-3502",
////            3 to "DA-3503",
////            4 to "DA-3001"
////        )
////
////        etSequence.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences))
////        etModel.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelMap.values.toList()))
////
////        etSequence.setOnItemClickListener { _, _, position, _ ->
////            val selectedSeq = sequences[position]
////            etModel.setText(modelMap[selectedSeq] ?: "")
////        }
////
////        AlertDialog.Builder(requireContext())
////            .setView(dialogView)
////            .setPositiveButton("Add") { _, _ ->
////                try {
////                    val entry = PlanEntry(
////                        date = etDate.text.toString(),
////                        shift = etShift.text.toString(),
////                        sequence = etSequence.text.toString().toInt(),
////                        model = etModel.text.toString(),
////                        quantity = etQuantity.text.toString().toInt()
////                    )
////                    planViewModel.insertPlan(entry)
////                    dashboardViewModel.loadDashboardData()
////                    Toast.makeText(requireContext(), "Plan added successfully!", Toast.LENGTH_SHORT).show()
////                } catch (e: Exception) {
////                    Toast.makeText(requireContext(), "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
////                }
////            }
////            .setNegativeButton("Cancel", null)
////            .create()
////            .show()
////    }
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun getCurrentDateAndShift(): Pair<String, String> {
////        val now = LocalTime.now()
////        val today = LocalDate.now()
////        return when {
////            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
////            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
////            else -> today.minusDays(1).toString() to "B"
////        }
////    }
////
////    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
////        return inflater.inflate(R.layout.fragment_plan, container, false)
////    }
////}
//
//
//package com.example.parttracker.ui
//
//import android.os.Build
//import android.os.Bundle
//import android.view.*
//import android.widget.*
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AlertDialog
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.parttracker.R
//import com.example.parttracker.model.PlanEntry
//import com.example.parttracker.ui.adapter.PlanAdapter
//import com.example.parttracker.util.modelColorMap
//import com.example.parttracker.viewmodel.DashboardViewModel
//import com.example.parttracker.viewmodel.DashboardViewModelFactory
//import com.example.parttracker.viewmodel.PlanViewModel
//import com.example.parttracker.viewmodel.PlanViewModelFactory
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import kotlinx.coroutines.launch
//import java.time.LocalDate
//import java.time.LocalTime
//
//class PlanFragment : Fragment() {
//
//    private val planViewModel: PlanViewModel by viewModels {
//        PlanViewModelFactory(requireContext())
//    }
//
//    private val dashboardViewModel: DashboardViewModel by viewModels {
//        DashboardViewModelFactory(requireContext())
//    }
//
//    private lateinit var adapter: PlanAdapter
//    private lateinit var selectedDate: String
//    private lateinit var selectedShift: String
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
//        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)
//
//
//        adapter = PlanAdapter(emptyList()) { planToDelete ->
//            AlertDialog.Builder(requireContext())
//                .setTitle("Delete Plan")
//                .setMessage("Delete plan for ${planToDelete.model} (Shift ${planToDelete.shift})?")
//                .setPositiveButton("Delete") { _, _ ->
//                    lifecycleScope.launch {
//                        planViewModel.deletePlan(planToDelete)
//                        dashboardViewModel.loadDashboardData(requireContext())
//                        Toast.makeText(requireContext(), "Plan deleted", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                .setNegativeButton("Cancel", null)
//                .show()
//        }
//
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//
//        val (date, shift) = getCurrentDateAndShift()
//        selectedDate = date
//        selectedShift = shift
//
//        planViewModel.plans.observe(viewLifecycleOwner) { planList ->
//            adapter.updateData(planList)
//        }
//
//        fabAdd.setOnClickListener {
//            showAddPlanDialog()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun showAddPlanDialog() {
//        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
//        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
//        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
//        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
//        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
//        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
//        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)
//
//        etDate.setText(selectedDate)
//        etShift.setText(selectedShift)
//
//        val sequences = listOf(1, 2, 3, 4)
//        val models = listOf("GA-3501", "LA-3502", "DA-3503", "DA-3001")
//
//        etSequence.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences))
//        etModel.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, models))
//
//        val colorAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
//        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerColor.adapter = colorAdapter
//
//        etModel.setOnItemClickListener { _, _, _, _ ->
//            val selectedModel = etModel.text.toString().trim()
//            val colors = modelColorMap[selectedModel] ?: emptyList()
//            colorAdapter.clear()
//            colorAdapter.addAll(colors)
//            colorAdapter.notifyDataSetChanged()
//        }
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Add Plan")
//            .setView(dialogView)
//            .setPositiveButton("Add") { _, _ ->
//                try {
//                    val entry = PlanEntry(
//                        date = etDate.text.toString(),
//                        shift = etShift.text.toString(),
//                        sequence = etSequence.text.toString().toInt(),
//                        model = etModel.text.toString(),
//                        quantity = etQuantity.text.toString().toInt(),
//                        color = spinnerColor.selectedItem.toString()
//                    )
//                    planViewModel.insertPlanAndSync(entry, requireContext())
//
//                    dashboardViewModel.loadDashboardData(requireContext())
//                    Toast.makeText(requireContext(), "Plan added successfully!", Toast.LENGTH_SHORT).show()
//                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .create()
//            .show()
//    }
//
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun getCurrentDateAndShift(): Pair<String, String> {
//        val now = LocalTime.now()
//        val today = LocalDate.now()
//        return when {
//            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
//            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
//            else -> today.minusDays(1).toString() to "B"
//        }
//    }
//
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        return inflater.inflate(R.layout.fragment_plan, container, false)
//    }
//}
//


package com.example.parttracker.ui

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.PlanEntry
import com.example.parttracker.ui.adapter.PlanAdapter
import com.example.parttracker.util.modelColorMap
import com.example.parttracker.viewmodel.DashboardViewModel
import com.example.parttracker.viewmodel.DashboardViewModelFactory
import com.example.parttracker.viewmodel.PlanViewModel
import com.example.parttracker.viewmodel.PlanViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class PlanFragment : Fragment() {

    private val planViewModel: PlanViewModel by viewModels {
        PlanViewModelFactory(requireContext())
    }

    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(requireContext())
    }

    private lateinit var adapter: PlanAdapter
    private lateinit var selectedDate: String
    private lateinit var selectedShift: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sharedPref = requireContext().getSharedPreferences("UserPrefs", 0)
        val userRole = sharedPref.getString("userRole", "") ?: ""

        if (userRole != "Admin" && userRole != "Plan")
            {
            Toast.makeText(requireContext(), "Access denied: Plan access restricted.", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }


        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlan)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddPlan)

        adapter = PlanAdapter(emptyList()) { planToDelete ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Plan")
                .setMessage("Delete plan for ${planToDelete.model} (Shift ${planToDelete.shift})?")
                .setPositiveButton("Delete") { _, _ ->
                    lifecycleScope.launch {
                        planViewModel.deletePlan(planToDelete)
                        dashboardViewModel.loadDashboardData(requireContext())
                        Toast.makeText(requireContext(), "Plan deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val (date, shift) = getCurrentDateAndShift()
        selectedDate = date
        selectedShift = shift

        // Observe only today's date + shift to avoid repeating data
        planViewModel.getPlansForDateShift(date, shift).observe(viewLifecycleOwner) { planList ->
            adapter.updateData(planList)
        }

        fabAdd.setOnClickListener {
            showAddPlanDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddPlanDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val etShift = dialogView.findViewById<EditText>(R.id.etShift)
        val etSequence = dialogView.findViewById<AutoCompleteTextView>(R.id.etSequence)
        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)

        etDate.setText(selectedDate)
        etShift.setText(selectedShift)

        val sequences = listOf(1, 2, 3, 4)
        val models = modelColorMap.keys.toList()

        etSequence.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sequences))
        etModel.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, models))

        val colorAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = colorAdapter

        etModel.setOnItemClickListener { _, _, _, _ ->
            val selectedModel = etModel.text.toString().trim()
            val colors = modelColorMap[selectedModel] ?: emptyList()
            colorAdapter.clear()
            colorAdapter.addAll(colors)
            colorAdapter.notifyDataSetChanged()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Plan")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                try {
                    val entry = PlanEntry(
                        sequence = etSequence.text.toString().toInt(),
                        model = etModel.text.toString().trim(),
                        quantity = etQuantity.text.toString().toInt(),
                        date = etDate.text.toString().trim(),
                        shift = etShift.text.toString().trim(),
                        color = spinnerColor.selectedItem.toString().trim()
                    )

                    lifecycleScope.launch {
                        planViewModel.insertPlanAndSync(entry, requireContext())
                        dashboardViewModel.loadDashboardData(requireContext())
                        Toast.makeText(requireContext(), "Plan added successfully!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateAndShift(): Pair<String, String> {
        val now = LocalTime.now()
        val today = LocalDate.now()
        return when {
            now >= LocalTime.of(6, 30) && now < LocalTime.of(15, 30) -> today.toString() to "A"
            now >= LocalTime.of(15, 30) || now < LocalTime.of(0, 30) -> today.toString() to "B"
            else -> today.minusDays(1).toString() to "B"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }
}

