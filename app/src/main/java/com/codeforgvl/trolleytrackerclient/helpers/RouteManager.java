package com.codeforgvl.trolleytrackerclient.helpers;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.fragments.MapFragment;
import com.codeforgvl.trolleytrackerclient.models.LatLon;
import com.codeforgvl.trolleytrackerclient.models.Route;
import com.codeforgvl.trolleytrackerclient.models.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.models.RouteStop;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by ahodges on 12/18/2015.
 */
public class RouteManager {
    private MapFragment mapFragment;

    private RouteSchedule[] mSchedule;

    public RouteManager(MapFragment activity, Route[] routes, RouteSchedule[] schedules){
        mapFragment = activity;
        if(routes != null){
            updateRoutes(routes);
        }
        if(schedules != null){
            updateSchedule(schedules);
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

    public void updateSchedule(RouteSchedule[] schedules){

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
}
