package com.codeforgvl.trolleytrackerclient.ui.schedule

import com.codeforgvl.trolleytrackerclient.network.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SchedulePresenter
@Inject constructor(private val apiClient: ApiClient) : ScheduleContract.Presenter {

    private lateinit var view: ScheduleContract.View

    private var disposable: Disposable? = null

    override fun setView(view: ScheduleContract.View) {
        this.view = view
    }

    override fun getRoutes(routeId: String) {
        disposable = apiClient.getRouteDetails(routeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    view.getRoutesSuccess(arrayOf(it))
                }, {
                    view.getRoutesFailure()
                })
    }

    override fun onDestroy() {
        disposable?.dispose()
    }

}