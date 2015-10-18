package com.codeforgvl.trolleytrackerclient;

import com.codeforgvl.trolleytrackerclient.data.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.data.Trolley;
import com.codeforgvl.trolleytrackerclient.data.Route;
import com.google.gson.Gson;

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

    public static Trolley[] getRunningTrolleys(){
        String json = httpGETRequest(Constants.RUNNING_TROLLEYS_ENDPOINT);
        return new Gson().fromJson(json, Trolley[].class);
    }

    public static Route[] getActiveRoutes(){
        String json = httpGETRequest(Constants.ACTIVE_ROUTES_ENDPOINT);
        return new Gson().fromJson(json, Route[].class);
    }

    public static RouteSchedule[] getRouteSchedule(){
        String json = httpGETRequest(Constants.ROUTE_SCHEDULE_ENDPOINT);
        return new Gson().fromJson(json, RouteSchedule[].class);
    }

    public static Route getRouteDetails(int routeId){
        String json = httpGETRequest(Constants.GetRouteDetailsEndpoint(routeId));
        return new Gson().fromJson(json, Route.class);
    }
}
