package com.codeforgvl.trolleytrackerclient.models

import com.codeforgvl.trolleytrackerclient.ui.schedule.adapter.SchedulesAdapter

abstract class ScheduleItems {
    abstract fun getType():SchedulesAdapter.Type
}