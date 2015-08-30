package com.codeforgvl.trolleytrackerclient;

import android.util.Log;

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

    public static TrolleyData[] getRunningTrolleys(){
        String json = httpGETRequest(Constants.RUNNING_TROLLEYS_ENDPOINT);
        return new Gson().fromJson(json, TrolleyData[].class);
    }

    public static TrolleyRoute[] getActiveRoutes(){
        String json = httpGETRequest(Constants.ACTIVE_ROUTES_ENDPOINT);
        return new Gson().fromJson(json, TrolleyRoute[].class);
    }

    public static TrolleyRoute getRouteDetails(int routeId){
        String json = httpGETRequest(Constants.GetRouteDetailsEndpoint(routeId));
        return new Gson().fromJson(json, TrolleyRoute.class);
    }
}
