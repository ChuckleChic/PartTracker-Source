//////////package com.example.parttracker.ui.adapter
//////////
//////////import android.view.LayoutInflater
//////////import android.view.View
//////////import android.view.ViewGroup
//////////import android.widget.TextView
//////////import androidx.recyclerview.widget.RecyclerView
//////////import com.example.parttracker.R
//////////import com.example.parttracker.model.DashboardRow
//////////
//////////class PlanAdapter(
//////////    private var plans: List<DashboardRow>,
//////////    private val onItemClick: (DashboardRow) -> Unit
//////////) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {
//////////
//////////    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//////////        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
//////////        val tvShift: TextView = itemView.findViewById(R.id.tvShift)
//////////        val tvModel: TextView = itemView.findViewById(R.id.tvModel)
//////////        val tvPlan: TextView = itemView.findViewById(R.id.tvPlan)
//////////        val tvPartName: TextView = itemView.findViewById(R.id.tvPartName)
//////////        val tvOb: TextView = itemView.findViewById(R.id.tvOb)
//////////        val tvDispatch: TextView = itemView.findViewById(R.id.tvDispatch)
//////////        val tvReceived: TextView = itemView.findViewById(R.id.tvReceived)
//////////        val tvRemainingPs: TextView = itemView.findViewById(R.id.tvRemainingPs)
//////////        val tvRemainingVa: TextView = itemView.findViewById(R.id.tvRemainingVa)
//////////        val tvProduced: TextView = itemView.findViewById(R.id.tvProduced)
//////////        val tvRejection: TextView = itemView.findViewById(R.id.tvRejection)
//////////        val tvCb: TextView = itemView.findViewById(R.id.tvCb)
//////////    }
//////////
//////////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
//////////        val view = LayoutInflater.from(parent.context)
//////////            .inflate(R.layout.item_dashboard, parent, false)
//////////        return PlanViewHolder(view)
//////////    }
//////////
//////////    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
//////////        val item = plans[position]
//////////
//////////        holder.tvDate.text = item.date
//////////        holder.tvShift.text = item.shift
//////////        holder.tvModel.text = item.model
//////////        holder.tvPlan.text = item.quantity.toString()
//////////        holder.tvPartName.text = item.partName
//////////        holder.tvOb.text = item.openingBalance.toString()
//////////        holder.tvDispatch.text = item.dispatch.toString()
//////////        holder.tvReceived.text = item.received.toString()
//////////        holder.tvRemainingPs.text = item.remainingPs.toString()
//////////        holder.tvRemainingVa.text = item.remainingVa.toString()
//////////        holder.tvProduced.text = item.produced.toString()
//////////        holder.tvRejection.text = item.rejection.toString()
//////////        holder.tvCb.text = item.cb.toString()
//////////
//////////        holder.itemView.setOnClickListener {
//////////            onItemClick(item)
//////////        }
//////////    }
//////////
//////////    override fun getItemCount(): Int = plans.size
//////////
//////////    fun updateData(newPlans: List<DashboardRow>) {
//////////        plans = newPlans
//////////        notifyDataSetChanged()
//////////    }
//////////
//////////
//////////}
////////
////////package com.example.parttracker.ui.adapter
////////
////////import android.view.LayoutInflater
////////import android.view.View
////////import android.view.ViewGroup
////////import android.widget.TextView
////////import androidx.recyclerview.widget.RecyclerView
////////import com.example.parttracker.R
////////import com.example.parttracker.model.DashboardEntry
////////import com.example.parttracker.model.DashboardRow
////////
////////class PlanAdapter(
////////    private var data: List<DashboardRow>,
////////    private val onItemClick: (DashboardRow) -> Unit
////////) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {
////////
////////    inner class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
////////        val tvDate: TextView = view.findViewById(R.id.tvDate)
////////        val tvShift: TextView = view.findViewById(R.id.tvShift)
////////        val tvModel: TextView = view.findViewById(R.id.tvModel)
////////        val tvPartName: TextView = view.findViewById(R.id.tvPartName)
////////        val tvPlan: TextView = view.findViewById(R.id.tvPlan)
////////        val tvOb: TextView = view.findViewById(R.id.tvOb)
////////        val tvDispatch: TextView = view.findViewById(R.id.tvDispatch)
////////        val tvReceived: TextView = view.findViewById(R.id.tvReceived)
////////        val tvRemainingPs: TextView = view.findViewById(R.id.tvRemainingPs)
////////        val tvRemainingVa: TextView = view.findViewById(R.id.tvRemainingVa)
////////        val tvProduced: TextView = view.findViewById(R.id.tvProduced)
////////        val tvRejection: TextView = view.findViewById(R.id.tvRejection)
////////        val tvCb: TextView = view.findViewById(R.id.tvCb)
////////    }
////////
////////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
////////        val view = LayoutInflater.from(parent.context)
////////            .inflate(R.layout.item_dashboard, parent, false)
////////        return PlanViewHolder(view)
////////    }
////////
////////    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
////////        val row = data[position]
////////        holder.tvDate.text = row.date
////////        holder.tvShift.text = row.shift
////////        holder.tvModel.text = row.model
////////        holder.tvPartName.text = row.partName
////////        holder.tvPlan.text = row.quantity.toString()
////////        holder.tvOb.text = row.openingBalance.toString()
////////        holder.tvDispatch.text = row.dispatch.toString()
////////        holder.tvReceived.text = row.received.toString()
////////        holder.tvRemainingPs.text = row.remainingPs.toString()
////////        holder.tvRemainingVa.text = row.remainingVa.toString()
////////        holder.tvProduced.text = row.produced.toString()
////////        holder.tvRejection.text = row.rejection.toString()
////////        holder.tvCb.text = row.cb.toString()
////////
////////        holder.itemView.setOnClickListener { onItemClick(row) }
////////    }
////////
////////    override fun getItemCount(): Int = data.size
////////
////////    fun updateData(newData: List<DashboardRow>) {
////////        data = newData
////////        notifyDataSetChanged()
////////    }
////////}
////////
//////
//////
////////package com.example.parttracker.ui.adapter
////////
////////import android.view.LayoutInflater
////////import android.view.View
////////import android.view.ViewGroup
////////import android.widget.TextView
////////import androidx.recyclerview.widget.RecyclerView
////////import com.example.parttracker.R
////////import com.example.parttracker.model.DashboardRow
////////
////////class PlanAdapter(
////////    private var items: List<DashboardRow>,
////////    private val onItemClick: (DashboardRow) -> Unit
////////) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {
////////
////////    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
////////        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
////////        val tvShift: TextView = itemView.findViewById(R.id.tvShift)
////////        val tvModel: TextView = itemView.findViewById(R.id.tvModel)
////////        val tvPlan: TextView = itemView.findViewById(R.id.tvPlan)
////////        val tvProduced: TextView = itemView.findViewById(R.id.tvProduced)
////////        val tvCb: TextView = itemView.findViewById(R.id.tvCb)
////////    }
////////
////////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
////////        val view = LayoutInflater.from(parent.context)
////////            .inflate(R.layout.item_dashboard, parent, false)
////////        return PlanViewHolder(view)
////////    }
////////
////////    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
////////        val item = items[position]
////////        holder.tvDate.text = item.date
////////        holder.tvShift.text = item.shift
////////        holder.tvModel.text = item.model
////////        holder.tvPlan.text = item.planned.toString()
////////        holder.tvProduced.text = item.produced.toString()
////////        holder.tvCb.text = item.cb.toString()
////////
////////        holder.itemView.setOnClickListener {
////////            onItemClick(item)
////////        }
////////    }
////////
////////    override fun getItemCount(): Int = items.size
////////
////////    fun updateData(newItems: List<DashboardRow>) {
////////        this.items = newItems
////////        notifyDataSetChanged()
////////    }
////////}
//////
//////
//////package com.example.parttracker.ui.adapter
//////
//////import android.view.LayoutInflater
//////import android.view.View
//////import android.view.ViewGroup
//////import android.widget.TextView
//////import androidx.recyclerview.widget.RecyclerView
//////import com.example.parttracker.R
//////import com.example.parttracker.model.PlanEntry
//////
//////class PlanAdapter(private var planList: List<PlanEntry>) :
//////    RecyclerView.Adapter<PlanAdapter.ViewHolder>() {
//////
//////    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//////        val tvSequence: TextView = view.findViewById(R.id.tvSequence)
//////        val tvModel: TextView = view.findViewById(R.id.tvModel)
//////        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
//////        val tvDate: TextView = view.findViewById(R.id.tvDate)
//////        val tvShift: TextView = view.findViewById(R.id.tvShift)
//////    }
//////
//////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//////        val view = LayoutInflater.from(parent.context)
//////            .inflate(R.layout.plan_row_item, parent, false)
//////        return ViewHolder(view)
//////    }
//////
//////    override fun getItemCount(): Int = planList.size
//////
//////    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//////        val item = planList[position]
//////        holder.tvSequence.text = item.sequence
//////        holder.tvModel.text = item.model
//////        holder.tvQuantity.text = item.quantity.toString()
//////        holder.tvDate.text = item.date
//////        holder.tvShift.text = item.shift
//////    }
//////
//////    fun updateList(newList: List<PlanEntry>) {
//////        planList = newList
//////        notifyDataSetChanged()
//////    }
//////}
//////
////
////
////
////package com.example.parttracker.ui.adapter
////
////import android.app.AlertDialog
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.TextView
////import androidx.recyclerview.widget.RecyclerView
////import com.example.parttracker.R
////import com.example.parttracker.model.PlanEntry
////import kotlin.coroutines.jvm.internal.CompletedContinuation.context
////
////class PlanAdapter(private var plans: List<PlanEntry>) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {
////
////    inner class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
////        val tvSequence: TextView = view.findViewById(R.id.tvSequence)
////        val tvModel: TextView = view.findViewById(R.id.tvModel)
////        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
////        val tvDate: TextView = view.findViewById(R.id.tvDate)
////        val tvShift: TextView = view.findViewById(R.id.tvShift)
////    }
////
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
////        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_row_item, parent, false)
////        return PlanViewHolder(view)
////    }
////
////    override fun getItemCount(): Int = plans.size
////
////    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
////        val item = plans[position]
////        holder.tvSequence.text = item.sequence.toString()
////        holder.tvModel.text = item.model
////        holder.tvQuantity.text = item.quantity.toString()
////        holder.tvDate.text = item.date
////        holder.tvShift.text = item.shift
////
////
////        holder.itemView.setOnLongClickListener {
////            AlertDialog.Builder(context)
////                .setTitle("Delete Plan")
////                .setMessage("Do you want to delete the plan for ${plans.model} (Shift ${plans.shift})?")
////                .setPositiveButton("Delete") { _, _ ->
////                    lifecycleScope.launch {
////                        repository.deletePlan(plans.model, plans.date, plans.shift)
////                        loadPlanData()
////                    }
////                }
////                .setNegativeButton("Cancel", null)
////                .show()
////            true
////        }
////
////    }
////
////    fun updateData(newPlans: List<PlanEntry>) {
////        this.plans = newPlans
////        notifyDataSetChanged()
////    }
////}
////
//
//package com.example.parttracker.ui.adapter
//
//import android.app.AlertDialog
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import android.util.Log
//import androidx.recyclerview.widget.RecyclerView
//import com.example.parttracker.R
//import com.example.parttracker.model.PlanEntry
//
//class PlanAdapter(
//    private var plans: List<PlanEntry>,
//    private val onLongClickDelete: (PlanEntry) -> Unit
//) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {
//
//    inner class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvSequence: TextView = view.findViewById(R.id.tvSequence)
//        val tvModel: TextView = view.findViewById(R.id.tvModel)
//        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
//        val tvDate: TextView = view.findViewById(R.id.tvDate)
//        val tvShift: TextView = view.findViewById(R.id.tvShift)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_row_item, parent, false)
//        return PlanViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = plans.size
//
//    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
//        val item = plans[position]
//        holder.tvSequence.text = item.sequence.toString()
//        holder.tvModel.text = item.model
//        holder.tvQuantity.text = item.quantity.toString()
//        holder.tvDate.text = item.date
//        holder.tvShift.text = item.shift
//
//
//
//        holder.itemView.setOnLongClickListener {
//            Log.d("PlanAdapter", "Long clicked on: ${item.model}")
//            onLongClickDelete(item)
//            true
//        }
//
//
//
//
//
//    }
//
//    fun updateData(newPlans: List<PlanEntry>) {
//        this.plans = newPlans
//        notifyDataSetChanged()
//    }
//}


package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.PlanEntry

class PlanAdapter(
    private var plans: List<PlanEntry>,
    private val onLongClickDelete: (PlanEntry) -> Unit
) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    inner class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSequence: TextView = view.findViewById(R.id.tvSequence)
        val tvModel: TextView = view.findViewById(R.id.tvModel)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvShift: TextView = view.findViewById(R.id.tvShift)
        val tvColor: TextView = view.findViewById(R.id.tvColor) // NEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_row_item, parent, false)
        return PlanViewHolder(view)
    }

    override fun getItemCount(): Int = plans.size

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val item = plans[position]
        holder.tvSequence.text = item.sequence.toString()
        holder.tvModel.text = item.model
        holder.tvQuantity.text = item.quantity.toString()
        holder.tvDate.text = item.date
        holder.tvShift.text = item.shift
        holder.tvColor.text = item.color // NEW

        holder.itemView.setOnLongClickListener {
            Log.d("PlanAdapter", "Long clicked on: ${item.model}")
            onLongClickDelete(item)
            true
        }
    }

    fun updateData(newPlans: List<PlanEntry>) {
        this.plans = newPlans
        notifyDataSetChanged()
    }
}
