package com.codeforgvl.trolleytrackerclient.models

import com.codeforgvl.trolleytrackerclient.ui.schedule.adapter.SchedulesAdapter

class Day(val text: String): ScheduleItems() {
    override fun getType(): SchedulesAdapter.Type {
        return SchedulesAdapter.Type.DAY
    }
}