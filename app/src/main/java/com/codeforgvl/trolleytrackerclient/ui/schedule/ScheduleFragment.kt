package com.codeforgvl.trolleytrackerclient.ui.schedule

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.afollestad.materialdialogs.MaterialDialog
import com.codeforgvl.trolleytrackerclient.R
import com.codeforgvl.trolleytrackerclient.Utils
import com.codeforgvl.trolleytrackerclient.activities.MainActivity
import com.codeforgvl.trolleytrackerclient.data.TrolleyData
import com.codeforgvl.trolleytrackerclient.models.ScheduledRoute
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration

import org.joda.time.DateTime

import java.util.ArrayList

import javax.inject.Inject

import dagger.android.support.AndroidSupportInjection

import com.codeforgvl.trolleytrackerclient.models.json.Route
import com.codeforgvl.trolleytrackerclient.ui.schedule.adapter.SchedulesAdapter
import com.codeforgvl.trolleytrackerclient.models.ScheduleItems
import kotlinx.android.synthetic.main.fragment_schedule.*

/**
 * Created by ahodges on 12/21/2015.
 */
class ScheduleFragment
    : Fragment(),
        SwipeRefreshLayout.OnRefreshListener,
        ScheduleContract.View,
        SchedulesAdapter.ScheduleClickedListener {
    //long lastUpdatedAt;
    private var lastScheduleUpdate: Array<RouteSchedule>? = null

    @Inject
    lateinit var presenter: ScheduleContract.Presenter

    private var previewLoadingDialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Icepick.restoreInstanceState<ScheduleFragment>(this, savedInstanceState)
        if (savedInstanceState != null) {
            processBundle(savedInstanceState)
        } else {
            processBundle(arguments)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setView(this)

        scheduleList.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).build())
        scheduleList.layoutManager = LinearLayoutManager(context)

        scheduleRefreshLayout.setOnRefreshListener(this)
    }

    private fun processBundle(b: Bundle?) {
        if (b == null) {
            return
        }
        if (!isHidden) {
            Utils.getActivity(this).setTitle(R.string.title_fragment_schedule)
        }

        if (lastScheduleUpdate == null) {
            lastScheduleUpdate = TrolleyData.getInstance().schedules
        }

        //Create list of ScheduledRoute objects, count them by day
//        mSectionedAdapter = createScheduleViewAdapter(lastScheduleUpdate)

        val lastUpdate = TrolleyData.getInstance().lastScheduleUpdateTime
        if (lastUpdate.isBefore(DateTime.now().minusHours(4))) {
            presenter.getRouteSchedule()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

//        Icepick.saveInstanceState<ScheduleFragment>(this, outState)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            Utils.getActivity(this).setTitle(R.string.title_fragment_schedule)
//            mSectionedAdapter.notifyDataSetChanged()
        }
    }

    override fun onRefresh() {
        presenter.getRouteSchedule()
    }

    override fun onResume() {
        super.onResume()
        presenter.getRouteSchedule()
    }

    private fun getPreviewLoadingDialog(): MaterialDialog? {
        if (previewLoadingDialog == null) {
            previewLoadingDialog =
                    MaterialDialog.Builder(context).title(R.string.preview_loading_title)
                            .progress(true, 0).build()
        }
        return previewLoadingDialog
    }

    override fun itemClicked(scheduledRoute: ScheduledRoute) {
        getPreviewLoadingDialog()?.setContent(
                String.format(
                        context.getString(R.string.preview_loading_content),
                        scheduledRoute.routeSchedule.RouteLongName
                )
        )
        getPreviewLoadingDialog()!!.show()

        presenter.getRoutes(scheduledRoute.routeSchedule.RouteID.toString())
    }

    override fun getRoutesSuccess(routes: Array<Route>) {
        val bundle = Bundle()
        getPreviewLoadingDialog()?.dismiss()
        (activity as MainActivity).showRoutePreview(bundle)
    }

    override fun getRoutesFailure() {
        Toast.makeText(
                activity,
                "Please check your internet connection ",
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun getRouteScheduleSuccess(schedule: ArrayList<ScheduleItems>) {
        if (!isAdded) {
            return
        }

        scheduleList.adapter = SchedulesAdapter(context, this, schedule)
        scheduleList.adapter.notifyDataSetChanged()
        scheduleRefreshLayout.isRefreshing = false
    }

    override fun getRouteScheduleFailure() {
        Toast.makeText(
                activity,
                "Please check your internet connection ",
                Toast.LENGTH_SHORT
        ).show()
    }

    companion object {

        fun newInstance(args: Bundle): ScheduleFragment {
            val fragment = ScheduleFragment()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
