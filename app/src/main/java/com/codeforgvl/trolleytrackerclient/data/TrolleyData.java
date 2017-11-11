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

    private Trolley[] trolleys; // Full trolley record definition, including running attributes
    private Route[] routes;
    private RouteSchedule[] schedules;
    private DateTime lastTrolleyUpdateTime;
    private DateTime lastRouteUpdateTime;
    private DateTime lastScheduleUpdateTime;
    private boolean trolleyColorsUpdated;  // Full trolley update; trolley color may have changed

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

    // Return if trolley colors have changed
    //  NOTE: Side effect - clears flag upon call
    public boolean TrolleyColorUpdated() {
        boolean wasUpdated = trolleyColorsUpdated;
        trolleyColorsUpdated = false;
        return wasUpdated;
    }

    public synchronized void setTrolleyData(
            Trolley[] newTrolleys,  // Full data record
            Route[] newRoutes,
            RouteSchedule[] newSchedules
    ) {
        trolleys = newTrolleys;
        routes = newRoutes;
        schedules = newSchedules;
        lastTrolleyUpdateTime = DateTime.now();
        lastRouteUpdateTime = DateTime.now();
        lastScheduleUpdateTime = DateTime.now();
        trolleyColorsUpdated = true;
    }

    public synchronized Trolley[] getTrolleys() {
        return trolleys;
    }

    // Replace trolley array with new full trolley array
    // and reapply running definitions
    public synchronized void setTrolleys(Trolley[] newTrolleys) {
        trolleyColorsUpdated = false;
        if (trolleys != null) {
            if (trolleys.length != newTrolleys.length)
                trolleyColorsUpdated = true;
            else {
                // Trolley order is always same from server if nothing added or removed
                for (int i=0; i<trolleys.length; i++) {
                    newTrolleys[i].Running = trolleys[i].Running;
                    if (!trolleys[i].IconColorRGB.equals(newTrolleys[i].IconColorRGB)) {
                        trolleyColorsUpdated = true;
                    }
                }

            }
        }
        trolleys = newTrolleys;
        lastTrolleyUpdateTime = DateTime.now();

    }
    // Update location only and running status for running trolleys
    public synchronized void setRunningTrolleyLocations(Trolley[] newTrolleyLocations) {

        if (trolleys == null) return;  // No data downloaded yet

        // Keep track of running trolleys so that non-running trolleys can be marked as not running
        boolean[] wasRunning = new boolean[trolleys.length];

        for (int i=0; i<newTrolleyLocations.length; i++) {
            for (int j=0; j < trolleys.length; j++) {
                if (newTrolleyLocations[i].ID == trolleys[j].ID) {
                    trolleys[j].Lat = newTrolleyLocations[i].Lat;
                    trolleys[j].Lon = newTrolleyLocations[i].Lon;
                    trolleys[j].Running = true;
                    wasRunning[j] = true;
                    break;
                }
            }
        }

        // Ensure that inactive trolleys are not marked as running
        for (int i=0; i< wasRunning.length; i++) {
            if (!wasRunning[i]) {
                trolleys[i].Running = false;
            }
        }

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
