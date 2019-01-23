package com.codeforgvl.trolleytrackerclient.network

import com.codeforgvl.trolleytrackerclient.models.json.Route
import io.reactivex.Observable

class ApiClient(private val api: ApiService) {


    fun getRouteDetails(routeId: String): Observable<Route> {
        return api.getRouteDetails(routeId)
    }
}