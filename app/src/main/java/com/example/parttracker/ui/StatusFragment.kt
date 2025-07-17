package com.example.parttracker.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.ui.adapter.ScannedPartAdapter
import com.example.parttracker.ui.adapter.UsedPartAdapter
import com.example.parttracker.viewmodel.PartViewModel
import kotlinx.coroutines.launch

class StatusFragment : Fragment() {

    private lateinit var partViewModel: PartViewModel
    private lateinit var scannedRecyclerView: RecyclerView
    private lateinit var usedRecyclerView: RecyclerView

    private lateinit var spinnerPartName: Spinner
    private lateinit var etQuantity: EditText
    private lateinit var btnMarkUsed: Button

    private lateinit var tvDispatchTitle: TextView
    private lateinit var tvStockTitle: TextView
    private lateinit var tvUsedTitle: TextView

    private lateinit var tvDispatchDynamic: TextView
    private lateinit var tvStockDynamic: TextView
    private lateinit var tvUsedDynamic: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_status, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        partViewModel = ViewModelProvider(this)[PartViewModel::class.java]

        // RecyclerViews
        scannedRecyclerView = view.findViewById(R.id.recyclerViewScannedParts)
        usedRecyclerView = view.findViewById(R.id.recyclerViewUsedParts)

        scannedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        usedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Input fields
        spinnerPartName = view.findViewById(R.id.spinnerPartName)
        etQuantity = view.findViewById(R.id.etQuantity)
        btnMarkUsed = view.findViewById(R.id.btnMarkUsed)

        // Populate Spinner with distinct part names
        partViewModel.getDistinctPartNamesFromCTL().observe(viewLifecycleOwner) { partNames ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, partNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPartName.adapter = adapter
        }

        // Static Titles
        tvDispatchTitle = view.findViewById(R.id.tvDispatched)
        tvStockTitle = view.findViewById(R.id.tvStock)
        tvUsedTitle = view.findViewById(R.id.tvUsed)

        // Dynamic Summary Holders
        tvDispatchDynamic = view.findViewById(R.id.tvDispatchBreakdown)
        tvStockDynamic = view.findViewById(R.id.tvStockBreakdown)
        tvUsedDynamic = view.findViewById(R.id.tvUsedBreakdown)

        // Load Scanned Parts Log
        lifecycleScope.launch {
            try {
                val scannedParts = partViewModel.getAllParts()
                scannedRecyclerView.adapter = ScannedPartAdapter(scannedParts)
            } catch (e: Exception) {
                Log.e("StatusFragment", "Error loading scanned parts", e)
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe per-part grouped counts
        partViewModel.getGroupedPartCountsByLocation("Paintshop")
            .observe(viewLifecycleOwner) { list ->
                tvDispatchDynamic.text = list.joinToString("\n") { "${it.partName}: ${it.count}" }
            }

        partViewModel.getGroupedPartCountsByLocation("CTL")
            .observe(viewLifecycleOwner) { list ->
                tvStockDynamic.text = list.joinToString("\n") { "${it.partName}: ${it.count}" }
            }

        partViewModel.usedCountByPart.observe(viewLifecycleOwner) { usedList ->
            usedRecyclerView.adapter = UsedPartAdapter(usedList)
            tvUsedDynamic.text = usedList.joinToString("\n") { "${it.partName}: ${it.count}" }
        }

        // Handle button
        btnMarkUsed.setOnClickListener {
            val selectedPartName = spinnerPartName.selectedItem?.toString()?.trim()
            val quantity = etQuantity.text.toString().trim().toIntOrNull()

            if (selectedPartName.isNullOrEmpty() || quantity == null || quantity <= 0) {
                Toast.makeText(
                    requireContext(),
                    "Select a valid part and enter valid quantity",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    partViewModel.markPartAsUsed(selectedPartName, quantity, requireContext())
                    Toast.makeText(
                        requireContext(),
                        "$quantity $selectedPartName marked as used",
                        Toast.LENGTH_SHORT
                    ).show()
                    etQuantity.text.clear()
                } catch (e: Exception) {
                    Log.e("StatusFragment", "Mark used error", e)
                    Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnDeleteAll = view.findViewById<Button>(R.id.btnDeleteAll)
        btnDeleteAll.setOnClickListener {
            lifecycleScope.launch {
                partViewModel.clearAllScannedParts()
                partViewModel.clearAllUsedParts()
                Toast.makeText(
                    requireContext(),
                    "All scanned and used parts deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
