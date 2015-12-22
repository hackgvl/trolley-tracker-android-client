package com.codeforgvl.trolleytrackerclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.models.RouteSchedule;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;

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

    ArrayList<Card> schedule = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();

        if(extras != null){
            Parcelable[] sParcels = extras.getParcelableArray(RouteSchedule.SCHEDULE_KEY);
            for(Parcelable s : sParcels){
                RouteSchedule rs = (RouteSchedule)s;
                Card card = new Card(getContext());
                CardHeader header = new CardHeader(getContext());
                header.setTitle(String.format(getString(R.string.schedule_title), rs.DayOfWeek, rs.StartTime, rs.EndTime));
                card.addCardHeader(header);
                schedule.add(card);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getContext(), schedule);
        CardListView listView = (CardListView)view.findViewById(R.id.scheduleList);
        if(listView != null){
            listView.setAdapter(mCardArrayAdapter);
        }

        return view;
    }
}
