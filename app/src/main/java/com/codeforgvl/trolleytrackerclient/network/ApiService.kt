package com.codeforgvl.trolleytrackerclient.network

import com.codeforgvl.trolleytrackerclient.models.json.Route
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule
import com.codeforgvl.trolleytrackerclient.models.json.Trolley
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/api/v1/Trolleys/Running")
    fun getRunningTrolleys(): Observable<Result<ArrayList<Trolley>>>

    @GET("/api/v1/Trolleys")
    fun getAllTrolleys(): Observable<ArrayList<Trolley>>

    @GET("/api/v1/Routes/Active")
    fun getActiveRoutes(): Observable<ArrayList<Route>>

    @GET("/api/v1/RouteSchedules")
    fun getRouteSchedules(): Observable<ArrayList<RouteSchedule>>

    @GET("/api/v1/Routes/{id}")
    fun getRouteDetails(@Path("id") routeId: String): Observable<Route>
}
