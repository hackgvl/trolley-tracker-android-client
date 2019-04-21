package com.codeforgvl.trolleytrackerclient.ui.schedule.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.codeforgvl.trolleytrackerclient.R

class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val timeTextView = itemView.findViewById<TextView>(R.id.scheduleTime)
    val routeNameView = itemView.findViewById<TextView>(R.id.routeName)
}