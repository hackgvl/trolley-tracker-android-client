package com.codeforgvl.trolleytrackerclient.activities;

import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.codeforgvl.trolleytrackerclient.adapters.MapWindowAdapter;
import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.helpers.RouteManager;
import com.codeforgvl.trolleytrackerclient.helpers.TrolleyManager;
import com.codeforgvl.trolleytrackerclient.models.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.models.Trolley;
import com.codeforgvl.trolleytrackerclient.models.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener {
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private SlidingUpPanelLayout drawer;
    private FloatingActionButton fab;

    private Marker selectedMarker;

    private TrolleyManager trolleyMan;
    private RouteManager routeMan;

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
        RouteSchedule[] schedules = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            Parcelable[] tParcels = extras.getParcelableArray(Trolley.TROLLEY_KEY);
            trolleys = new Trolley[tParcels.length];
            System.arraycopy(tParcels, 0, trolleys, 0, tParcels.length);

            Parcelable[] rParcels = extras.getParcelableArray(Route.ROUTE_KEY);
            routes = new Route[rParcels.length];
            System.arraycopy(rParcels, 0, routes, 0, rParcels.length);

            Parcelable[] sParcels = extras.getParcelableArray(RouteSchedule.SCHEDULE_KEY);
            schedules = new RouteSchedule[sParcels.length];
            System.arraycopy(sParcels, 0, schedules, 0, sParcels.length);
        }
        setUpMapIfNeeded(trolleys, routes, schedules);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded(null, null, null);
    }

    @Override
    protected void onPause(){
        super.onPause();
        trolleyMan.stopUpdates();
        routeMan.stopUpdates();
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
    private void setUpMapIfNeeded(Trolley[] trolleys, Route[] routes, RouteSchedule[] schedules) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //Capture marker clicks, show 'selected' marker
                mMap.setOnMarkerClickListener(this);
                mMap.setOnMapClickListener(this);
                selectedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).visible(false));

                mMap.setInfoWindowAdapter(new MapWindowAdapter(this));

                //Show users current location
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(this);
            }

            //Load stop/route data
            if(routeMan == null){
                routeMan = new RouteManager(this, routes, schedules);
            }

            //Load current trolley position
            if(trolleyMan == null){
                trolleyMan = new TrolleyManager(this, trolleys);
            }
        }

        routeMan.startUpdates();
        trolleyMan.startUpdates();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(selectedMarker)){
            selectedMarker.showInfoWindow();
            return true;
        }

        //Don't show 'selected' marker when user clicks on a trolley
        if(!trolleyMan.getMarkers().contains(marker)){
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
            if(!trolleyMan.isEmpty()){
                LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                for(Marker m : trolleyMan.getMarkers()){
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
