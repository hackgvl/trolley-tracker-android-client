package com.codeforgvl.trolleytrackerclient;

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

    public static final int SLEEP_INTERVAL = 5000;
}