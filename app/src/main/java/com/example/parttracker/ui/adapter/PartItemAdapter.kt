package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.CBPartEntry

class PartItemAdapter(private val partList: List<CBPartEntry>) :
    RecyclerView.Adapter<PartItemAdapter.PartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_part_row, parent, false)
        return PartViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        holder.bind(partList[position])
    }

    override fun getItemCount(): Int = partList.size

    inner class PartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvPartName: TextView = view.findViewById(R.id.tvPartName)

        fun bind(part: CBPartEntry) {
            tvPartName.text = "• ${part.partName}: ${part.cb}"
        }
    }
}
