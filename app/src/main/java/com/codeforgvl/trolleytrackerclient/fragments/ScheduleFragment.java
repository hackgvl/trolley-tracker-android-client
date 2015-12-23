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
import com.codeforgvl.trolleytrackerclient.models.RouteSchedule;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
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

    ScheduleAdapter mAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();

        if(extras != null){
            Parcelable[] sParcels = extras.getParcelableArray(RouteSchedule.SCHEDULE_KEY);
            List<RouteSchedule> rsList = new ArrayList<>(sParcels.length);
            for(Parcelable s : sParcels){
                rsList.add((RouteSchedule)s);
            }
            mAdapter = new ScheduleAdapter(rsList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        RecyclerView listView = (RecyclerView)view.findViewById(R.id.scheduleList);
        if(listView != null){
            listView.setAdapter(mAdapter);
            listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());
            listView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return view;
    }
}
