package com.codeforgvl.trolleytrackerclient.helpers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.data.TrolleyAPI;
import com.codeforgvl.trolleytrackerclient.fragments.IMapFragment;
import com.codeforgvl.trolleytrackerclient.fragments.TrackerFragment;
import com.codeforgvl.trolleytrackerclient.models.json.LatLon;
import com.codeforgvl.trolleytrackerclient.models.json.Route;
import com.codeforgvl.trolleytrackerclient.models.json.RouteStop;
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
    private IMapFragment trackerFragment;
    private HashMap<Integer, Polyline> routePolylines = new HashMap<>();
    private HashMap<Integer, Marker> stopMarkers = new HashMap<>();

    private Route[] lastRouteUpdate;
    private long lastUpdatedAt;

    public RouteManager(IMapFragment activity){
        trackerFragment = activity;
    }

    public void processBundle(Bundle b){
        if(b == null){
            return;
        }

        if (b != null){
            lastUpdatedAt = b.getLong(Route.LAST_UPDATED_KEY);
            if(!updateRoutesIfNeeded()){
                Parcelable[] rParcels = b.getParcelableArray(Route.ROUTE_KEY);
                lastRouteUpdate = new Route[rParcels.length];
                System.arraycopy(rParcels, 0, lastRouteUpdate, 0, rParcels.length);
                updateRoutes(lastRouteUpdate);
            }
        }
    }

    public void updateActiveRoutes(){
        new RouteUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean updateRoutesIfNeeded(){
        DateTime lastUpdate = new DateTime(lastUpdatedAt);
        if(lastUpdate.isBefore(DateTime.now().minusMinutes(30))){
            updateActiveRoutes();
            return true;
        } else {
            return false;
        }
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
            routeLine.color(Constants.getRouteColorForRouteNumber(trackerFragment.getContext(), i));
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

            return activeRoutes;
        }

        @Override
        protected void onPostExecute(Route[] routes) {
            lastRouteUpdate = routes;
            lastUpdatedAt = DateTime.now().getMillis();
            updateRoutes(routes);
        }
    }

    public void saveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArray(Route.ROUTE_KEY, lastRouteUpdate);
        savedInstanceState.putLong(Route.LAST_UPDATED_KEY, lastUpdatedAt);
    }
}
