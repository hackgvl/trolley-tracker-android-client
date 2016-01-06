package com.codeforgvl.trolleytrackerclient.helpers;

import android.os.Bundle;
import android.os.Parcelable;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.fragments.MapFragment;
import com.codeforgvl.trolleytrackerclient.models.json.LatLon;
import com.codeforgvl.trolleytrackerclient.models.json.Route;
import com.codeforgvl.trolleytrackerclient.models.json.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.models.json.RouteStop;
import com.codeforgvl.trolleytrackerclient.models.json.Trolley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by ahodges on 12/18/2015.
 */
public class RouteManager {
    private MapFragment mapFragment;

    private Route[] mRoutes;

    public RouteManager(MapFragment activity){
        mapFragment = activity;
    }

    public void processBundle(Bundle b){
        if(b == null){
            return;
        }

        if (b != null){
            Parcelable[] rParcels = b.getParcelableArray(Route.ROUTE_KEY);
            mRoutes = new Route[rParcels.length];
            System.arraycopy(rParcels, 0, mRoutes, 0, rParcels.length);
            updateRoutes(mRoutes);
        }
    }

    public void startUpdates(){
        //if (mUpdateTask == null || mUpdateTask.isCancelled()){
        //    mUpdateTask = new TrolleyUpdateTask();
        //    mUpdateTask.execute();
        //}
    }

    public void stopUpdates(){
        //if(mUpdateTask != null){
        //    mUpdateTask.cancel(false);
        //}
    }

    public void updateRoutes(Route[] routes){
        for(int i=0; i<routes.length; i++){
            PolylineOptions routeLine = new PolylineOptions();
            for(LatLon p : routes[i].RouteShape){
                routeLine.add(new LatLng(p.Lat,p.Lon));
            }
            routeLine.color(Constants.getRouteColorForRouteNumber(mapFragment.getContext(), i));
            mapFragment.mMap.addPolyline(routeLine);


            for(RouteStop s : routes[i].Stops){
                mapFragment.mMap.addMarker(new MarkerOptions()
                        .title(s.Name)
                        .snippet(s.Description)
                        .anchor(0.5f, 0.5f)
                        .icon(IconFactory.getStopIcon(mapFragment.getContext(), Constants.getStopColorForRouteNumber(mapFragment.getContext(), i)))
                        .position(new LatLng(s.Lat, s.Lon)));
            }
        }
    }

    public void saveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArray(Route.ROUTE_KEY, mRoutes);
    }
}
