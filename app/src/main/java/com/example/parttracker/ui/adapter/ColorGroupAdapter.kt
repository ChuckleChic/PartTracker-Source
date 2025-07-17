package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.ColorGroup

class ColorGroupAdapter(private val colorGroups: List<ColorGroup>) :
    RecyclerView.Adapter<ColorGroupAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_group, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colorGroups[position])
    }

    override fun getItemCount(): Int = colorGroups.size

    inner class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvColorName: TextView = view.findViewById(R.id.tvColorName)
        private val ivExpand: ImageView = view.findViewById(R.id.ivExpand)
        private val rvParts: RecyclerView = view.findViewById(R.id.rvParts)

        fun bind(colorGroup: ColorGroup) {
            tvColorName.text = "Color: ${colorGroup.color}"

            // Setup RecyclerView
            rvParts.layoutManager = LinearLayoutManager(itemView.context)
            rvParts.adapter = PartItemAdapter(colorGroup.partsCB)
            rvParts.visibility = if (colorGroup.isExpanded) View.VISIBLE else View.GONE

            // Rotate arrow icon accordingly
            ivExpand.rotation = if (colorGroup.isExpanded) 180f else 0f

            ivExpand.setOnClickListener {
                colorGroup.isExpanded = !colorGroup.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
