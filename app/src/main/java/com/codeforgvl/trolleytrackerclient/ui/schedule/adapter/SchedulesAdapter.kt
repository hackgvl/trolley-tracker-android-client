package com.codeforgvl.trolleytrackerclient.ui.schedule.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.codeforgvl.trolleytrackerclient.R
import com.codeforgvl.trolleytrackerclient.models.ScheduledRoute
import com.codeforgvl.trolleytrackerclient.models.Day
import com.codeforgvl.trolleytrackerclient.models.ScheduleItems

class SchedulesAdapter(
        private val context: Context,
        private val scheduledClickedListener: ScheduleClickedListener,
        private val items: ArrayList<ScheduleItems>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class Type {
        SCHEDULE, DAY
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].getType().ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Type.DAY.ordinal) {
            DayViewHolder(
                    LayoutInflater.from(context)
                            .inflate(R.layout.view_schedule_header, parent, false)
            )
        } else {
            ScheduleViewHolder(
                    LayoutInflater.from(context)
                            .inflate(R.layout.view_schedule_item, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is DayViewHolder) {
            holder.title.text = (items[position] as Day).text
        } else if (holder is ScheduleViewHolder) {
            val rs = (items[position] as ScheduledRoute).routeSchedule
            holder.routeNameView.text = rs.RouteLongName
            holder.timeTextView.text = String.format(
                    context.getString(R.string.schedule_time_format),
                    rs.StartTime,
                    rs.EndTime)
            holder.itemView.isSelected = (items[position] as ScheduledRoute).interval.containsNow()
            holder.itemView.isEnabled = !(items[position] as ScheduledRoute).interval.end.isBeforeNow

            holder.itemView.setOnClickListener { scheduledClickedListener.itemClicked(items[position] as ScheduledRoute) }
        }
    }

    interface ScheduleClickedListener {
        fun itemClicked(scheduledRoute: ScheduledRoute)
    }

}