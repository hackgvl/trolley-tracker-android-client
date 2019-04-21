package com.codeforgvl.trolleytrackerclient.ui.schedule.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.codeforgvl.trolleytrackerclient.R

class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title = itemView.findViewById<TextView>(R.id.section_text)
}