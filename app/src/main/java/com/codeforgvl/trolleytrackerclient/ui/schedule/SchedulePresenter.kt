package com.codeforgvl.trolleytrackerclient.ui.schedule

import com.codeforgvl.trolleytrackerclient.data.TrolleyData
import com.codeforgvl.trolleytrackerclient.models.ScheduledRoute
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule
import com.codeforgvl.trolleytrackerclient.network.ApiClient
import com.codeforgvl.trolleytrackerclient.models.Day
import com.codeforgvl.trolleytrackerclient.models.ScheduleItems
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.DurationFieldType
import java.util.HashMap
import javax.inject.Inject

class SchedulePresenter
@Inject constructor(private val apiClient: ApiClient) : ScheduleContract.Presenter {

    private lateinit var view: ScheduleContract.View

    private var compositeDisposable = CompositeDisposable()

    override fun setView(view: ScheduleContract.View) {
        this.view = view
    }

    override fun getRoutes(routeId: String) {
        compositeDisposable.add(apiClient.getRouteDetails(routeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    val routes = arrayOf(it)
                    TrolleyData.getInstance().routes = routes
                    view.getRoutesSuccess(routes)
                }, {
                    view.getRoutesFailure()
                }))
    }

    override fun getRouteSchedule() {
        compositeDisposable.add(apiClient.getRouteSchedule()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    TrolleyData.getInstance().schedules = it
                    addDays(it)
                }
                .subscribe ({
                    view.getRouteScheduleSuccess(it)
                }, {
                    view.getRouteScheduleFailure()
                }))
    }

    private fun addDays(routeSchedules: Array<RouteSchedule>): ArrayList<ScheduleItems>{
        val srList = java.util.ArrayList<ScheduledRoute>(routeSchedules.size)
        val dayCount = HashMap<Int, Int>(7)
        for (rs in routeSchedules) {
            val sr = ScheduledRoute(rs)

            if (!dayCount.containsKey(sr.dayOfWeek)) {
                dayCount[sr.dayOfWeek] = 1
            } else {
                dayCount[sr.dayOfWeek] = dayCount[sr.dayOfWeek]!! + 1
            }

            srList.add(sr)
        }
        srList.sort()

        val list = ArrayList<ScheduleItems>()

        //Insert section headers appropriately
        var now = DateTime.now()
        var entryNo = 0
        for (day in 1..7) {
            val day = Day(now.toString("EEEE M/d"))
            if (dayCount.containsKey(now.dayOfWeek)) {
                list.add(day)
                entryNo += dayCount[now.dayOfWeek]!!
                for (sr in srList) {
                    if (sr.dayOfWeek == now.dayOfWeek().get()) {
                        list.add(sr)
                    }
                }
            }
            now = now.withFieldAdded(DurationFieldType.days(), 1)
        }

        return list
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

}