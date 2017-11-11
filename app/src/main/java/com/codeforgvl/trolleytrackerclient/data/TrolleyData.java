package com.codeforgvl.trolleytrackerclient.data;

import com.codeforgvl.trolleytrackerclient.models.ScheduledRoute;
import com.codeforgvl.trolleytrackerclient.models.json.Route;
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.models.json.Trolley;

import org.joda.time.DateTime;

/**
 * Created by MikeN on 11/10/2017.
 * Singleton class to store and access objects after downloading from the API.
 * Not persisted when app is swapped out because data is likely stale so it will
 * be re-queried.
 */

public class TrolleyData {

    private Trolley[] trolleys;
    private Route[] routes;
    private RouteSchedule[] schedules;
    private DateTime lastTrolleyUpdateTime;
    private DateTime lastRouteUpdateTime;
    private DateTime lastScheduleUpdateTime;

    private static TrolleyData instance;

    static {
        instance = new TrolleyData();
    }

    private TrolleyData() {
        // Set starting timestamps to "old" so data is refreshed
        DateTime longTimeAgo = DateTime.now().minusDays(2);
        lastTrolleyUpdateTime = longTimeAgo;
        lastRouteUpdateTime = longTimeAgo;
        lastScheduleUpdateTime = longTimeAgo;
    }

    public static TrolleyData getInstance() {
        return TrolleyData.instance;
    }

    public synchronized void setTrolleyData(
            Trolley[] newTrolleys,
            Route[] newRoutes,
            RouteSchedule[] newSchedules
    ) {
        trolleys = newTrolleys;
        routes = newRoutes;
        schedules = newSchedules;
        lastTrolleyUpdateTime = DateTime.now();
        lastRouteUpdateTime = DateTime.now();
        lastScheduleUpdateTime = DateTime.now();
    }

    public synchronized Trolley[] getTrolleys() {
        return trolleys;
    }
    public synchronized void setTrolleys(Trolley[] newTrolleys) {
        trolleys = newTrolleys;
        lastTrolleyUpdateTime = DateTime.now();
    }

    public synchronized Route[] getRoutes() {
        return routes;
    }
    public synchronized void setRoutes(Route[] newRoutes) {
        routes = newRoutes;
        lastRouteUpdateTime = DateTime.now();
    }

    public synchronized RouteSchedule[] getSchedules() {
        return schedules;
    }
    public synchronized void setSchedules(RouteSchedule[] newSchedules) {
        schedules = newSchedules;
        lastScheduleUpdateTime = DateTime.now();
    }

    public synchronized DateTime getLastTrolleyUpdateTime() {
        return lastTrolleyUpdateTime;
    }
    public synchronized DateTime getLastRouteUpdateTime() {
        return lastRouteUpdateTime;
    }
    public synchronized DateTime getLastScheduleUpdateTime() {
        return lastScheduleUpdateTime;
    }
}
