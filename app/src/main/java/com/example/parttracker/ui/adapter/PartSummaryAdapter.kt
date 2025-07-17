package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.PartCountByLocation

class PartSummaryAdapter(private val items: List<PartCountByLocation>) :
    RecyclerView.Adapter<PartSummaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partName: TextView = view.findViewById(R.id.tvPartName)
        val quantity: TextView = view.findViewById(R.id.tvPartQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_part_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.partName.text = item.partName
        holder.quantity.text = "Qty: ${item.count}"
    }

    override fun getItemCount(): Int = items.size
}
