package com.codeforgvl.trolleytrackerclient.models;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.Utils;
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.ui.schedule.adapter.SchedulesAdapter;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Interval;
import org.joda.time.Partial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by ahodges on 12/29/2015.
 */
public class ScheduledRoute extends ScheduleItems implements Comparable {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormat.forPattern("EEEE h:m a").withLocale(Locale.ENGLISH);

    private Interval interval;
    private RouteSchedule schedule;
    private int dayOfWeek;

    public ScheduledRoute(RouteSchedule rs) {
        schedule = rs;

        DateTime now = DateTime.now();
        Partial dayPartial = new Partial().with(DateTimeFieldType.dayOfWeek(), Constants.DayOfWeek.valueOf(rs.DayOfWeek).ordinal() + 1);
        DateTime startDay = Utils.rollForwardWith(now, dayPartial);

        DateTime schedStartDate = DateTime.parse(rs.DayOfWeek + " " + rs.StartTime, INPUT_DATE_FORMAT);
        DateTime schedEndDate = DateTime.parse(rs.DayOfWeek + " " + rs.EndTime, INPUT_DATE_FORMAT);

        DateTime nextStart = startDay
                .withTime(schedStartDate.getHourOfDay(), schedStartDate.getMinuteOfHour(), 0, 0);
        DateTime nextEnd = startDay
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

    @NotNull
    @Override
    public SchedulesAdapter.Type getType() {
        return SchedulesAdapter.Type.SCHEDULE;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof ScheduledRoute){
            ScheduledRoute other = (ScheduledRoute)o;
            return interval.getStart().compareTo(other.getInterval().getStart());
        }
        return 0;
    }
}
