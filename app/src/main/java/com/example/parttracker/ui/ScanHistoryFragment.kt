////package com.example.parttracker.ui
////
////import android.os.Bundle
////import android.view.*
////import androidx.fragment.app.Fragment
////import androidx.lifecycle.lifecycleScope
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.example.parttracker.R
////import com.example.parttracker.data.PartDatabase
////import com.example.parttracker.repository.PartRepository
////import com.example.parttracker.ui.adapter.ScannedPartAdapter
////import kotlinx.coroutines.launch
////
////class ScanHistoryFragment : Fragment() {
////
////    private lateinit var recyclerView: RecyclerView
////    private lateinit var adapter: ScannedPartAdapter
////    private lateinit var repository: PartRepository
////
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
////        return inflater.inflate(R.layout.fragment_scan_history, container, false)
////    }
////
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        recyclerView = view.findViewById(R.id.recyclerViewScanHistory)
////        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////
////        val db = PartDatabase.getRoomDatabase(requireContext())
////        repository = PartRepository(
////            scannedPartDao = db.scannedPartDao(),
////            usedPartDao = db.usedPartDao(),
////            dashboardDao = db.dashboardEntryDao(),
////            planDao = db.planDao(),
////            modelProductionDao = db.modelProductionDao()
////        )
////
////
////        lifecycleScope.launch {
////            val scannedParts = repository.getAllScannedParts()
////            adapter = ScannedPartAdapter(scannedParts)
////            recyclerView.adapter = adapter
////        }
////    }
////}
//
//
//package com.example.parttracker.ui
//
////import android.os.Build
////import android.os.Bundle
////import android.view.*
////import android.widget.DatePicker
////import androidx.annotation.RequiresApi
////import androidx.fragment.app.Fragment
////import androidx.lifecycle.lifecycleScope
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.example.parttracker.R
////import com.example.parttracker.data.PartDatabase
////import com.example.parttracker.repository.PartRepository
////import com.example.parttracker.ui.adapter.ScannedPartAdapter
////import kotlinx.coroutines.launch
//
//
//import android.os.Build
//import android.os.Bundle
//import android.view.*
//import android.widget.Button
//import android.widget.DatePicker
//import androidx.annotation.RequiresApi
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.parttracker.R
//import com.example.parttracker.data.PartDatabase
//import com.example.parttracker.repository.PartRepository
//import com.example.parttracker.ui.adapter.ScannedPartAdapter
//import kotlinx.coroutines.launch
//
//
//class ScanHistoryFragment : Fragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: ScannedPartAdapter
//    private lateinit var repository: PartRepository
//    private lateinit var datePicker: DatePicker
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View = inflater.inflate(R.layout.fragment_scan_history, container, false)
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        recyclerView = view.findViewById(R.id.recyclerViewScanHistory)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        datePicker = view.findViewById(R.id.datePicker)
//        val btnToday: Button = view.findViewById(R.id.btnToday) // ✅ Add this
//
//        val db = PartDatabase.getRoomDatabase(requireContext())
//        repository = PartRepository(
//            scannedPartDao = db.scannedPartDao(),
//            usedPartDao = db.usedPartDao(),
//            dashboardDao = db.dashboardEntryDao(),
//            planDao = db.planDao(),
//            modelProductionDao = db.modelProductionDao()
//        )
//
//        adapter = ScannedPartAdapter(emptyList())
//        recyclerView.adapter = adapter
//
//        fun getSelectedDate(): String {
//            val day = datePicker.dayOfMonth.toString().padStart(2, '0')
//            val month = (datePicker.month + 1).toString().padStart(2, '0')
//            val year = datePicker.year.toString()
//            return "$day/$month/$year"
//        }
//
//        fun loadScansByDate() {
//            val selectedDate = getSelectedDate()
//            lifecycleScope.launch {
//                val parts = repository.getPartsByDate(selectedDate)
//                adapter.updateData(parts)
//            }
//        }
//
//        // ✅ "Today" button sets current date
//        btnToday.setOnClickListener {
//            val calendar = java.util.Calendar.getInstance()
//            datePicker.updateDate(
//                calendar.get(java.util.Calendar.YEAR),
//                calendar.get(java.util.Calendar.MONTH),
//                calendar.get(java.util.Calendar.DAY_OF_MONTH)
//            )
//        }
//
//        lifecycleScope.launch {
//            repository.deleteOldScannedParts()
//            loadScansByDate()
//        }
//
//        datePicker.setOnDateChangedListener { _, _, _, _ ->
//            loadScansByDate()
//        }
//    }
//
//
//
//}


package com.example.parttracker.ui

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.data.PartDatabase
import com.example.parttracker.repository.PartRepository
import com.example.parttracker.ui.adapter.ScannedPartAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScanHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScannedPartAdapter
    private lateinit var repository: PartRepository
    private lateinit var tvSelectedDate: TextView

    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_scan_history, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerViewScanHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ScannedPartAdapter(mutableListOf())
        recyclerView.adapter = adapter




        val btnToday: Button = view.findViewById(R.id.btnToday)
        val btnSelectDate: Button = view.findViewById(R.id.btnSelectDate)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)

        val db = PartDatabase.getRoomDatabase(requireContext())
        repository = PartRepository(
            scannedPartDao = db.scannedPartDao(),
            usedPartDao = db.usedPartDao(),
            dashboardDao = db.dashboardEntryDao(),
            planDao = db.planDao(),
            modelProductionDao = db.modelProductionDao()
        )

        // Function to format Calendar to dd/MM/yyyy
        fun formatDate(calendar: Calendar): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(calendar.time)
        }

        // Load parts based on selectedDate
        fun loadScansByDate() {
            tvSelectedDate.text = "Showing data for $selectedDate"
            lifecycleScope.launch {
                val parts = repository.getPartsByDate(selectedDate)
                adapter.updateData(parts)
                view.findViewById<TextView>(R.id.tvEmptyMessage).visibility =
                    if (parts.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        // Load today's data on first launch
        val todayCal = Calendar.getInstance()
        selectedDate = formatDate(todayCal)

        lifecycleScope.launch {
            repository.deleteOldScannedParts()
            loadScansByDate()
        }

        // "Select Date" opens Material Date Picker
        btnSelectDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Scan Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                val cal = Calendar.getInstance().apply {
                    timeInMillis = selection
                }
                selectedDate = formatDate(cal)
                loadScansByDate()
            }
        }

        // "Today" button resets to today
        btnToday.setOnClickListener {
            selectedDate = formatDate(Calendar.getInstance())
            loadScansByDate()
        }
    }
}

