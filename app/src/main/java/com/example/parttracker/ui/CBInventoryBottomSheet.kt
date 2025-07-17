package com.example.parttracker.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parttracker.databinding.BottomSheetCbInventoryBinding
import com.example.parttracker.model.CBPartEntry
import com.example.parttracker.model.ColorGroup
import com.example.parttracker.model.ModelGroup
import com.example.parttracker.ui.adapter.ModelGroupAdapter
import com.example.parttracker.viewmodel.DashboardViewModel
import com.example.parttracker.viewmodel.DashboardViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CBInventoryBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCbInventoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private lateinit var adapter: ModelGroupAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCbInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewModel
        val factory = DashboardViewModelFactory(requireContext())
        viewModel = ViewModelProvider(requireActivity(), factory)[DashboardViewModel::class.java]

        // Setup RecyclerView
        adapter = ModelGroupAdapter()
        binding.rvCbInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCbInventory.adapter = adapter

        // Load data if empty
        if (viewModel.dashboardRows.value.isNullOrEmpty()) {
            viewModel.loadDashboardData(requireContext())
        }

        // Observe data and group
        viewModel.dashboardRows.observe(viewLifecycleOwner) { rows ->
            val groupedByModel = rows.groupBy { it.model }.map { (model, modelRows) ->
                val groupedByColor = modelRows.groupBy { it.color }.map { (color, colorRows) ->
                    val partEntries = colorRows.map { row ->
                        CBPartEntry(partName = row.partName, cb = row.cb)
                    }
                    ColorGroup(color = color, partsCB = partEntries)
                }
                ModelGroup(model = model, colorGroups = groupedByColor)
            }
            adapter.submitList(groupedByModel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
