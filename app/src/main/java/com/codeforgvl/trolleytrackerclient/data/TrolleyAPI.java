package com.codeforgvl.trolleytrackerclient.data;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.models.json.Trolley;
import com.codeforgvl.trolleytrackerclient.models.json.Route;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class TrolleyAPI {
    private static String httpGETRequest(String urlString){
        StringBuilder builder = new StringBuilder();

        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.connect();

            int status = connection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        builder.append(line+"\n");
                    }
                    br.close();
            }

        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
        }

        return builder.toString();
    }

    // Location only for the trolleys that are running
    public static Trolley[] getRunningTrolleys(){
        String json = httpGETRequest(Constants.RUNNING_TROLLEYS_ENDPOINT);
        Trolley[] ret = null;
        try{
            ret = new Gson().fromJson(json, Trolley[].class);
        } catch (JsonSyntaxException ex){
            //Catch exception re: malformed json from API
        }
        return ret != null? ret : new Trolley[0];
    }


    // Get full record for all trolleys
    public static Trolley[] getAllTrolleys(){
        String json = httpGETRequest(Constants.ALL_TROLLEYS_ENDPOINT);
        Trolley[] ret = null;
        try{
            ret = new Gson().fromJson(json, Trolley[].class);
        } catch (JsonSyntaxException ex){
            //Catch exception re: malformed json from API
        }
        return ret != null? ret : new Trolley[0];
    }

    public static Route[] getActiveRoutes(){
        String json = httpGETRequest(Constants.ACTIVE_ROUTES_ENDPOINT);
        Route[] ret = null;
        try{
            ret = new Gson().fromJson(json, Route[].class);
        } catch (JsonSyntaxException ex){
            //Catch exception re: malformed json from API
        }
        return ret != null? ret : new Route[0];
    }

    public static RouteSchedule[] getRouteSchedule(){
        String json = httpGETRequest(Constants.ROUTE_SCHEDULE_ENDPOINT);
        RouteSchedule[] ret = null;
        try{
            ret = new Gson().fromJson(json, RouteSchedule[].class);
        } catch (JsonSyntaxException ex){
            //Catch exception re: malformed json from API
        }
        return ret != null? ret : new RouteSchedule[0];
    }

    public static Route getRouteDetails(int routeId){
        String json = httpGETRequest(Constants.GetRouteDetailsEndpoint(routeId));
        Route ret = null;
        try{
            ret = new Gson().fromJson(json, Route.class);
        } catch (JsonSyntaxException ex){
            //Catch exception re: malformed json from API
        }
        return ret != null? ret : new Route();
    }
}
