package com.codeforgvl.trolleytrackerclient.ui.schedule

import com.codeforgvl.trolleytrackerclient.models.json.Route
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule

interface ScheduleContract {
    interface Presenter {
        fun setView(view: ScheduleContract.View)
        fun getRoutes(routeId: String)
        fun getRouteSchedule()
        fun onDestroy()
    }

    interface View {
        fun getRoutesSuccess(routes: Array<Route>)
        fun getRouteScheduleSuccess(schedule: Array<RouteSchedule>)
        fun getRouteScheduleFailure()
        fun getRoutesFailure()
    }
}