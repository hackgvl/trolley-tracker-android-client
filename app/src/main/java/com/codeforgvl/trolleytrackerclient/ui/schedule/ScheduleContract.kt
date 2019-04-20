package com.codeforgvl.trolleytrackerclient.ui.schedule

import com.codeforgvl.trolleytrackerclient.models.json.Route

interface ScheduleContract {
    interface Presenter {
        fun setView(view: ScheduleContract.View)
        fun getRoutes(routeId: String)
        fun onDestroy()
    }

    interface View {
        fun getRoutesSuccess(routes: Array<Route>)
        fun getRoutesFailure()
    }
}