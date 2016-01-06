package com.codeforgvl.trolleytrackerclient.helpers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.data.TrolleyAPI;
import com.codeforgvl.trolleytrackerclient.fragments.MapFragment;
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

import java.util.HashMap;

/**
 * Created by ahodges on 12/18/2015.
 */
public class RouteManager {
    private static final String ROUTE_LAST_UPDATED_KEY = "TROLLEY_LAST_UPDATED_KEY";
    private MapFragment mapFragment;
    private HashMap<Integer, Polyline> routePolylines = new HashMap<>();
    private HashMap<Integer, Marker> stopMarkers = new HashMap<>();

    private Route[] lastRouteUpdate;
    private long lastUpdatedAt;

    public RouteManager(MapFragment activity){
        mapFragment = activity;
    }

    public void processBundle(Bundle b){
        if(b == null){
            return;
        }

        if (b != null){
            lastUpdatedAt = b.getLong(ROUTE_LAST_UPDATED_KEY);
            DateTime lastUpdate = new DateTime(lastUpdatedAt);
            if(lastUpdate.isBefore(DateTime.now().minusMinutes(30))){
                new RouteUpdateTask().execute();
            } else {
                Parcelable[] rParcels = b.getParcelableArray(Route.ROUTE_KEY);
                lastRouteUpdate = new Route[rParcels.length];
                System.arraycopy(rParcels, 0, lastRouteUpdate, 0, rParcels.length);
                updateRoutes(lastRouteUpdate);
            }
        }
    }

    public void startUpdates(){
        //if (mUpdateTask == null || mUpdateTask.isCancelled()){
        //    mUpdateTask = new RouteUpdateTask();
        //    mUpdateTask.execute();
        //}
    }

    public void stopUpdates(){
        //if(mUpdateTask != null){
        //    mUpdateTask.cancel(false);
        //}
    }

    public void updateRoutes(Route[] routes){
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
            routeLine.color(Constants.getRouteColorForRouteNumber(mapFragment.getContext(), i));
            routePolylines.put(r.ID, mapFragment.mMap.addPolyline(routeLine));

            //Add route stops
            for(RouteStop s : r.Stops) {
                stopMarkers.put(s.ID, mapFragment.mMap.addMarker(new MarkerOptions()
                        .title(s.Name)
                        .snippet(s.Description)
                        .anchor(0.5f, 0.5f)
                        .icon(IconFactory.getStopIcon(mapFragment.getContext(), Constants.getStopColorForRouteNumber(mapFragment.getContext(), i)))
                        .position(new LatLng(s.Lat, s.Lon))));
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

            return activeRoutes;
        }

        @Override
        protected void onPostExecute(Route[] routes) {
            lastRouteUpdate = routes;
            updateRoutes(routes);
        }
    }

    public void saveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArray(Route.ROUTE_KEY, lastRouteUpdate);
    }
}
