package com.example.parttracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.parttracker.R
import com.example.parttracker.viewmodel.DashboardViewModel
import com.example.parttracker.viewmodel.DashboardViewModelFactory
import com.example.parttracker.ui.adapter.CBExpandableAdapter
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi

class CBInventoryFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(requireContext().applicationContext as Application)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cb_inventory, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val expandableListView = view.findViewById<ExpandableListView>(R.id.expandableListView)

        viewModel.dashboardRows.observe(viewLifecycleOwner) { allRows ->
            val grouped = allRows.groupBy { it.model }
            val data: Map<String, Map<String, Map<String, Int>>> = grouped.mapValues { modelEntry ->
                modelEntry.value.groupBy { it.color }.mapValues { colorEntry ->
                    colorEntry.value.associate { it.partName to it.cb }
                }
            }

            val adapter = CBExpandableAdapter(requireContext(), data)
            expandableListView.setAdapter(adapter)
        }
    }
}