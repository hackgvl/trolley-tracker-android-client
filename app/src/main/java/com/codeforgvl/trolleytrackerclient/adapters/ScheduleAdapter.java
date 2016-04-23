package com.codeforgvl.trolleytrackerclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.models.ScheduledRoute;
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule;

import java.util.List;

/**
 * Created by Adam Hodges on 12/23/2015.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final Context mContext;
    private List<ScheduledRoute> mSchedules;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public View parentView;
        public TextView timeTextView;
        public TextView routeNameView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            parentView = itemView;
            timeTextView = (TextView) itemView.findViewById(R.id.scheduleTime);
            routeNameView = (TextView) itemView.findViewById(R.id.routeName);
        }
    }

    public ScheduleAdapter(Context context, List<ScheduledRoute> schedules){
        mContext = context;
        mSchedules = schedules;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View scheduleView = inflater.inflate(R.layout.view_schedule_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(scheduleView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ScheduleAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ScheduledRoute sr = mSchedules.get(position);
        RouteSchedule rs = sr.getRouteSchedule();

        // Set item views based on the data model
        viewHolder.timeTextView.setText(String.format(viewHolder.parentView.getContext().getString(R.string.schedule_time_format), rs.StartTime, rs.EndTime));
        viewHolder.routeNameView.setText(rs.RouteLongName);

        viewHolder.parentView.setSelected(sr.getInterval().containsNow());
        viewHolder.parentView.setEnabled(!sr.getInterval().getEnd().isBeforeNow());
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    public ScheduledRoute getItem(int position){
        return mSchedules.get(position);
    }
}
