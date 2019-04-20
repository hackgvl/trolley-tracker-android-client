package com.codeforgvl.trolleytrackerclient.ui.schedule

import com.codeforgvl.trolleytrackerclient.data.TrolleyData
import com.codeforgvl.trolleytrackerclient.network.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
                .subscribe ({
                    TrolleyData.getInstance().schedules = it
                    view.getRouteScheduleSuccess(it)
                }, {
                    view.getRouteScheduleFailure()
                }))
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

}