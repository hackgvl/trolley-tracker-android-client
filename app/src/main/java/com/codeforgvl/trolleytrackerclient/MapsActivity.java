package com.codeforgvl.trolleytrackerclient;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private HashMap<Integer, Marker> trolleyMarkers = new HashMap<>();

    private TrolleyUpdateTask mUpdateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        TrolleyData[] trolleys = null;
        TrolleyRoute[] routes = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            Parcelable[] tParcels = extras.getParcelableArray(TrolleyData.TROLLEY_DATA);
            trolleys = new TrolleyData[tParcels.length];
            System.arraycopy(tParcels, 0, trolleys, 0, tParcels.length);

            Parcelable[] rParcels = extras.getParcelableArray(TrolleyRoute.TROLLEY_ROUTES);
            routes = new TrolleyRoute[rParcels.length];
            System.arraycopy(rParcels, 0, routes, 0, rParcels.length);
        }
        setUpMapIfNeeded(trolleys, routes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded(null, null);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mUpdateTask != null){
            mUpdateTask.cancel(false);
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call setUpMap once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded(TrolleyData[] trolleys, TrolleyRoute[] routes) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(trolleys, routes);
            }
        }

        //Start/restart background updates
        if (mUpdateTask == null || mUpdateTask.isCancelled()){
            mUpdateTask = new TrolleyUpdateTask();
            mUpdateTask.execute();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(TrolleyData[] trolleys, TrolleyRoute[] routes) {
        //Show users current location
        mMap.setMyLocationEnabled(true);

        //Load stop/route data TODO: handle multiple routes
        if(routes != null){
            PolylineOptions routeLine = new PolylineOptions();
            for(LatLon p : routes[0].RouteShape){
                routeLine.add(new LatLng(p.Lat,p.Lon));
            }
            routeLine.color(0x7FFF0000);
            mMap.addPolyline(routeLine);

            for(RouteStop s : routes[0].Stops){
                mMap.addMarker(new MarkerOptions()
                        .alpha(.5f)
                        .title(s.Name)
                        .snippet(s.Description)
                        .icon(IconFactory.getCustomMarker(getBaseContext(), FontAwesomeIcons.fa_map_marker, .3, Color.BLUE))
                        .position(new LatLng(s.Lat, s.Lon)));
            }
        }

        //Load current trolley position
        if(trolleys != null){
            updateTrolleys(trolleys);
        }
    }

    private void updateTrolleys(TrolleyData[] trolleys){
        Set<Integer> keySet = new HashSet<>(trolleyMarkers.keySet());
        for(TrolleyData t : trolleys){
            if(trolleyMarkers.containsKey(t.ID)){
                trolleyMarkers.get(t.ID).setPosition(new LatLng(t.Lat, t.Lon));
                keySet.remove(t.ID);
            } else {
                trolleyMarkers.put(t.ID, mMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(IconFactory.getCustomMarker(getBaseContext(), FontAwesomeIcons.fa_bus, .8, Color.GREEN))
                        .position(new LatLng(t.Lat, t.Lon))));
            }
        }

        for(Integer deadTrolleyID : keySet){
            trolleyMarkers.get(deadTrolleyID).remove();
            trolleyMarkers.remove(deadTrolleyID);
        }

        for(Integer trolleyID : trolleyMarkers.keySet()){
            Log.d(Constants.LOG_TAG, trolleyMarkers.get(trolleyID).getPosition().toString());
        }
    }

    private class TrolleyUpdateTask extends AsyncTask<Void, TrolleyData[], Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                Log.d(Constants.LOG_TAG, "requesting trolley update");

                TrolleyData[] trolleyList = TrolleyAPI.getRunningTrolleys();

                publishProgress(trolleyList);

                try {
                    Thread.sleep(Constants.SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(TrolleyData[]... trolleyUpdate){
            Log.d(Constants.LOG_TAG, "processing trolley updates");

            updateTrolleys(trolleyUpdate[0]);
        }
    }
}
