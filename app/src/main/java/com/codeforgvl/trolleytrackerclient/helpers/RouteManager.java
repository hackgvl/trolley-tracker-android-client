package com.codeforgvl.trolleytrackerclient.helpers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.data.TrolleyAPI;
import com.codeforgvl.trolleytrackerclient.data.TrolleyData;
import com.codeforgvl.trolleytrackerclient.fragments.IMapFragment;
import com.codeforgvl.trolleytrackerclient.fragments.TrackerFragment;
import com.codeforgvl.trolleytrackerclient.models.json.LatLon;
import com.codeforgvl.trolleytrackerclient.models.json.Route;
import com.codeforgvl.trolleytrackerclient.models.json.RouteStop;
import com.codeforgvl.trolleytrackerclient.models.json.Trolley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.livefront.bridge.Bridge;

import org.joda.time.DateTime;

import java.util.HashMap;

import icepick.State;

import static com.codeforgvl.trolleytrackerclient.Constants.ROUTE_UPDATE_INTERVAL;

/**
 * Created by ahodges on 12/18/2015.
 */
public class RouteManager {
    private IMapFragment trackerFragment;
    private HashMap<Integer, Polyline> routePolylines = new HashMap<>();
    private HashMap<Integer, Marker> stopMarkers = new HashMap<>();

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
            Bridge.restoreInstanceState(trackerFragment, b);
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

    public boolean updateRoutesIfNeeded(){

        if (lastRouteUpdate == null) {
            lastRouteUpdate = TrolleyData.getInstance().getRoutes();
        }


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
                stopMarkers.put(s.ID, trackerFragment.getMap().addMarker(new MarkerOptions()
                        .title(s.Name)
                        .snippet(s.Description)
                        .anchor(0.5f, 0.5f)
                        .icon(IconFactory.getStopIcon(trackerFragment.getContext(), Constants.getStopColorForRouteNumber(trackerFragment.getContext(), i)))
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

            // Recheck all trolleys in case trolley color has changed: when trolley
            // switches to a different route
            Log.d(Constants.LOG_TAG, "requesting full trolley update");
            Trolley[] trolleys = TrolleyAPI.getAllTrolleys();
            TrolleyData.getInstance().setTrolleys(trolleys);

            return activeRoutes;
        }

        @Override
        protected void onPostExecute(Route[] routes) {
            if(!trackerFragment.fragmentIsAdded()){
                return;
            }
            lastRouteUpdate = routes;
            TrolleyData.getInstance().setRoutes(lastRouteUpdate);

            updateRoutes(routes);
        }
    }

    public void saveInstanceState(Bundle savedInstanceState){
        Bridge.saveInstanceState(trackerFragment, savedInstanceState);
    }
}
