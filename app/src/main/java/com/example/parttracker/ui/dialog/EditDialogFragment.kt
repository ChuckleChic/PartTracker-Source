package com.example.parttracker.ui.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.parttracker.R
import com.example.parttracker.model.DashboardRow
import com.example.parttracker.viewmodel.DashboardViewModel
import android.content.Context

class EditDialogFragment(
    private val dashboardRow: DashboardRow
) : DialogFragment() {

    private val viewModel: DashboardViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_fields, null)

        val etOB = view.findViewById<EditText>(R.id.etOB)
        val etProduced = view.findViewById<EditText>(R.id.etProduced)
        val etRejection = view.findViewById<EditText>(R.id.etRejection)

        etOB.setText(dashboardRow.ob.toString())
        etProduced.setText(dashboardRow.produced.toString())
        etRejection.setText(dashboardRow.rejection.toString())

        return AlertDialog.Builder(requireContext())
            .setTitle("Edit ${dashboardRow.partName}")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val updated = dashboardRow.copy(
                    ob = etOB.text.toString().toIntOrNull() ?: 0,
                    produced = etProduced.text.toString().toIntOrNull() ?: 0,
                    rejection = etRejection.text.toString().toIntOrNull() ?: 0
                )
                viewModel.updateDashboardRow(updated, requireContext()
                )
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
