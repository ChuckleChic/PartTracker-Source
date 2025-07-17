//package com.example.parttracker.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.parttracker.R
//import com.example.parttracker.model.ScannedPart
//
//class ScannedPartAdapter(private val items: List<ScannedPart>) :
//    RecyclerView.Adapter<ScannedPartAdapter.ScannedPartViewHolder>() {
//
//    class ScannedPartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvPartName: TextView = view.findViewById(R.id.tvPartName)
//        val tvProductId: TextView = view.findViewById(R.id.tvProductId)
//        val tvTrolley: TextView = view.findViewById(R.id.tvTrolley)
//        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedPartViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_scanned_part, parent, false)
//        return ScannedPartViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ScannedPartViewHolder, position: Int) {
//        val part = items[position]
//        holder.tvPartName.text = part.partName
//        holder.tvProductId.text = "Product ID: ${part.productId}"
//        holder.tvTrolley.text = "Trolley: ${part.trolleyName} (${part.trolleyNumber})"
//        holder.tvTimestamp.text = "Scanned: ${part.timestamp}"
//    }
//
//    fun updateData(newParts: List<ScannedPart>) {
//        partList = newParts
//        notifyDataSetChanged()
//    }
//
//
//    override fun getItemCount(): Int = items.size
//}

package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.ScannedPart

class ScannedPartAdapter(private var partList: List<ScannedPart>) :
    RecyclerView.Adapter<ScannedPartAdapter.ScannedPartViewHolder>() {

    class ScannedPartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPartName: TextView = view.findViewById(R.id.tvPartName)
        val tvProductId: TextView = view.findViewById(R.id.tvProductId)
        val tvTrolley: TextView = view.findViewById(R.id.tvTrolley)
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedPartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scanned_part, parent, false)
        return ScannedPartViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScannedPartViewHolder, position: Int) {
        val part = partList[position]
        holder.tvPartName.text = part.partName
        holder.tvProductId.text = "Product ID: ${part.productId}"
        holder.tvTrolley.text = "Trolley: ${part.trolleyName} (${part.trolleyNumber})"
        holder.tvTimestamp.text = "Scanned: ${part.timestamp}"
    }

    override fun getItemCount(): Int = partList.size

    // ✅ Now correctly referring to partList
    fun updateData(newParts: List<ScannedPart>) {
        partList = newParts
        notifyDataSetChanged()
    }
}

