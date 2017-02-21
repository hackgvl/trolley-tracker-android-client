package com.codeforgvl.trolleytrackerclient;

import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class Constants {
    public static final String LOG_TAG = "TROLLEYTRACKER";
    public static final String HOST = BuildConfig.DEBUG ? "yeahthattrolley.azurewebsites.net" : "api.yeahthattrolley.com";
    public static final String API_PATH = "/api/v1/";
    public static final String RUNNING_TROLLEYS_ENDPOINT = "http://" + HOST + API_PATH + "Trolleys/Running";
    public static final String ACTIVE_ROUTES_ENDPOINT = "http://" + HOST + API_PATH + "Routes/Active";
    public static final String ROUTE_SCHEDULE_ENDPOINT = "http://" + HOST + API_PATH + "RouteSchedules";

    private static final String ROUTE_DETAILS_ENDPOINT = "http://" + HOST + API_PATH + "Routes/";
    public static String GetRouteDetailsEndpoint(int routeId) {
        return ROUTE_DETAILS_ENDPOINT + routeId;
    }

    public static final int ROUTE_UPDATE_INTERVAL = 15;

    public static final int SLEEP_INTERVAL = 5000;

    public static final int LOCATION_PERMISSION_REQUEST_ID = 1;

    public static int getRouteColorForRouteNumber(Context context, int ndx){

        int routeNo = (ndx % 5) + 1;
        switch (routeNo){
            case 1:
                return ContextCompat.getColor(context, R.color.route1);
            case 2:
                return ContextCompat.getColor(context, R.color.route2);
            case 3:
                return ContextCompat.getColor(context, R.color.route3);
            case 4:
                return ContextCompat.getColor(context, R.color.route4);
            default:
                return ContextCompat.getColor(context, R.color.route5);
        }
    }

    public static int getStopColorForRouteNumber(Context context, int ndx){
        int routeNo = (ndx % 5) + 1;
        switch (routeNo){
            case 1:
                return ContextCompat.getColor(context, R.color.stop1);
            case 2:
                return ContextCompat.getColor(context, R.color.stop2);
            case 3:
                return ContextCompat.getColor(context, R.color.stop3);
            case 4:
                return ContextCompat.getColor(context, R.color.stop4);
            default:
                return ContextCompat.getColor(context, R.color.stop5);
        }
    }

    public enum DayOfWeek {
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday
    }
}