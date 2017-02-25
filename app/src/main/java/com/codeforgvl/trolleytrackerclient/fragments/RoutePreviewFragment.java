package com.codeforgvl.trolleytrackerclient.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.Utils;
import com.codeforgvl.trolleytrackerclient.adapters.MapWindowAdapter;
import com.codeforgvl.trolleytrackerclient.helpers.RouteManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class RoutePreviewFragment extends Fragment implements OnMapReadyCallback,IMapFragment, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private SlidingUpPanelLayout drawer;

    private Marker selectedMarker;

    private RouteManager routeMan;

    public static RoutePreviewFragment newInstance(Bundle args) {
        RoutePreviewFragment fragment = new RoutePreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RoutePreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        drawer = (SlidingUpPanelLayout)view.findViewById(R.id.sliding_layout);
        drawer.setPanelSlideListener(new DrawerListener());

        setUpMapIfNeeded();

        if (savedInstanceState != null){
            processBundle(savedInstanceState);
        } else {
            processBundle(getArguments());
        }

        return view;
    }

    public void processBundle(Bundle b){
        if(b == null){
            return;
        }

        if(!isHidden()){
            Utils.getActivity(this).setTitle(R.string.title_fragment_preview);
        }

        //Load stop/route data
        if(routeMan == null){
            routeMan = new RouteManager(this);
        }
        routeMan.processBundle(b);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        routeMan.saveInstanceState(outState);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            //Capture marker clicks, show 'selected' marker
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMapClickListener(this);
            selectedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).visible(false));

            mMap.setInfoWindowAdapter(new MapWindowAdapter(getContext()));

            //Show users current location
            enableMyLocation();
        }

        routeMan.onMapReady();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                Utils.requestPermission((AppCompatActivity)getActivity(), Constants.LOCATION_PERMISSION_REQUEST_ID,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
            }
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                    mMap.setMyLocationEnabled(true);
                } else {

                }
                return;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(selectedMarker)){
            selectedMarker.showInfoWindow();
            return true;
        }

        selectedMarker.setPosition(marker.getPosition());
        selectedMarker.setVisible(true);
        selectedMarker.showInfoWindow();

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

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Utils.getActivity(this).setTitle(R.string.title_fragment_preview);
        }
    }

    @Override
    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public boolean fragmentIsAdded(){
        return isAdded();
    }
}
