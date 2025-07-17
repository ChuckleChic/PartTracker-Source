package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.ModelGroup

class ModelGroupAdapter : RecyclerView.Adapter<ModelGroupAdapter.ModelViewHolder>() {

    private var modelGroups: List<ModelGroup> = emptyList()

    fun submitList(list: List<ModelGroup>) {
        modelGroups = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_model_group, parent, false)
        return ModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(modelGroups[position])
    }

    override fun getItemCount(): Int = modelGroups.size

    inner class ModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvModelName: TextView = view.findViewById(R.id.tvModelName)
        private val ivExpand: ImageView = view.findViewById(R.id.ivExpand)
        private val rvColors: RecyclerView = view.findViewById(R.id.rvColors)

        fun bind(modelGroup: ModelGroup) {
            tvModelName.text = "Model: ${modelGroup.model}"

            // Setup inner RecyclerView
            rvColors.layoutManager = LinearLayoutManager(itemView.context)
            rvColors.adapter = ColorGroupAdapter(modelGroup.colorGroups)
            rvColors.isNestedScrollingEnabled = false
            rvColors.visibility = if (modelGroup.isExpanded) View.VISIBLE else View.GONE

            // Set arrow icon rotation
            ivExpand.rotation = if (modelGroup.isExpanded) 180f else 0f

            // Handle expand/collapse
            ivExpand.setOnClickListener {
                modelGroup.isExpanded = !modelGroup.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
