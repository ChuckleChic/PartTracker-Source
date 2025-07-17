package com.example.parttracker.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.parttracker.R

class CBExpandableAdapter(
    private val context: Context,
    private val data: Map<String, Map<String, Map<String, Int>>>
) : BaseExpandableListAdapter() {

    private val models = data.keys.toList()

    override fun getGroupCount(): Int = models.size

    override fun getChildrenCount(groupPosition: Int): Int {
        return data[models[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any = models[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return data[models[groupPosition]]!!.keys.toList()[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_cb_group, parent, false)
        val tvGroupTitle = view.findViewById<TextView>(R.id.tvModelName)
        tvGroupTitle.text = models[groupPosition]
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_cb_child, parent, false)
        val model = models[groupPosition]
        val color = data[model]!!.keys.toList()[childPosition]
        val parts = data[model]!![color]!!

        val tvColor = view.findViewById<TextView>(R.id.tvColorName)
        val tvParts = view.findViewById<TextView>(R.id.tvPartsList)
        tvColor.text = color
        tvParts.text = parts.entries.joinToString(" | ") { "${it.key}: ${it.value}" }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false
}