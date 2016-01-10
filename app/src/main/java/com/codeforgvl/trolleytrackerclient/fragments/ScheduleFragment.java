package com.codeforgvl.trolleytrackerclient.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.adapters.ScheduleAdapter;
import com.codeforgvl.trolleytrackerclient.adapters.SimpleSectionedRecyclerViewAdapter;
import com.codeforgvl.trolleytrackerclient.data.TrolleyAPI;
import com.codeforgvl.trolleytrackerclient.models.ScheduledRoute;
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ahodges on 12/21/2015.
 */
public class ScheduleFragment extends Fragment {

   private RouteSchedule[] lastScheduleUpdate;
    private long lastUpdatedAt;

    public static ScheduleFragment newInstance(Bundle args) {
        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleFragment() {
        // Required empty public constructor
    }


    SimpleSectionedRecyclerViewAdapter mSectionedAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();

        if(extras != null){
            processBundle(extras);
        }
    }

    private void processBundle(Bundle b){
        if(b == null){
            return;
        }
        Parcelable[] sParcels = b.getParcelableArray(RouteSchedule.SCHEDULE_KEY);
        lastScheduleUpdate = new RouteSchedule[sParcels.length];
        System.arraycopy(sParcels, 0, lastScheduleUpdate, 0, sParcels.length);

        //Create list of ScheduledRoute objects, count them by day
        mSectionedAdapter = createScheduleViewAdapter(lastScheduleUpdate);

        lastUpdatedAt = b.getLong(RouteSchedule.LAST_UPDATED_KEY);
        DateTime lastUpdate = new DateTime(lastUpdatedAt);
        if(lastUpdate.isBefore(DateTime.now().minusHours(4))){
            new ScheduleUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        processBundle(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(RouteSchedule.SCHEDULE_KEY, lastScheduleUpdate);
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle(R.string.title_fragment_schedule);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        RecyclerView listView = (RecyclerView)view.findViewById(R.id.scheduleList);
        if(listView != null){
            listView.setHasFixedSize(true);
            listView.setAdapter(mSectionedAdapter);
            listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            listView.setLayoutManager(layoutManager);
        }

        return view;
    }

    private SimpleSectionedRecyclerViewAdapter createScheduleViewAdapter(RouteSchedule[] schedule){
        List<ScheduledRoute> srList = new ArrayList<>(schedule.length);
        HashMap<Integer,Integer> dayCount = new HashMap<>(7);
        for(RouteSchedule rs : schedule){
            ScheduledRoute sr = new ScheduledRoute(rs);

            if(!dayCount.containsKey(sr.getDayOfWeek())){
                dayCount.put(sr.getDayOfWeek(), 1);
            } else {
                dayCount.put(sr.getDayOfWeek(), dayCount.get(sr.getDayOfWeek()) + 1);
            }

            srList.add(sr);
        }

        //Insert section headers appropriately
        DateTime now = DateTime.now();
        List<SimpleSectionedRecyclerViewAdapter.Section> sections = new ArrayList<>(7);
        int entryNo = 0;
        for(int day = 1; day < 8; day++){
            String header = now.toString(getString(R.string.schedule_date_format));
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(entryNo, header));
            if(dayCount.containsKey(now.getDayOfWeek())){
                entryNo += dayCount.get(now.getDayOfWeek());
            }

            now = now.withFieldAdded(DurationFieldType.days(), 1);
        }

        //Sort schedule
        Collections.sort(srList);

        //Create adapters
        ScheduleAdapter mAdapter = new ScheduleAdapter(getContext(), srList);
        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter adapter = new SimpleSectionedRecyclerViewAdapter(getContext(), R.layout.view_schedule_header, R.id.section_text, mAdapter);
        adapter.setSections(sections.toArray(dummy));
        return adapter;
    }

    private class ScheduleUpdateTask extends AsyncTask<Void, Void, RouteSchedule[]> {
        @Override
        protected RouteSchedule[] doInBackground(Void... params) {
            Log.d(Constants.LOG_TAG, "requesting schedule update");
            return TrolleyAPI.getRouteSchedule();
        }

        @Override
        protected void onPostExecute(RouteSchedule[] schedule) {
            lastScheduleUpdate = schedule;
            lastUpdatedAt = DateTime.now().getMillis();

            RecyclerView listView = (RecyclerView)getActivity().findViewById(R.id.scheduleList);
            if(listView != null){
                listView.setHasFixedSize(true);
                listView.setAdapter(mSectionedAdapter);
                listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                listView.setLayoutManager(layoutManager);
            }
            createScheduleViewAdapter(schedule);
        }
    }
}