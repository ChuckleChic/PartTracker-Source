//////package com.example.parttracker.ui
//////
//////import android.app.Application
//////import android.os.Build
//////import android.os.Bundle
//////import android.util.Log
//////import android.view.*
//////import android.widget.*
//////import androidx.annotation.RequiresApi
//////import androidx.appcompat.app.AlertDialog
//////import androidx.fragment.app.Fragment
//////import androidx.fragment.app.viewModels
//////import androidx.lifecycle.lifecycleScope
//////import androidx.recyclerview.widget.LinearLayoutManager
//////import androidx.recyclerview.widget.RecyclerView
//////import com.example.parttracker.R
//////import com.example.parttracker.model.DashboardRow
//////import com.example.parttracker.ui.adapter.SequenceGroupAdapter
//////import com.example.parttracker.viewmodel.DashboardViewModel
//////import com.example.parttracker.viewmodel.DashboardViewModelFactory
//////import com.google.android.material.floatingactionbutton.FloatingActionButton
//////import kotlinx.coroutines.launch
//////
//////class DashboardFragment : Fragment() {
//////
//////    private lateinit var adapter: SequenceGroupAdapter
//////
//////    private val viewModel: DashboardViewModel by viewModels {
//////        DashboardViewModelFactory(requireContext().applicationContext as Application)
//////    }
//////
//////    override fun onCreateView(
//////        inflater: LayoutInflater,
//////        container: ViewGroup?,
//////        savedInstanceState: Bundle?
//////    ): View {
//////        return inflater.inflate(R.layout.fragment_dashboard, container, false)
//////    }
//////
//////    @RequiresApi(Build.VERSION_CODES.O)
//////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//////        super.onViewCreated(view, savedInstanceState)
//////
//////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvDashboard)
//////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAdd)
//////
//////        val tvTotalPlanned = view.findViewById<TextView>(R.id.tvTotalPlanned)
//////        val tvTotalProduced = view.findViewById<TextView>(R.id.tvTotalProduced)
//////        val tvTotalCb = view.findViewById<TextView>(R.id.tvTotalCb)
//////
//////        adapter = SequenceGroupAdapter(emptyList()) { dashboardRow ->
//////            showEditObDialog(dashboardRow)
//////        }
//////
//////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//////        recyclerView.adapter = adapter
//////
//////        viewLifecycleOwner.lifecycleScope.launch {
//////            viewModel.matchUnmatchedScannedPartsThenLoadDashboard(requireContext())
//////        }
//////
//////        viewModel.sequenceGroups.observe(viewLifecycleOwner) { groups ->
//////            adapter.updateData(groups)
//////
//////            val allRows = groups.flatMap { group ->
//////                group.colorGroups.flatMap { it.rows }
//////            }
//////
//////            val totalPlanned = allRows
//////                .groupBy { it.model to it.color }
//////                .mapValues { entry -> entry.value.maxOfOrNull { it.planned } ?: 0 }
//////                .values.sum()
//////
//////            val totalProduced = allRows
//////                .groupBy { it.model to it.color }
//////                .mapValues { entry -> entry.value.firstOrNull()?.produced ?: 0 }
//////                .values.sum()
//////
//////            val totalCb = allRows
//////                .groupBy { it.model to it.color }
//////                .mapValues { entry -> entry.value.minOfOrNull { it.cb } ?: 0 }
//////                .values.sum()
//////
//////
//////
//////
//////            tvTotalPlanned.text = totalPlanned.toString()
//////            tvTotalProduced.text = totalProduced.toString()
//////            tvTotalCb.text = totalCb.toString()
//////        }
//////
//////        fabAdd.setOnClickListener {
//////            showModelProductionDialog()
//////        }
//////    }
//////
//////    @RequiresApi(Build.VERSION_CODES.O)
//////    private fun showModelProductionDialog() {
//////        val dialogView = LayoutInflater.from(requireContext())
//////            .inflate(R.layout.dialog_model_production, null)
//////
//////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
//////        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)
//////        //val etOB = dialogView.findViewById<EditText>(R.id.etOB)
//////        val etProduced = dialogView.findViewById<EditText>(R.id.etProduced)
//////        val etRejection = dialogView.findViewById<EditText>(R.id.etRejection)
//////
//////        val modelToColors = mapOf(
//////            "GA-3501" to listOf(
//////                "Bro. Black",
//////                "Indigo Blue",
//////                "Hazelnut",
//////                "Pista Green",
//////                "Scarlet Red"
//////            ),
//////            "LA-3502" to listOf("Bro. Black", "Indigo Blue", "Corse Gray", "Classic White"),
//////            "DA-3503" to listOf("Bro. Black", "Indigo Blue", "Corse Gray", "Classic White"),
//////            "DA-3001" to listOf(
//////                "Bro. Black",
//////                "Azure Blue",
//////                "Racing Red",
//////                "Cyber White",
//////                "Lime Yellow"
//////            )
//////        )
//////
//////        val models = modelToColors.keys.toList()
//////        etModel.setAdapter(
//////            ArrayAdapter(
//////                requireContext(),
//////                android.R.layout.simple_dropdown_item_1line,
//////                models
//////            )
//////        )
//////
//////        etModel.setOnItemClickListener { _, _, _, _ ->
//////            val selectedModel = etModel.text.toString().trim()
//////            val colors = modelToColors[selectedModel] ?: emptyList()
//////            val colorAdapter = ArrayAdapter(
//////                requireContext(),
//////                android.R.layout.simple_spinner_dropdown_item,
//////                colors
//////            )
//////            spinnerColor.adapter = colorAdapter
//////        }
//////
//////        etModel.setOnDismissListener {
//////            val selectedModel = etModel.text.toString().trim()
//////            val colors = modelToColors[selectedModel] ?: emptyList()
//////            val adapter = ArrayAdapter(
//////                requireContext(),
//////                android.R.layout.simple_spinner_dropdown_item,
//////                colors
//////            )
//////            spinnerColor.adapter = adapter
//////        }
//////
//////        AlertDialog.Builder(requireContext())
//////            .setTitle("Enter Model Production")
//////            .setView(dialogView)
//////            .setPositiveButton("Save") { _, _ ->
//////                try {
//////                    val model = etModel.text.toString().trim()
//////                    val color = spinnerColor.selectedItem?.toString()?.trim() ?: ""
//////                    //val obText = etOB.text.toString().trim()
//////                    //val ob = if (obText.isNotEmpty()) obText.toInt() else 0
//////                    val produced = etProduced.text.toString().toInt()
//////                    val rejection = etRejection.text.toString().toInt()
//////
//////                    viewModel.saveModelProduction(
//////                        model = model,
//////                        color = color,
//////                        //openingBalance = ob,
//////                        produced = produced,
//////                        rejection = rejection,
//////                        context = requireContext()
//////                    )
//////
//////                    Toast.makeText(
//////                        requireContext(),
//////                        "Production saved for $model ($color)",
//////                        Toast.LENGTH_SHORT
//////                    ).show()
//////                } catch (e: Exception) {
//////                    Toast.makeText(
//////                        requireContext(),
//////                        "Invalid input: ${e.message}",
//////                        Toast.LENGTH_SHORT
//////                    ).show()
//////                }
//////            }
//////            .setNegativeButton("Cancel", null)
//////            .create()
//////            .show()
//////    }
//////
//////    // ✅ Updated to take DashboardRow instead of RowData
//////    @RequiresApi(Build.VERSION_CODES.O)
//////    private fun showEditObDialog(partRow: DashboardRow) {
//////        val dialogView = LayoutInflater.from(requireContext())
//////            .inflate(R.layout.dialog_edit_ob, null)
//////
//////        val etNewOb = dialogView.findViewById<EditText>(R.id.etNewOb)
//////        etNewOb.setText(partRow.ob.toString())
//////
//////        AlertDialog.Builder(requireContext())
//////            .setTitle("Edit Opening Balance for ${partRow.partName}")
//////            .setView(dialogView)
//////            .setPositiveButton("Save") { _, _ ->
//////                try {
//////                    val newObText = etNewOb.text.toString().trim()
//////                    val newOb = if (newObText.isNotEmpty()) newObText.toInt() else 0
//////
//////                    val updatedRow = partRow.copy(ob = newOb)
//////                    viewModel.updateDashboardRow(updatedRow, requireContext())
//////
//////                    Toast.makeText(
//////                        requireContext(),
//////                        "OB updated for ${partRow.partName}",
//////                        Toast.LENGTH_SHORT
//////                    ).show()
//////
//////                } catch (e: Exception) {
//////                    Toast.makeText(
//////                        requireContext(),
//////                        "Invalid OB: ${e.message}",
//////                        Toast.LENGTH_SHORT
//////                    ).show()
//////                }
//////            }
//////            .setNegativeButton("Cancel", null)
//////            .create()
//////            .show()
//////    }
//////}
////
////
////package com.example.parttracker.ui
////
////import android.app.Application
////import android.os.Build
////import android.os.Bundle
////import android.view.*
////import android.widget.*
////import androidx.annotation.RequiresApi
////import androidx.appcompat.app.AlertDialog
////import androidx.fragment.app.Fragment
////import androidx.fragment.app.viewModels
////import androidx.lifecycle.lifecycleScope
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.example.parttracker.R
////import com.example.parttracker.model.DashboardRow
////import com.example.parttracker.ui.adapter.SequenceGroupAdapter
////import com.example.parttracker.viewmodel.DashboardViewModel
////import com.example.parttracker.viewmodel.DashboardViewModelFactory
////import com.google.android.material.floatingactionbutton.FloatingActionButton
////import kotlinx.coroutines.launch
////
////class DashboardFragment : Fragment() {
////
////    private lateinit var adapter: SequenceGroupAdapter
////
////    private val viewModel: DashboardViewModel by viewModels {
////        DashboardViewModelFactory(requireContext().applicationContext as Application)
////    }
////
////    override fun onCreateView(
////        inflater: LayoutInflater,
////        container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////        return inflater.inflate(R.layout.fragment_dashboard, container, false)
////    }
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////        val recyclerView = view.findViewById<RecyclerView>(R.id.rvDashboard)
////        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAdd)
////
////        val tvTotalPlanned = view.findViewById<TextView>(R.id.tvTotalPlanned)
////        val tvTotalProduced = view.findViewById<TextView>(R.id.tvTotalProduced)
////        val tvTotalCb = view.findViewById<TextView>(R.id.tvTotalCb)
////
////        adapter = SequenceGroupAdapter(emptyList()) { dashboardRow ->
////            showEditObDialog(dashboardRow)
////        }
////
////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////        recyclerView.adapter = adapter
////
////        viewLifecycleOwner.lifecycleScope.launch {
////            viewModel.matchUnmatchedScannedPartsThenLoadDashboard(requireContext())
////            viewModel.startListeningForDashboardChanges(requireContext())
////            viewModel.startListeningForScannedParts(requireContext())
////            viewModel.startListeningForUsedParts(requireContext())
////            viewModel.startListeningForPlans(requireContext())
////            viewModel.startListeningForModelProduction(requireContext())
////            viewModel.syncFirestoreDataIfEmpty(requireContext())
////
////
////        }
////
////        viewModel.sequenceGroups.observe(viewLifecycleOwner) { groups ->
////            adapter.updateData(groups)
////
////            val allRows = groups.flatMap { group ->
////                group.colorGroups.flatMap { it.rows }
////            }
////
////            val totalPlanned = allRows
////                .groupBy { it.model to it.color }
////                .mapValues { entry -> entry.value.maxOfOrNull { it.planned } ?: 0 }
////                .values.sum()
////
////            val totalProduced = allRows
////                .groupBy { it.model to it.color }
////                .mapValues { entry -> entry.value.firstOrNull()?.produced ?: 0 }
////                .values.sum()
////
////            val totalCb = allRows
////                .groupBy { it.model to it.color }
////                .mapValues { entry -> entry.value.minOfOrNull { it.cb } ?: 0 }
////                .values.sum()
////
////            tvTotalPlanned.text = totalPlanned.toString()
////            tvTotalProduced.text = totalProduced.toString()
////            tvTotalCb.text = totalCb.toString()
////
////            tvTotalCb.setOnClickListener {
////                if (viewModel.dashboardRows.value.isNullOrEmpty()) {
////                    Toast.makeText(requireContext(), "Loading data, please wait...", Toast.LENGTH_SHORT).show()
////                } else {
////                    CBInventoryBottomSheet().show(parentFragmentManager, "CBInventory")
////                }
////            }
////
////
////        }
////
////        fabAdd.setOnClickListener {
////            showModelProductionDialog()
////        }
////    }
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun showModelProductionDialog() {
////        val dialogView = LayoutInflater.from(requireContext())
////            .inflate(R.layout.dialog_model_production, null)
////
////        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
////        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)
////        val etProduced = dialogView.findViewById<EditText>(R.id.etProduced)
////        val etRejection = dialogView.findViewById<EditText>(R.id.etRejection)
////
////        val modelToColors = mapOf(
////            "GA-3501" to listOf("Bro. Black", "Indigo Blue", "Hazelnut", "Pista Green", "Scarlet Red"),
////            "LA-3502" to listOf("Bro. Black", "Indigo Blue", "Corse Gray", "Classic White"),
////            "DA-3503" to listOf("Bro. Black", "Indigo Blue", "Corse Gray", "Classic White"),
////            "DA-3001" to listOf("Bro. Black", "Azure Blue", "Racing Red", "Cyber White", "Lime Yellow")
////        )
////
////        val models = modelToColors.keys.toList()
////        etModel.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, models))
////
////        etModel.setOnItemClickListener { _, _, _, _ ->
////            val selectedModel = etModel.text.toString().trim()
////            val colors = modelToColors[selectedModel] ?: emptyList()
////            spinnerColor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, colors)
////        }
////
////        AlertDialog.Builder(requireContext())
////            .setTitle("Enter Model Production")
////            .setView(dialogView)
////            .setPositiveButton("Save") { _, _ ->
////                try {
////                    val model = etModel.text.toString().trim()
////                    val color = spinnerColor.selectedItem?.toString()?.trim() ?: ""
////                    val produced = etProduced.text.toString().toInt()
////                    val rejection = etRejection.text.toString().toInt()
////
////                    viewModel.saveModelProduction(
////                        model = model,
////                        color = color,
////                        produced = produced,
////                        rejection = rejection,
////                        context = requireContext()
////                    )
////
////                    Toast.makeText(requireContext(), "Production saved for $model ($color)", Toast.LENGTH_SHORT).show()
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
////    private fun showEditObDialog(partRow: DashboardRow) {
////        val dialogView = LayoutInflater.from(requireContext())
////            .inflate(R.layout.dialog_edit_ob, null)
////
////        val etNewOb = dialogView.findViewById<EditText>(R.id.etNewOb)
////        etNewOb.setText(partRow.ob.toString())
////
////        AlertDialog.Builder(requireContext())
////            .setTitle("Edit Opening Balance for ${partRow.partName}")
////            .setView(dialogView)
////            .setPositiveButton("Save") { _, _ ->
////                try {
////                    val newObText = etNewOb.text.toString().trim()
////                    val newOb = if (newObText.isNotEmpty()) newObText.toInt() else partRow.ob
////
////                    val updatedRow = partRow.copy(ob = newOb)
////                    viewModel.updateDashboardRow(updatedRow, requireContext())
////
////                    Toast.makeText(requireContext(), "OB updated for ${partRow.partName}", Toast.LENGTH_SHORT).show()
////                } catch (e: Exception) {
////                    Toast.makeText(requireContext(), "Invalid OB: ${e.message}", Toast.LENGTH_SHORT).show()
////                }
////            }
////            .setNegativeButton("Cancel", null)
////            .create()
////            .show()
////    }
////
////
////}
//
//package com.example.parttracker.ui
//
//import android.app.Application
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
//import com.example.parttracker.model.DashboardRow
//import com.example.parttracker.ui.adapter.SequenceGroupAdapter
//import com.example.parttracker.viewmodel.DashboardViewModel
//import com.example.parttracker.viewmodel.DashboardViewModelFactory
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import kotlinx.coroutines.launch
//
//class DashboardFragment : Fragment() {
//
//    private lateinit var adapter: SequenceGroupAdapter
//
//    private val viewModel: DashboardViewModel by viewModels {
//        DashboardViewModelFactory(requireContext().applicationContext as Application)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.fragment_dashboard, container, false)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val recyclerView = view.findViewById<RecyclerView>(R.id.rvDashboard)
//        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAdd)
//
//        val tvTotalPlanned = view.findViewById<TextView>(R.id.tvTotalPlanned)
//        val tvTotalProduced = view.findViewById<TextView>(R.id.tvTotalProduced)
//        val tvTotalCb = view.findViewById<TextView>(R.id.tvTotalCb)
//
//        adapter = SequenceGroupAdapter(emptyList()) { dashboardRow ->
//            showEditObDialog(dashboardRow)
//        }
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.matchUnmatchedScannedPartsThenLoadDashboard(requireContext())
//            viewModel.startListeningForDashboardChanges(requireContext())
//            viewModel.startListeningForScannedParts(requireContext())
//            viewModel.startListeningForUsedParts(requireContext())
//            viewModel.startListeningForPlans(requireContext())
//            viewModel.startListeningForModelProduction(requireContext())
//            viewModel.syncFirestoreDataIfEmpty(requireContext())
//        }
//
//        // 🔁 Observe grouped dashboard
//        viewModel.sequenceGroups.observe(viewLifecycleOwner) { groups ->
//            adapter.updateData(groups)
//
//            val allRows = groups.flatMap { group ->
//                group.colorGroups.flatMap { it.rows }
//            }
//
//            val totalPlanned = allRows
//                .groupBy { it.model to it.color }
//                .mapValues { entry -> entry.value.maxOfOrNull { it.planned } ?: 0 }
//                .values.sum()
//
//            val totalProduced = allRows
//                .groupBy { it.model to it.color }
//                .mapValues { entry -> entry.value.firstOrNull()?.produced ?: 0 }
//                .values.sum()
//
//            val totalCb = allRows
//                .groupBy { it.model to it.color }
//                .mapValues { entry -> entry.value.minOfOrNull { it.cb } ?: 0 }
//                .values.sum()
//
//            tvTotalPlanned.text = totalPlanned.toString()
//            tvTotalProduced.text = totalProduced.toString()
//            tvTotalCb.text = totalCb.toString()
//        }
//
//        // 📦 Show CB inventory
//        tvTotalCb.setOnClickListener {
//            if (viewModel.dashboardRows.value.isNullOrEmpty()) {
//                Toast.makeText(requireContext(), "Loading data, please wait...", Toast.LENGTH_SHORT).show()
//            } else {
//                CBInventoryBottomSheet().show(parentFragmentManager, "CBInventory")
//            }
//        }
//
//        fabAdd.setOnClickListener {
//            showModelProductionDialog()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun showModelProductionDialog() {
//        val dialogView = LayoutInflater.from(requireContext())
//            .inflate(R.layout.dialog_model_production, null)
//
//        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
//        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)
//        val etProduced = dialogView.findViewById<EditText>(R.id.etProduced)
//        val etRejection = dialogView.findViewById<EditText>(R.id.etRejection)
//
//        val modelToColors = mapOf(
//            "GA-3501" to listOf("Bro. Black", "Indigo Blue", "Hazelnut", "Pista Green", "Scarlet Red"),
//            "LA-3502" to listOf("Bro. Black", "Indigo Blue", "Corse Gray", "Classic White"),
//            "DA-3503" to listOf("Bro. Black", "Indigo Blue", "Corse Gray", "Classic White"),
//            "DA-3001" to listOf("Bro. Black", "Azure Blue", "Racing Red", "Cyber White", "Lime Yellow")
//        )
//
//        val models = modelToColors.keys.toList()
//        etModel.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, models))
//
//        etModel.setOnItemClickListener { _, _, _, _ ->
//            val selectedModel = etModel.text.toString().trim()
//            val colors = modelToColors[selectedModel] ?: emptyList()
//            spinnerColor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, colors)
//        }
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Enter Model Production")
//            .setView(dialogView)
//            .setPositiveButton("Save") { _, _ ->
//                try {
//                    val model = etModel.text.toString().trim()
//                    val color = spinnerColor.selectedItem?.toString()?.trim() ?: ""
//                    val produced = etProduced.text.toString().toInt()
//                    val rejection = etRejection.text.toString().toInt()
//
//                    viewModel.saveModelProduction(model, color, produced, rejection, requireContext())
//                    Toast.makeText(requireContext(), "Production saved for $model ($color)", Toast.LENGTH_SHORT).show()
//                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .create()
//            .show()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun showEditObDialog(partRow: DashboardRow) {
//        val dialogView = LayoutInflater.from(requireContext())
//            .inflate(R.layout.dialog_edit_ob, null)
//
//        val etNewOb = dialogView.findViewById<EditText>(R.id.etNewOb)
//        etNewOb.setText(partRow.ob.toString())
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Edit Opening Balance for ${partRow.partName}")
//            .setView(dialogView)
//            .setPositiveButton("Save") { _, _ ->
//                try {
//                    val newObText = etNewOb.text.toString().trim()
//                    val newOb = if (newObText.isNotEmpty()) newObText.toInt() else partRow.ob
//
//                    val updatedRow = partRow.copy(ob = newOb)
//                    viewModel.updateDashboardRow(updatedRow, requireContext())
//
//                    Toast.makeText(requireContext(), "OB updated for ${partRow.partName}", Toast.LENGTH_SHORT).show()
//                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), "Invalid OB: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .create()
//            .show()
//    }
//}
//


package com.example.parttracker.ui

import android.app.Application
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
import com.example.parttracker.model.DashboardRow
import com.example.parttracker.ui.adapter.SequenceGroupAdapter
import com.example.parttracker.viewmodel.DashboardViewModel
import com.example.parttracker.viewmodel.DashboardViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var adapter: SequenceGroupAdapter
    private lateinit var fabAdd: FloatingActionButton


    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(requireContext().applicationContext as Application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAdd = view.findViewById(R.id.fabAdd)


        val recyclerView = view.findViewById<RecyclerView>(R.id.rvDashboard)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        val tvTotalPlanned = view.findViewById<TextView>(R.id.tvTotalPlanned)
        val tvTotalProduced = view.findViewById<TextView>(R.id.tvTotalProduced)
        val tvTotalCb = view.findViewById<TextView>(R.id.tvTotalCb)

        adapter = SequenceGroupAdapter(emptyList()) { dashboardRow ->
            showEditObDialog(dashboardRow)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // ✅ ATTACH FIRESTORE LISTENERS DIRECTLY
        viewModel.startListeningForDashboardChanges(requireContext())
        viewModel.startListeningForScannedParts(requireContext())
        viewModel.startListeningForUsedParts(requireContext())
        viewModel.startListeningForPlans(requireContext())
        viewModel.startListeningForModelProduction(requireContext())

        // 🔁 Coroutine for initial sync and matching
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.matchUnmatchedScannedPartsThenLoadDashboard(requireContext())
            viewModel.syncFirestoreDataIfEmpty(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.syncFirestoreDataIfEmpty(requireContext())
            delay(500)
            viewModel.matchUnmatchedScannedPartsThenLoadDashboard(requireContext())

        }

        // 🔁 Observe grouped dashboard
        viewModel.sequenceGroups.observe(viewLifecycleOwner) { groups ->
            adapter.updateData(groups)

            val allRows = groups.flatMap { group ->
                group.colorGroups.flatMap { it.rows }
            }

            val totalPlanned = allRows
                .groupBy { it.model to it.color }
                .mapValues { entry -> entry.value.maxOfOrNull { it.planned } ?: 0 }
                .values.sum()

            val totalProduced = allRows
                .groupBy { it.model to it.color }
                .mapValues { entry -> entry.value.firstOrNull()?.produced ?: 0 }
                .values.sum()

            val totalCb = allRows
                .groupBy { it.model to it.color }
                .mapValues { entry -> entry.value.minOfOrNull { it.cb } ?: 0 }
                .values.sum()

            tvTotalPlanned.text = totalPlanned.toString()
            tvTotalProduced.text = totalProduced.toString()
            tvTotalCb.text = totalCb.toString()
        }

        // 📦 Show CB inventory
        tvTotalCb.setOnClickListener {
            if (viewModel.dashboardRows.value.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Loading data, please wait...", Toast.LENGTH_SHORT).show()
            } else {
                CBInventoryBottomSheet().show(parentFragmentManager, "CBInventory")
            }
        }

        fabAdd.setOnClickListener {
            showModelProductionDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showModelProductionDialog() {

        val sharedPref = requireContext().getSharedPreferences("UserPrefs", 0)
        val userRole = sharedPref.getString("userRole", "") ?: ""

        if (userRole != "Admin" && userRole != "Production") {
            fabAdd.visibility = View.GONE
        }


        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_model_production, null)

        val etModel = dialogView.findViewById<AutoCompleteTextView>(R.id.etModel)
        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)
        val etProduced = dialogView.findViewById<EditText>(R.id.etProduced)
        val etRejection = dialogView.findViewById<EditText>(R.id.etRejection)

        val modelToColors = mapOf(
            "GA-3501" to listOf("Bro. Black", "Indigo Blue", "Hazelnut", "Pista Green", "Scarlet Red"),
            "LA-3502" to listOf("Bro. Black", "Indigo Blue", "Corse Gray", "Classic White"),
            "DA-3503" to listOf("Bro. Black", "Indigo Blue", "Corse Gray", "Classic White"),
            "DA-3001" to listOf("Bro. Black", "Azure Blue", "Racing Red", "Cyber White", "Lime Yellow")
        )

        val models = modelToColors.keys.toList()
        etModel.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, models))

        etModel.setOnItemClickListener { _, _, _, _ ->
            val selectedModel = etModel.text.toString().trim()
            val colors = modelToColors[selectedModel] ?: emptyList()
            spinnerColor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, colors)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Enter Model Production")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                try {
                    val model = etModel.text.toString().trim()
                    val color = spinnerColor.selectedItem?.toString()?.trim() ?: ""
                    val produced = etProduced.text.toString().toInt()
                    val rejection = etRejection.text.toString().toInt()

                    viewModel.saveModelProduction(model, color, produced, rejection, requireContext())
                    Toast.makeText(requireContext(), "Production saved for $model ($color)", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showEditObDialog(partRow: DashboardRow) {

        val sharedPref = requireContext().getSharedPreferences("UserPrefs", 0)
        val userRole = sharedPref.getString("userRole", "") ?: ""

        if (userRole != "Admin") {
            Toast.makeText(requireContext(), "Only Admin can edit OB.", Toast.LENGTH_SHORT).show()
            return
        }


        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_ob, null)

        val etNewOb = dialogView.findViewById<EditText>(R.id.etNewOb)
        etNewOb.setText(partRow.ob.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Opening Balance for ${partRow.partName}")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                try {
                    val newObText = etNewOb.text.toString().trim()
                    val newOb = if (newObText.isNotEmpty()) newObText.toInt() else partRow.ob

                    val updatedRow = partRow.copy(ob = newOb)
                    viewModel.updateDashboardRow(updatedRow, requireContext())

                    Toast.makeText(requireContext(), "OB updated for ${partRow.partName}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Invalid OB: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}

