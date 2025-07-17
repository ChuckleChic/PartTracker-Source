//////package com.example.parttracker.adapter
//////
//////import android.view.LayoutInflater
//////import android.view.ViewGroup
//////import androidx.recyclerview.widget.RecyclerView
//////import com.example.parttracker.databinding.ItemDashboardRowBinding
//////import com.example.parttracker.model.DashboardRow
//////
//////class DashboardAdapter(private var rows: List<DashboardRow>) :
//////
//////    RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {
//////
//////    var onItemLongClick: ((DashboardRow) -> Unit)? = null
//////
//////
//////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
//////        val binding = ItemDashboardRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//////        return DashboardViewHolder(binding)
//////    }
//////
//////    fun updateData(newList: List<DashboardRow>) {
//////        rows = newList
//////        notifyDataSetChanged()
//////    }
//////
//////
//////    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
//////        holder.bind(rows[position])
//////        holder.itemView.setOnLongClickListener {
//////            val item = rows[position]
//////            onItemLongClick?.invoke(item)
//////            true
//////        }
//////
//////    }
//////
//////    override fun getItemCount(): Int = rows.size
//////
//////    fun updateList(newList: List<DashboardRow>) {
//////        rows = newList
//////        notifyDataSetChanged()
//////    }
//////
//////    inner class DashboardViewHolder(private val binding: ItemDashboardRowBinding) :
//////        RecyclerView.ViewHolder(binding.root) {
//////        fun bind(row: DashboardRow) {
//////            binding.apply {
//////                tvDate.text = row.date
//////                tvShift.text = row.shift
//////                tvModel.text = row.model
//////                tvPlan.text = row.plan.toString()
//////                tvPartName.text = row.partName
//////                tvOB.text = row.ob.toString()
//////                tvDispatch.text = row.dispatch.toString()
//////                tvReceived.text = row.received.toString()
//////                tvRemPS.text = row.remainingFromPS.toString()
//////                tvRemVA.text = row.remainingToReceiveAtVA.toString()
//////                tvProduced.text = row.produced.toString()
//////                tvRejection.text = row.rejection.toString()
//////                tvCB.text = row.closingBalance.toString()
//////            }
//////        }
//////    }
//////}
////package com.example.parttracker.adapter
////
////import android.view.LayoutInflater
////import android.view.ViewGroup
////import androidx.recyclerview.widget.RecyclerView
////import com.example.parttracker.databinding.ItemDashboardRowBinding
////import com.example.parttracker.model.DashboardEntry
////
////class DashboardAdapter(private var rows: List<DashboardEntry>) :
////    RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {
////
////    var onItemLongClick: ((DashboardEntry) -> Unit)? = null
////
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
////        val binding = ItemDashboardRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
////        return DashboardViewHolder(binding)
////    }
////
////    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
////        holder.bind(rows[position])
////        holder.itemView.setOnLongClickListener {
////            onItemLongClick?.invoke(rows[position])
////            true
////        }
////    }
////
////    override fun getItemCount(): Int = rows.size
////
////    // Updated type here too (was: com.example.parttracker.data.DashboardRow)
////    fun updateList(newList: List<DashboardEntry>) {
////        rows = newList
////        notifyDataSetChanged()
////    }
////
////    inner class DashboardViewHolder(private val binding: ItemDashboardRowBinding) :
////        RecyclerView.ViewHolder(binding.root) {
////
////        fun bind(row: DashboardEntry) {
////            binding.apply {
////                tvDate.text = row.date
////                tvShift.text = row.shift
////                tvModel.text = "Model: ${row.model}"
////                tvPlan.text = "Plan: ${row.planned}"                  // changed from row.plan
////                tvPartName.text = row.partName
////                tvOB.text = "OB: ${row.openingBalance}"
////                tvDispatch.text = "Dispatch: ${row.dispatch}"
////                tvReceived.text = "Received: ${row.received}"
////                tvRemPS.text = "Remaining PS: ${row.remainingPs}"     // changed from row.remainingFromPS
////                tvRemVA.text = "Remaining VA: ${row.remainingVa}"     // changed from row.remainingToReceiveAtVA
////                tvProduced.text = "Produced: ${row.produced}"
////                tvRejection.text = "Rejection: ${row.rejection}"
////                tvCB.text = "Closing: ${row.cb}"                      // changed from row.closingBalance
////            }
////        }
////    }
////}
//
//
//package com.example.parttracker.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.recyclerview.widget.RecyclerView
//import com.example.parttracker.R
//import com.example.parttracker.model.DashboardRow
//
//class DashboardAdapter(
//    private var rows: List<DashboardRow>,
//    private val onEditClick: (DashboardRow) -> Unit
//) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
//
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvPartName: TextView = view.findViewById(R.id.tvPartName)
//        val tvPlan: TextView = view.findViewById(R.id.tvPlan)
//        val tvOB: TextView = view.findViewById(R.id.tvOB)
//        val tvDispatch: TextView = view.findViewById(R.id.tvDispatch)
//        val tvReceived: TextView = view.findViewById(R.id.tvReceived)
//        val tvRemainingPs: TextView = view.findViewById(R.id.tvRemainingPs)
//        val tvRemainingVa: TextView = view.findViewById(R.id.tvRemainingVa)
//        val tvProduced: TextView = view.findViewById(R.id.tvProduced)
//        val tvRejection: TextView = view.findViewById(R.id.tvRejection)
//        val tvCB: TextView = view.findViewById(R.id.tvCB)
//        val btnEdit: Button = view.findViewById(R.id.btnEdit)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.dashboard_row_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = rows.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = rows[position]
//        holder.tvPartName.text = "🔩 ${item.partName}"
//        holder.tvPlan.text = "Plan: ${item.planned}"
//        holder.tvOB.text = "OB: ${item.ob}"
//        holder.tvDispatch.text = "Dispatch: ${item.dispatch}"
//        holder.tvReceived.text = "Received: ${item.received}"
//        holder.tvRemainingPs.text = "Remaining PS: ${item.remainingPs}"
//        holder.tvRemainingVa.text = "Remaining VA: ${item.remainingVa}"
//        holder.tvProduced.text = "Produced: ${item.produced}"
//        holder.tvRejection.text = "Rejection: ${item.rejection}"
//        holder.tvCB.text = "CB: ${item.cb}"
//
//        holder.btnEdit.setOnClickListener {
//            onEditClick(item)
//        }
//    }
//
//    fun updateData(newRows: List<DashboardRow>) {
//        this.rows = newRows
//        notifyDataSetChanged()
//    }
//}

package com.example.parttracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.DashboardRow

//class DashboardAdapter(
//    private var rows: List<DashboardRow>,
//    private val onEditClick: (DashboardRow) -> Unit
//) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
//
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvDate: TextView = view.findViewById(R.id.tvDate)
//        val tvShift: TextView = view.findViewById(R.id.tvShift)
//        val tvPartName: TextView = view.findViewById(R.id.tvPartName)
//        val tvPlan: TextView = view.findViewById(R.id.tvPlan)
//        val tvOB: TextView = view.findViewById(R.id.tvOb)
//        val tvDispatch: TextView = view.findViewById(R.id.tvDispatch)
//        val tvReceived: TextView = view.findViewById(R.id.tvReceived)
//        val tvRemainingPs: TextView = view.findViewById(R.id.tvRemainingPs)
//        val tvRemainingVa: TextView = view.findViewById(R.id.tvRemainingVa)
//        val tvProduced: TextView = view.findViewById(R.id.tvProduced)
//        val tvRejection: TextView = view.findViewById(R.id.tvRejection)
//        val tvCB: TextView = view.findViewById(R.id.tvCb)
//        val btnEdit: Button = view.findViewById(R.id.btnEdit)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.dashboard_row_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = rows.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = rows[position]
//
//        holder.tvDate.text = item.date
//        holder.tvShift.text = item.shift
//        holder.tvPartName.text = item.partName
//        holder.tvPlan.text = item.planned.toString()
//        holder.tvOB.text = item.ob.toString()
//        holder.tvDispatch.text = item.dispatch.toString()
//        holder.tvReceived.text = item.received.toString()
//        holder.tvRemainingPs.text = item.remainingPs.toString()
//        holder.tvRemainingVa.text = item.remainingVa.toString()
//        holder.tvProduced.text = item.produced.toString()
//        holder.tvRejection.text = item.rejection.toString()
//        holder.tvCB.text = item.cb.toString()
//
//        holder.btnEdit.setOnClickListener {
//            onEditClick(item)
//        }
//    }
//
//    fun updateData(newRows: List<DashboardRow>) {
//        this.rows = newRows
//        notifyDataSetChanged()
//    }
//}

class DashboardAdapter(
    private var rows: List<DashboardRow>,
    private val onEditClick: (DashboardRow) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvModel: TextView = view.findViewById(R.id.tvModel)
        val tvColor: TextView = view.findViewById(R.id.tvColor)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvShift: TextView = view.findViewById(R.id.tvShift)
        val tvPartName: TextView = view.findViewById(R.id.tvPartName)
        val tvPlan: TextView = view.findViewById(R.id.tvPlan)
        val tvOB: TextView = view.findViewById(R.id.tvOb)
        val tvDispatch: TextView = view.findViewById(R.id.tvDispatch)
        val tvReceived: TextView = view.findViewById(R.id.tvReceived)
        val tvRemainingPs: TextView = view.findViewById(R.id.tvRemainingPs)
        val tvRemainingVa: TextView = view.findViewById(R.id.tvRemainingVa)
        val tvProduced: TextView = view.findViewById(R.id.tvProduced)
        val tvRejection: TextView = view.findViewById(R.id.tvRejection)
        val tvCB: TextView = view.findViewById(R.id.tvCb)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val detailsLayout: LinearLayout = view.findViewById(R.id.detailsLayout)
        val headerLayout: LinearLayout = view.findViewById(R.id.headerLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rows.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = rows[position]

        // Header data
        holder.tvModel.text = item.model
        holder.tvColor.text = item.color

        // Expanded section data
        holder.tvDate.text = "Date: ${item.date}"
        holder.tvShift.text = "Shift: ${item.shift}"
        holder.tvPartName.text = "Part: ${item.partName}"
        holder.tvPlan.text = "Planned: ${item.planned}"
        holder.tvOB.text = "OB: ${item.ob}"
        holder.tvDispatch.text = "Dispatch: ${item.dispatch}"
        holder.tvReceived.text = "Received: ${item.received}"
        holder.tvRemainingPs.text = "PS Remain: ${item.remainingPs}"
        holder.tvRemainingVa.text = "VA Remain: ${item.remainingVa}"
        holder.tvProduced.text = "Produced: ${item.produced}"
        holder.tvRejection.text = "Rejection: ${item.rejection}"
        holder.tvCB.text = "CB: ${item.cb}"

        // Expand/collapse toggle
        val isExpanded = expandedPositions.contains(position)
        holder.detailsLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.headerLayout.setOnClickListener {
            if (isExpanded) expandedPositions.remove(position)
            else expandedPositions.add(position)
            notifyItemChanged(position)
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(item)
        }
    }

    fun updateData(newRows: List<DashboardRow>) {
        this.rows = newRows
        notifyDataSetChanged()
    }
}


