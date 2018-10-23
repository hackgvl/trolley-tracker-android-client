package com.codeforgvl.trolleytrackerclient.helpers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.data.TrolleyAPI;
import com.codeforgvl.trolleytrackerclient.data.TrolleyData;
import com.codeforgvl.trolleytrackerclient.fragments.IMapFragment;
import com.codeforgvl.trolleytrackerclient.models.json.LatLon;
import com.codeforgvl.trolleytrackerclient.models.json.Route;
import com.codeforgvl.trolleytrackerclient.models.json.RouteStop;
import com.codeforgvl.trolleytrackerclient.models.json.Trolley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import icepick.Icepick;

import static com.codeforgvl.trolleytrackerclient.Constants.ROUTE_UPDATE_INTERVAL;

/**
 * Created by ahodges on 12/18/2015.
 */
public class RouteManager {
    private IMapFragment trackerFragment;
    private HashMap<Integer, Polyline> routePolylines = new HashMap<>();
    private HashMap<Integer, Marker> stopMarkers = new HashMap<>();
    private ArrayList<RouteStop> orderedStops = new ArrayList<>();

    Route[] lastRouteUpdate;
    //long lastUpdatedAt;

    public RouteManager(IMapFragment activity){
        trackerFragment = activity;
    }

    public void processBundle(Bundle b){
        if(b == null){
            return;
        }

        if (b != null){
            Icepick.restoreInstanceState(trackerFragment, b);
            if(!updateRoutesIfNeeded()){
                updateRoutes(lastRouteUpdate);
            }
        }
    }

    public void onMapReady(){
        if(lastRouteUpdate != null){
            updateRoutes(lastRouteUpdate);
        }
    }

    public void updateActiveRoutes(){
        new RouteUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void updateETAs(){
        //Find current location of trolley
        HashMap<Integer, RouteStop> trolleyLastStops = new HashMap<>();
        HashMap<Integer, DateTime> prevStopTrolleyVisits = new HashMap<>();
        RouteStop prevStop = null;
        for(RouteStop stop : orderedStops){
            for(Integer trolleyID : stop.LastTrolleyArrivalTimes.keySet()){
                if (prevStopTrolleyVisits.containsKey(trolleyID)){
                    DateTime prev = prevStopTrolleyVisits.get(trolleyID);
                    if (prev.isAfter(stop.LastTrolleyArrivalTimes.get(trolleyID))){
                        //This trolley was last at the previous stop.
                        trolleyLastStops.put(trolleyID, prevStop);
                    }
                } else {
                    prevStopTrolleyVisits.put(trolleyID, stop.LastTrolleyArrivalTimes.get(trolleyID);
                }

                prevStop = stop;
            }
        }

        for(RouteStop stop : orderedStops){
            Integer stopIndex = orderedStops.indexOf(stop); //TODO: optimize
            for(Integer trolleyID : trolleyLastStops.keySet()){
                Integer lastSeenIndex = orderedStops.indexOf(trolleyLastStops.get(trolleyID));

                Interval eta = null;
                //Loop backwards through stops, accumulating interval time
                //Use lastRouteUpdate to adjust ETA
            }
        }


    }

    public boolean updateRoutesIfNeeded(){

        lastRouteUpdate = TrolleyData.getInstance().getRoutes();
        DateTime lastUpdate =  TrolleyData.getInstance().getLastRouteUpdateTime();
        DateTime now = DateTime.now();

        boolean intervalElapsed = lastUpdate.isBefore(now.minusMinutes(ROUTE_UPDATE_INTERVAL));
        boolean periodCrossed = lastUpdate.minuteOfHour().get() % ROUTE_UPDATE_INTERVAL != now.minuteOfHour().get() % ROUTE_UPDATE_INTERVAL;
        if(intervalElapsed || periodCrossed){
            updateActiveRoutes();
            return true;
        } else {
            return false;
        }
    }

    public void updateRoutes(Route[] routes){
        if(trackerFragment.getMap() == null || !trackerFragment.fragmentIsAdded())
            return;

        //Clear all current routes
        for(Marker m : stopMarkers.values()){
            m.remove();
        }
        for(Polyline p : routePolylines.values()){
            p.remove();
        }

        //Add new active routes
        for(int i=0; i<routes.length; i++){
            Route r = routes[i];

            //Add route shape
            PolylineOptions routeLine = new PolylineOptions();
            for(LatLon p : r.RouteShape){
                routeLine.add(new LatLng(p.Lat,p.Lon));
            }
            routeLine.color(Color.parseColor(r.getRouteColorRGB()));
            routePolylines.put(r.ID, trackerFragment.getMap().addPolyline(routeLine));

            //Add route stops
            for(RouteStop s : r.Stops) {
                Marker stop = trackerFragment.getMap().addMarker(new MarkerOptions()
                        .title(s.Name)
                        .snippet(s.Description)
                        .anchor(0.5f, 0.5f)
                        .icon(IconFactory.getStopIcon(trackerFragment.getContext(), Constants.getStopColorForRouteNumber(trackerFragment.getContext(), i)))
                        .position(new LatLng(s.Lat, s.Lon)));
                stopMarkers.put(s.ID, stop);
            }
        }
    }

    private class RouteUpdateTask extends AsyncTask<Void, Void, Route[]> {
        @Override
        protected Route[] doInBackground(Void... params) {
            Log.d(Constants.LOG_TAG, "requesting route update");

            Route[] activeRoutes = TrolleyAPI.getActiveRoutes();
            if(activeRoutes.length > 0){
                for(Route route : activeRoutes){
                    Route details = TrolleyAPI.getRouteDetails(route.ID);
                    route.RouteShape = details.RouteShape;
                    route.Stops = details.Stops;
                }
            }

            // Recheck all trolleys in case trolley color has changed: when trolley
            // switches to a different route
            Log.d(Constants.LOG_TAG, "requesting full trolley update");
            Trolley[] trolleys = TrolleyAPI.getAllTrolleys();
            TrolleyData.getInstance().setTrolleys(trolleys);

            return activeRoutes;
        }

        @Override
        protected void onPostExecute(Route[] routes) {
            if(!trackerFragment.fragmentIsAdded() || !trackerFragment.fragmentIsVisible()){
                return;
            }
            lastRouteUpdate = routes;
            TrolleyData.getInstance().setRoutes(lastRouteUpdate);


            updateRoutes(routes);
        }
    }

    public void saveInstanceState(Bundle savedInstanceState){
        Icepick.saveInstanceState(trackerFragment, savedInstanceState);
    }
}
