package com.codeforgvl.trolleytrackerclient.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.afollestad.materialdialogs.MaterialDialog
import com.codeforgvl.trolleytrackerclient.Constants
import com.codeforgvl.trolleytrackerclient.R
import com.codeforgvl.trolleytrackerclient.Utils
import com.codeforgvl.trolleytrackerclient.activities.MainActivity
import com.codeforgvl.trolleytrackerclient.adapters.ScheduleAdapter
import com.codeforgvl.trolleytrackerclient.adapters.SimpleSectionedRecyclerViewAdapter
import com.codeforgvl.trolleytrackerclient.data.TrolleyAPI
import com.codeforgvl.trolleytrackerclient.data.TrolleyData
import com.codeforgvl.trolleytrackerclient.helpers.RecyclerItemClickListener
import com.codeforgvl.trolleytrackerclient.models.ScheduledRoute
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule
import com.codeforgvl.trolleytrackerclient.network.ApiClient
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration

import org.joda.time.DateTime
import org.joda.time.DurationFieldType

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

import javax.inject.Inject

import dagger.android.support.AndroidSupportInjection
import icepick.Icepick
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

import android.content.Context.CONNECTIVITY_SERVICE
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by ahodges on 12/21/2015.
 */
class ScheduleFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    internal var lastScheduleUpdate: Array<RouteSchedule>? = null
    //long lastUpdatedAt;

    @Inject
    lateinit var apiClient: ApiClient

    private lateinit var mSectionedAdapter: SimpleSectionedRecyclerViewAdapter

    private var previewLoadingDialog: MaterialDialog? = null
    val isConnected: Boolean
        get() {
            val manager = activity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return info != null && info.isConnected
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Icepick.restoreInstanceState<ScheduleFragment>(this, savedInstanceState)
        if (savedInstanceState != null) {
            processBundle(savedInstanceState)
        } else {
            processBundle(arguments)
        }
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
        mSectionedAdapter = createScheduleViewAdapter(lastScheduleUpdate)

        val lastUpdate = TrolleyData.getInstance().lastScheduleUpdateTime
        if (lastUpdate.isBefore(DateTime.now().minusHours(4))) {
            ScheduleUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement MapFragmentListener")
        }

        AndroidSupportInjection.inject(this)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

//        Icepick.saveInstanceState<ScheduleFragment>(this, outState)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            Utils.getActivity(this).setTitle(R.string.title_fragment_schedule)
            mSectionedAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_schedule, container, false)

        val listView = view.findViewById<View>(R.id.scheduleList) as RecyclerView
        if (listView != null) {
            listView.setHasFixedSize(true)
            listView.adapter = mSectionedAdapter
            listView.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).build())
            val layoutManager = LinearLayoutManager(context)
            listView.layoutManager = layoutManager
            listView.addOnItemTouchListener(
                RecyclerItemClickListener(
                    context,
                    ScheduleItemClickListener()
                )
            )
        }

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.scheduleRefreshLayout)
        swipeRefresh?.setOnRefreshListener(this)

        return view
    }

    private fun createScheduleViewAdapter(schedule: Array<RouteSchedule>?): SimpleSectionedRecyclerViewAdapter {
        val srList = ArrayList<ScheduledRoute>(schedule?.size ?: 0)
        val dayCount = HashMap<Int, Int>(7)
        if (schedule != null) {
            for (rs in schedule) {
                val sr = ScheduledRoute(rs)

                if (!dayCount.containsKey(sr.dayOfWeek)) {
                    dayCount[sr.dayOfWeek] = 1
                } else {
                    dayCount[sr.dayOfWeek] = dayCount[sr.dayOfWeek]!! + 1
                }

                srList.add(sr)
            }
        }

        //Insert section headers appropriately
        var now = DateTime.now()
        val sections = ArrayList<SimpleSectionedRecyclerViewAdapter.Section>(7)
        var entryNo = 0
        for (day in 1..7) {
            val header = now.toString(getString(R.string.schedule_date_format))
            sections.add(SimpleSectionedRecyclerViewAdapter.Section(entryNo, header))
            if (dayCount.containsKey(now.dayOfWeek)) {
                entryNo += dayCount[now.dayOfWeek]!!
            }

            now = now.withFieldAdded(DurationFieldType.days(), 1)
        }

        //Sort schedule
        Collections.sort(srList)

        //Create adapters
        val mAdapter = ScheduleAdapter(context, srList)
        val adapter = SimpleSectionedRecyclerViewAdapter(
            context,
            R.layout.view_schedule_header,
            R.id.section_text,
            mAdapter
        )
        adapter.setSections(sections.toTypedArray())
        return adapter
    }

    override fun onRefresh() {
        ScheduleUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun getPreviewLoadingDialog(): MaterialDialog? {
        if (previewLoadingDialog == null) {
            previewLoadingDialog =
                MaterialDialog.Builder(context).title(R.string.preview_loading_title)
                    .progress(true, 0).build()
        }
        return previewLoadingDialog
    }

    private inner class ScheduleItemClickListener : RecyclerItemClickListener.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            //Show map preview if they clicked on a scheduled time
            if (isConnected) {
                if (getPreviewLoadingDialog()?.isShowing != true) {
                    val itemPosition = mSectionedAdapter.getItemId(position)
                    if (itemPosition < Integer.MAX_VALUE / 2) {
                        val route =
                            (mSectionedAdapter.baseAdapter as ScheduleAdapter).getItem(itemPosition.toInt())
                        getPreviewLoadingDialog()?.setContent(
                            String.format(
                                context.getString(R.string.preview_loading_content),
                                route.routeSchedule.RouteLongName
                            )
                        )
                        getPreviewLoadingDialog()!!.show()


                        val disposable = apiClient.getRouteDetails(route.routeSchedule.RouteID.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {it ->
                                if (isAdded) {
                                    val routes = arrayOf(it)
                                    val bundle = Bundle()
                                    TrolleyData.getInstance().routes = routes

                                    getPreviewLoadingDialog()?.dismiss()
                                    (activity as MainActivity).showRoutePreview(bundle)
                                }
                            }
                    }
                }
            } else {
                Toast.makeText(
                    activity,
                    "Please check your internet connection ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private inner class ScheduleUpdateTask : AsyncTask<Void, Void, Array<RouteSchedule>>() {
        override fun doInBackground(vararg params: Void): Array<RouteSchedule> {

            Log.d(Constants.LOG_TAG, "requesting schedule update")
            return TrolleyAPI.getRouteSchedule()
        }

        override fun onPostExecute(schedule: Array<RouteSchedule>) {
            if (!isAdded) {
                return
            }

            lastScheduleUpdate = schedule
            TrolleyData.getInstance().schedules = lastScheduleUpdate


            mSectionedAdapter = createScheduleViewAdapter(schedule)
            val listView = activity.findViewById<RecyclerView>(R.id.scheduleList)

            if (listView != null) {
                listView.adapter = mSectionedAdapter
            }


            (activity.findViewById<View>(R.id.scheduleRefreshLayout) as SwipeRefreshLayout).isRefreshing =
                false
        }
    }

    companion object {

        fun newInstance(args: Bundle): ScheduleFragment {
            val fragment = ScheduleFragment()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
