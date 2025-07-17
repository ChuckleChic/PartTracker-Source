package com.example.parttracker.ui.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.parttracker.R
import com.example.parttracker.model.DashboardRow
import com.example.parttracker.model.SequenceGroup

class SequenceGroupAdapter(
    private var sequenceGroups: List<SequenceGroup>,
    private val onObEditClicked: (partRow: DashboardRow) -> Unit // ✅ Switched to DashboardRow
) : RecyclerView.Adapter<SequenceGroupAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerLayout: LinearLayout = view.findViewById(R.id.headerLayout)
        val tvSequenceHeader: TextView = view.findViewById(R.id.tvSequenceHeader)
        val ivExpandToggle: ImageView = view.findViewById(R.id.ivExpandToggle)
        val expandableContent: LinearLayout = view.findViewById(R.id.expandableContent)
        val tvMetaInfo: TextView = view.findViewById(R.id.tvMetaInfo)
        val tableContainer: LinearLayout = view.findViewById(R.id.tableContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sequence_group, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = sequenceGroups.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = sequenceGroups[position]

        val percentComplete = if (group.totalPlanned == 0) 0
        else (group.totalCompleted * 100 / group.totalPlanned)

        holder.tvSequenceHeader.text = "Sequence ${group.sequence} - $percentComplete% Complete"

        holder.headerLayout.setBackgroundColor(getGradientColor(percentComplete))

        holder.expandableContent.visibility = if (group.isExpanded) View.VISIBLE else View.GONE
        holder.ivExpandToggle.setImageResource(
            if (group.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
        )

        holder.headerLayout.setOnClickListener {
            group.isExpanded = !group.isExpanded
            notifyItemChanged(position)
        }

        if (group.isExpanded) {
            holder.tableContainer.removeAllViews()

            val headerRow = createTableRow(holder.itemView.context, true,
                listOf("Part", "Plan", "OB", "Dispatch", "Received", "PS Rem", "VA Rem", "Prod", "Rej", "CB"),
                null // No data for header
            )
            holder.tableContainer.addView(headerRow)

            group.colorGroups.forEach { colorGroup ->
                val colorTitle = TextView(holder.itemView.context).apply {
                    text = "🎨 Color: ${colorGroup.color}"
                    textSize = 15f
                    setPadding(8, 12, 8, 6)
                    setTypeface(null, Typeface.BOLD_ITALIC)
                }
                holder.tableContainer.addView(colorTitle)

                colorGroup.rows.forEach { rowData: DashboardRow ->
                    val rowLayout = createTableRow(holder.itemView.context, false, listOf(
                        rowData.partName,
                        rowData.planned.toString(),
                        rowData.ob.toString(),
                        rowData.dispatch.toString(),
                        rowData.received.toString(),
                        rowData.remainingPs.toString(),
                        rowData.remainingVa.toString(),
                        rowData.produced.toString(),
                        rowData.rejection.toString(),
                        rowData.cb.toString()
                    ), rowData)
                    rowLayout.setBackgroundColor(Color.WHITE)
                    holder.tableContainer.addView(rowLayout)
                }
            }

            group.colorGroups.firstOrNull()?.rows?.firstOrNull()?.let {
                holder.tvMetaInfo.text = "Date: ${it.date}   Shift: ${it.shift}   Model: ${it.model}"
            }
        }
    }

    private fun createTableRow(
        context: Context,
        isHeader: Boolean,
        texts: List<String>,
        partRow: DashboardRow?
    ): LinearLayout {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4, 4, 4, 4)
        }

        texts.forEachIndexed { index, txt ->
            val tv = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(100.dp(), LinearLayout.LayoutParams.WRAP_CONTENT)
                gravity = Gravity.CENTER
                textSize = if (isHeader) 15f else 14f
                setPadding(6, 6, 6, 6)
                text = txt
                if (isHeader) setTypeface(null, Typeface.BOLD)

                if (!isHeader && index == 2 && partRow != null) {
                    setTextColor(Color.BLUE)
                    setTypeface(null, Typeface.BOLD)
                    setOnClickListener {
                        onObEditClicked(partRow)
                    }
                }
            }
            row.addView(tv)
        }
        return row
    }

    private fun getGradientColor(percent: Int): Int {
        val clamped = percent.coerceIn(0, 100)

        val startR = 255
        val startG = 208
        val startB = 208

        val midR = 255
        val midG = 235
        val midB = 173

        val endR = 208
        val endG = 255
        val endB = 208

        val red: Int
        val green: Int
        val blue: Int

        if (clamped < 50) {
            val ratio = clamped / 50f
            red = startR + ((midR - startR) * ratio).toInt()
            green = startG + ((midG - startG) * ratio).toInt()
            blue = startB + ((midB - startB) * ratio).toInt()
        } else {
            val ratio = (clamped - 50) / 50f
            red = midR + ((endR - midR) * ratio).toInt()
            green = midG + ((endG - midG) * ratio).toInt()
            blue = midB + ((endB - midB) * ratio).toInt()
        }

        return Color.rgb(red, green, blue)
    }

//    fun updateData(newData: List<SequenceGroup>) {
//        this.sequenceGroups = newData
//        notifyDataSetChanged()
//    }


    fun updateData(newData: List<SequenceGroup>) {
        this.sequenceGroups = newData.sortedBy { it.sequence } // ✅ Sort by sequence number
        notifyDataSetChanged()
    }


    private fun Int.dp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
