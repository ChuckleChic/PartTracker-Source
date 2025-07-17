package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.UsedPartCount

class UsedPartAdapter(private val items: List<UsedPartCount>) :
    RecyclerView.Adapter<UsedPartAdapter.ViewHolder>() {



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partName: TextView = view.findViewById(R.id.tvUsedPartName)
        val quantity: TextView = view.findViewById(R.id.tvUsedQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_used_part, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.partName.text = item.partName
        holder.quantity.text = "Used: ${item.count}"
    }

    override fun getItemCount(): Int = items.size


}


