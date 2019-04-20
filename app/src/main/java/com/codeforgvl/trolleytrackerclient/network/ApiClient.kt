package com.codeforgvl.trolleytrackerclient.network

import com.codeforgvl.trolleytrackerclient.models.json.Route
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule
import io.reactivex.Observable

class ApiClient(private val api: ApiService) {

    fun getRouteDetails(routeId: String): Observable<Route> {
        return api.getRouteDetails(routeId)
    }

    fun getRouteSchedule(): Observable<Array<RouteSchedule>> {
        return api.getRouteSchedules()
    }
}