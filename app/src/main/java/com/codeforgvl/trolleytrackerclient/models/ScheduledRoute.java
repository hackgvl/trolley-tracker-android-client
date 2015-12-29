package com.codeforgvl.trolleytrackerclient.models;

import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by ahodges on 12/29/2015.
 */
public class ScheduledRoute implements Comparable {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormat.forPattern("EEEE h:m a");

    private Interval interval;
    private RouteSchedule schedule;
    private int dayOfWeek;

    public ScheduledRoute(RouteSchedule rs) {
        schedule = rs;

        DateTime schedStartDate = DateTime.parse(rs.DayOfWeek + " " + rs.StartTime, INPUT_DATE_FORMAT);
        DateTime schedEndDate = DateTime.parse(rs.DayOfWeek + " " + rs.EndTime, INPUT_DATE_FORMAT);

        DateTime nextStart = DateTime.now()
                .withDayOfWeek(schedStartDate.dayOfWeek().get())
                .withTime(schedStartDate.getHourOfDay(), schedStartDate.getMinuteOfHour(), 0, 0);
        DateTime nextEnd = DateTime.now()
                .withDayOfWeek(schedEndDate.dayOfWeek().get())
                .withTime(schedEndDate.getHourOfDay(), schedEndDate.getMinuteOfHour(), 0, 0);

        dayOfWeek = nextStart.getDayOfWeek();
        interval = new Interval(nextStart, nextEnd);
    }
    public Interval getInterval(){
        return interval;
    }
    public RouteSchedule getRouteSchedule(){
        return schedule;
    }
    public int getDayOfWeek(){
        return dayOfWeek;
    }

    public String getStartDate(){
        return interval.getStart().toString("M/d");
    }

    @Override
    public int compareTo(Object o) {
        if(o.getClass() == ScheduledRoute.class){
            ScheduledRoute other = (ScheduledRoute)o;
            return interval.getStart().compareTo(other.getInterval().getStart());
        }
        return 0;
    }
}
