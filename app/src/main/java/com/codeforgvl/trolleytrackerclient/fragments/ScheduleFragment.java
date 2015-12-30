package com.codeforgvl.trolleytrackerclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.adapters.ScheduleAdapter;
import com.codeforgvl.trolleytrackerclient.adapters.SimpleSectionedRecyclerViewAdapter;
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
            Parcelable[] sParcels = extras.getParcelableArray(RouteSchedule.SCHEDULE_KEY);

            //Create list of ScheduledRoute objects, count them by day
            List<ScheduledRoute> srList = new ArrayList<>(sParcels.length);
            HashMap<Integer,Integer> dayCount = new HashMap<>(7);
            for(Parcelable s : sParcels){
                RouteSchedule rs = (RouteSchedule)s;
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
            mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(getContext(), R.layout.view_schedule_header, R.id.section_text, mAdapter);
            mSectionedAdapter.setSections(sections.toArray(dummy));
        }
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
}
