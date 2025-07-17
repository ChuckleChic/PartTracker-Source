package com.example.parttracker.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.data.PartDatabase
import com.example.parttracker.model.ScannedPart
import com.example.parttracker.repository.PartRepository
import com.example.parttracker.ui.adapter.ScannedPartAdapter
import com.example.parttracker.util.modelColorMap
import com.example.parttracker.viewmodel.PartViewModel
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ScanFragment : Fragment() {

    private lateinit var repository: PartRepository
    private lateinit var partViewModel: PartViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var scannedPartAdapter: ScannedPartAdapter

    private val args: ScanFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }


//    val sharedPref = requireContext().getSharedPreferences("UserPrefs", 0)
//    val userRole = sharedPref.getString("userRole", "") ?: ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("UserPrefs", 0)
        val userRole = sharedPref.getString("userRole", "") ?: ""


        recyclerView = view.findViewById(R.id.recyclerViewScannedParts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        partViewModel = ViewModelProvider(this)[PartViewModel::class.java]

        lifecycleScope.launch {
            val scannedParts = partViewModel.getAllParts()
            scannedPartAdapter = ScannedPartAdapter(scannedParts)
            recyclerView.adapter = scannedPartAdapter
        }

        val db = PartDatabase.getRoomDatabase(requireContext())
        repository = PartRepository(
            scannedPartDao = db.scannedPartDao(),
            usedPartDao = db.usedPartDao(),
            dashboardDao = db.dashboardEntryDao(),
            planDao = db.planDao(),
            modelProductionDao = db.modelProductionDao()
        )

//        val integrator = IntentIntegrator.forSupportFragment(this)
//        integrator.setPrompt("Scan QR")
//        integrator.setOrientationLocked(true)
//        integrator.setBeepEnabled(true)
//        integrator.initiateScan()
//    }

        // Allow only PaintShopOperator, VehicleAssemblyOperator, or Admin
        if (userRole == "PaintShopOperator" && args.location != "Paint Shop") {
            Toast.makeText(requireContext(), "Access denied: Only Paint Shop access allowed", Toast.LENGTH_SHORT).show()
            return
        }

        if (userRole == "VehicleAssemblyOperator" && args.location != "CTL") {
            Toast.makeText(requireContext(), "Access denied: Only CTL access allowed", Toast.LENGTH_SHORT).show()
            return
        }

// Admin can access everything — so no restriction needed for them

// Now run scanner for allowed users
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setPrompt("Scan QR")
        integrator.setOrientationLocked(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
}

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            try {
                val json = JSONObject(result.contents)
                val trolleyName = json.getString("trolleyName")
                val trolleyNumber = json.getString("trolleyNumber")
                val expectedQuantity = json.getInt("quantity") // from QR

                val partOptions: Map<String, Pair<String, String>> = mapOf(
                    "Front Fender" to ("Front Fender" to "GL181402"),
                    "Upper Shield" to ("Upper Shield" to "GL181025"),
                    "Rear Shield" to ("Rear Shield" to "52GL0686"),
                    "Glove Box" to ("Glove Box" to "GL181153"),
                    "Lid Cover" to ("Lid Cover" to "GL181052"),
                    "Glove Box + Lid Cover" to ("Glove Box + Lid Cover" to "GL181153,GL181052"),
                    "Shield LH/RH" to ("Shield LH/RH" to "GL183574,GL183575"),
                    "Neck Piece" to ("Neck Piece" to "52GL0413")
                )

                val partNames = when (trolleyNumber) {
                    "T 01" -> arrayOf(
                        "Front Fender",
                        "Upper Shield",
                        "Rear Shield",
                        "Glove Box",
                        "Lid Cover",
                        "Glove Box + Lid Cover"
                    )
                    "T 03" -> arrayOf(
                        "Shield LH/RH",
                        "Neck Piece"
                    )
                    else -> arrayOf(json.getString("partName"))
                }

                val container = FrameLayout(requireContext())
                val input = EditText(requireContext()).apply {
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER
                    hint = "Enter actual quantity"
                    setPadding(40, 20, 40, 20)
                }
                container.addView(input)

                AlertDialog.Builder(requireContext())
                    .setTitle("Enter Actual Quantity")
                    .setMessage("Expected from QR: $expectedQuantity\nPlease enter actual quantity:")
                    .setView(container)
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        val actualQuantity = input.text.toString().toIntOrNull() ?: 0
                        val today = getCurrentDate()
                        val shift = getCurrentShift()
                        val allColors = modelColorMap.values.flatten().distinct().sorted()

                        showColorDialog(allColors) { selectedColor ->
                            showModelDialog(selectedColor) { selectedModel ->

                                AlertDialog.Builder(requireContext())
                                    .setTitle("Select Part")
                                    .setItems(partNames) { _, which ->
                                        val selectedPart = partNames[which]
                                        val (partName, productId) = partOptions[selectedPart]
                                            ?: (selectedPart to "")

                                        val adjustedQuantity = if (selectedPart == "Shield LH/RH") actualQuantity / 2 else actualQuantity

                                        val partsToSave = if (selectedPart == "Glove Box + Lid Cover") {
                                            listOf(
                                                ScannedPart(
                                                    partName = "Glove Box",
                                                    productId = "GL181153",
                                                    trolleyName = trolleyName,
                                                    trolleyNumber = trolleyNumber,
                                                    quantity = actualQuantity,
                                                    sequenceNumber = null,
                                                    location = args.location,
                                                    timestamp = getCurrentTimestamp(),
                                                    color = selectedColor,
                                                    model = selectedModel,
                                                    date = today,
                                                    shift = shift
                                                ),
                                                ScannedPart(
                                                    partName = "Lid Cover",
                                                    productId = "GL181052",
                                                    trolleyName = trolleyName,
                                                    trolleyNumber = trolleyNumber,
                                                    quantity = actualQuantity,
                                                    sequenceNumber = null,
                                                    location = args.location,
                                                    timestamp = getCurrentTimestamp(),
                                                    color = selectedColor,
                                                    model = selectedModel,
                                                    date = today,
                                                    shift = shift
                                                )
                                            )
                                        } else {
                                            listOf(
                                                ScannedPart(
                                                    partName = partName,
                                                    productId = productId,
                                                    trolleyName = trolleyName,
                                                    trolleyNumber = trolleyNumber,
                                                    quantity = adjustedQuantity,
                                                    sequenceNumber = null,
                                                    location = args.location,
                                                    timestamp = getCurrentTimestamp(),
                                                    color = selectedColor,
                                                    model = selectedModel,
                                                    date = today,
                                                    shift = shift
                                                )
                                            )
                                        }

                                        lifecycleScope.launch {
                                            for (part in partsToSave) {
                                                repository.insert(part, requireContext())
                                            }
                                            Toast.makeText(
                                                requireContext(),
                                                "Saved $adjustedQuantity for $selectedPart ($selectedModel - $selectedColor)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .setCancelable(false)
                                    .show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            } catch (e: Exception) {
                Log.e("ScanFragment", "QR parse error: ${e.message}")
                Toast.makeText(requireContext(), "Invalid QR", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Scan canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showColorDialog(colors: List<String>, onColorSelected: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Color")
        val colorArray = colors.toTypedArray()
        builder.setItems(colorArray) { _, which ->
            onColorSelected(colorArray[which])
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun showModelDialog(color: String, onModelSelected: (String) -> Unit) {
        val models = modelColorMap.filterValues { it.contains(color) }.keys.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Select Model for $color")
            .setItems(models) { _, which ->
                onModelSelected(models[which])
            }
            .setCancelable(false)
            .show()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentShift(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val totalMinutes = hour * 60 + minute

        return when {
            totalMinutes in 390 until 930 -> "A"  // 6:30 AM to 3:30 PM
            totalMinutes in 930 until 1470 -> "B" // 3:30 PM to 12:30 AM
            else -> "B"
        }
    }

//    private fun getCurrentTimestamp(): String {
//        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        return sdf.format(Date())
//    }

    private fun getCurrentTimestamp(): Long = System.currentTimeMillis()

}
