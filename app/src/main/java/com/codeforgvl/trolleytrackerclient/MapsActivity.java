package com.codeforgvl.trolleytrackerclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.codeforgvl.trolleytrackerclient.data.LatLon;
import com.codeforgvl.trolleytrackerclient.data.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.data.RouteStop;
import com.codeforgvl.trolleytrackerclient.data.Trolley;
import com.codeforgvl.trolleytrackerclient.data.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener {
    private SlidingUpPanelLayout drawer;
    private FloatingActionButton fab;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker selectedMarker;
    private HashMap<Integer, Marker> trolleyMarkers = new HashMap<>();

    private TrolleyUpdateTask mUpdateTask;

    private RouteSchedule[] mSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapsInitializer.initialize(getApplicationContext());

        ((FloatingActionButton) findViewById(R.id.myFAB)).setImageDrawable(new IconDrawable(this, MaterialIcons.md_directions_walk).colorRes(R.color.white));

        drawer = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        drawer.setPanelSlideListener(new DrawerListener());

        fab = (FloatingActionButton)findViewById(R.id.myFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMarker != null) {
                    LatLng ll = selectedMarker.getPosition();
                    Uri directionsUri = Uri.parse("google.navigation:q=" + ll.latitude + "," + ll.longitude + "&mode=w");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, directionsUri);
                    startActivity(mapIntent);
                }
            }
        });

        Trolley[] trolleys = null;
        Route[] routes = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            Parcelable[] tParcels = extras.getParcelableArray(Trolley.TROLLEY_KEY);
            trolleys = new Trolley[tParcels.length];
            System.arraycopy(tParcels, 0, trolleys, 0, tParcels.length);

            Parcelable[] rParcels = extras.getParcelableArray(Route.ROUTE_KEY);
            routes = new Route[rParcels.length];
            System.arraycopy(rParcels, 0, routes, 0, rParcels.length);

            Parcelable[] sParcels = extras.getParcelableArray(RouteSchedule.SCHEDULE_KEY);
            mSchedule = new RouteSchedule[sParcels.length];
            System.arraycopy(sParcels, 0, mSchedule, 0, sParcels.length);
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
    private void setUpMapIfNeeded(Trolley[] trolleys, Route[] routes) {
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
    private int getRouteColorForRouteNumber(int ndx){
        int routeNo = (ndx % 5) + 1;
        switch (routeNo){
            case 1:
                return getResources().getColor(R.color.route1);
            case 2:
                return getResources().getColor(R.color.route2);
            case 3:
                return getResources().getColor(R.color.route3);
            case 4:
                return getResources().getColor(R.color.route4);
            default:
                return getResources().getColor(R.color.route5);
        }
    }

    private int getStopColorForRouteNumber(int ndx){
        int routeNo = (ndx % 5) + 1;
        switch (routeNo){
            case 1:
                return getResources().getColor(R.color.stop1);
            case 2:
                return getResources().getColor(R.color.stop2);
            case 3:
                return getResources().getColor(R.color.stop3);
            case 4:
                return getResources().getColor(R.color.stop4);
            default:
                return getResources().getColor(R.color.stop5);
        }
    }

    private void setUpMap(Trolley[] trolleys, Route[] routes) {
        //Capture marker clicks, show 'selected' marker
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        selectedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).visible(false));

        mMap.setInfoWindowAdapter(new MapWindowAdapter(this));

        //Show users current location
        mMap.setMyLocationEnabled(true);

        //Load stop/route data
        if(routes != null){
            for(int i=0; i<routes.length; i++){
                PolylineOptions routeLine = new PolylineOptions();
                for(LatLon p : routes[i].RouteShape){
                    routeLine.add(new LatLng(p.Lat,p.Lon));
                }
                routeLine.color(getRouteColorForRouteNumber(i));
                mMap.addPolyline(routeLine);


                for(RouteStop s : routes[i].Stops){
                    mMap.addMarker(new MarkerOptions()
                            .title(s.Name)
                            .snippet(s.Description)
                            .anchor(0.5f, 0.5f)
                            .icon(IconFactory.getStopIcon(getBaseContext(), getStopColorForRouteNumber(i)))
                            .position(new LatLng(s.Lat, s.Lon)));
                }
            }
        }

        //Load current trolley position
        if(trolleys != null){
            updateTrolleys(trolleys);
        }

        mMap.setOnMyLocationChangeListener(this);
    }

    private void updateTrolleys(Trolley[] trolleys){
        Set<Integer> keySet = new HashSet<>(trolleyMarkers.keySet());
        for(int i=0; i < trolleys.length; i++){
            Trolley t = trolleys[i];
            if(trolleyMarkers.containsKey(t.ID)){
                trolleyMarkers.get(t.ID).setPosition(new LatLng(t.Lat, t.Lon));
                keySet.remove(t.ID);
            } else {
                trolleyMarkers.put(t.ID, mMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 1.0f)
                        .title("Trolley " + (i + 1))
                        .icon(BitmapDescriptorFactory.fromResource((i % 2 == 0) ? R.drawable.marker1 : R.drawable.marker2))
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(selectedMarker)){
            selectedMarker.showInfoWindow();
            return true;
        }

        //Don't show 'selected' marker when user clicks on a trolley
        if(!trolleyMarkers.values().contains(marker)){
            if(fab.getVisibility() != View.VISIBLE){
                fab.setVisibility(View.VISIBLE);
            }

            selectedMarker.setPosition(marker.getPosition());
            selectedMarker.setVisible(true);
            selectedMarker.showInfoWindow();
        } else {
            selectedMarker.setVisible(false);
            fab.setVisibility(View.GONE);
        }

        if(drawer.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN){
            drawer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }

        ((TextView)drawer.findViewById(R.id.drawer_title)).setText(marker.getTitle());

        //Animate to center
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 400, null);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        selectedMarker.setVisible(false);
        if(drawer.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN){
            drawer.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        if(fab.getVisibility() == View.VISIBLE){
            fab.setVisibility(View.GONE);
        }
    }

    private boolean firstFix = true;
    @Override
    public void onMyLocationChange(Location location) {
        if(firstFix){
            if(!trolleyMarkers.isEmpty()){
                LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                for(Marker m : trolleyMarkers.values()){
                    bounds.include(m.getPosition());
                }
                bounds.include(new LatLng(location.getLatitude(), location.getLongitude()));

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                int padding = size.x / 4;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), padding);
                mMap.animateCamera(cu);
            }
            firstFix = false;
        }
    }

    private class TrolleyUpdateTask extends AsyncTask<Void, Trolley[], Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                Log.d(Constants.LOG_TAG, "requesting trolley update");

                Trolley[] trolleyList = TrolleyAPI.getRunningTrolleys();

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
        protected void onProgressUpdate(Trolley[]... trolleyUpdate){
            Log.d(Constants.LOG_TAG, "processing trolley updates");

            updateTrolleys(trolleyUpdate[0]);
        }
    }

    private class DrawerListener implements SlidingUpPanelLayout.PanelSlideListener {
        @Override
        public void onPanelAnchored(View view) {

        }

        @Override
        public void onPanelHidden(View view) {

        }

        @Override
        public void onPanelSlide(View view, float v) {

        }

        @Override
        public void onPanelCollapsed(View view) {

        }

        @Override
        public void onPanelExpanded(View view) {

        }
    }
}
