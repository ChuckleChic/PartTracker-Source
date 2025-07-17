//package com.example.parttracker.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import com.google.android.material.button.MaterialButton
//import androidx.lifecycle.LifecycleOwner
//import androidx.recyclerview.widget.RecyclerView
//import com.example.parttracker.R
//import com.example.parttracker.viewmodel.PartViewModel
//import android.text.InputType
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//
//
//class StockAdapter(
//    private val partNames: List<String>,                 // All unique part names
//    private val viewModel: PartViewModel,                // ViewModel to access LiveData
//    private val lifecycleOwner: LifecycleOwner           // Needed to observe LiveData
//) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {
//
//    class StockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvPartName: TextView = view.findViewById(R.id.tvPartName)
//        val tvStock: TextView = view.findViewById(R.id.tvStock)
//        val btnUse: MaterialButton = view.findViewById(R.id.btnUsePart)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_part_stock, parent, false)
//        return StockViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = partNames.size
//
//    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
//        val partName = partNames[position]
//        holder.tvPartName.text = partName
//
//        // Observe live stock
//        viewModel.getStockForPart(partName).observe(lifecycleOwner) { stock ->
//            holder.tvStock.text = "Stock: ${stock ?: 0}"
//        }
//
//        // Handle use button
//        holder.btnUse.setOnClickListener {
//            val context = holder.itemView.context
//
//            val input = EditText(context).apply {
//                inputType = InputType.TYPE_CLASS_NUMBER
//                hint = "Enter quantity used"
//            }
//
//            AlertDialog.Builder(context)
//                .setTitle("Use Part")
//                .setMessage("How many \"$partName\" parts were used?")
//                .setView(input)
//                .setPositiveButton("Submit") { _, _ ->
//                    val quantity = input.text.toString().toIntOrNull()
//                    if (quantity != null && quantity > 0) {
//                        viewModel.markPartAsUsed(partName, quantity)
//                        Toast.makeText(context, "$quantity used for $partName", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                .setNegativeButton("Cancel", null)
//                .show()
//        }
//
//    }
//}

package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.viewmodel.PartViewModel
import com.google.android.material.button.MaterialButton

class StockAdapter(
    private val partNames: List<String>,                 // All unique part names
    private val viewModel: PartViewModel,                // ViewModel to access LiveData
    private val lifecycleOwner: LifecycleOwner           // Needed to observe LiveData
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    inner class StockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPartName: TextView = view.findViewById(R.id.tvPartName)
        val tvStock: TextView = view.findViewById(R.id.tvStock)
        val btnUse: MaterialButton = view.findViewById(R.id.btnUsePart)
        var currentLiveData: LiveData<Int>? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_part_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun getItemCount(): Int = partNames.size

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val partName = partNames[position]
        holder.tvPartName.text = partName

        // Remove any existing observer to prevent stacking
        holder.currentLiveData?.removeObservers(lifecycleOwner)

        // Observe new LiveData for current part
        val stockLiveData = viewModel.getStockForPart(partName)
        stockLiveData.observe(lifecycleOwner) { stock ->
            holder.tvStock.text = "Stock: ${stock ?: 0}"
        }
        holder.currentLiveData = stockLiveData

        // Handle button click to mark used
        holder.btnUse.setOnClickListener {
            val context = holder.itemView.context

            val input = EditText(context).apply {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
                hint = "Enter quantity used"
            }

            AlertDialog.Builder(context)
                .setTitle("Use Part")
                .setMessage("How many \"$partName\" were used?")
                .setView(input)
                .setPositiveButton("Submit") { _, _ ->
                    val quantity = input.text.toString().toIntOrNull()
                    if (quantity != null && quantity > 0) {
                        viewModel.markPartAsUsed(partName, quantity, context)
                        Toast.makeText(context, "$quantity used for $partName", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
