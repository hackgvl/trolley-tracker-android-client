package com.codeforgvl.trolleytrackerclient.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.Utils;
import com.codeforgvl.trolleytrackerclient.adapters.MapWindowAdapter;
import com.codeforgvl.trolleytrackerclient.helpers.RouteManager;
import com.codeforgvl.trolleytrackerclient.helpers.TrolleyManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

import org.joda.time.DateTime;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks {
    private MapFragmentListener mListener;

    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;

    private SlidingUpPanelLayout drawer;
    private FloatingActionButton fab;

    private Marker selectedMarker;

    private TrolleyManager trolleyMan;
    private RouteManager routeMan;

    public static MapFragment newInstance(Bundle args) {
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getContext());

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        ((FloatingActionButton) view.findViewById(R.id.myFAB)).setImageDrawable(new IconDrawable(getContext(), MaterialIcons.md_directions_walk).colorRes(R.color.white));

        drawer = (SlidingUpPanelLayout)view.findViewById(R.id.sliding_layout);
        drawer.setPanelSlideListener(new DrawerListener());

        fab = (FloatingActionButton)view.findViewById(R.id.myFAB);
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

        setUpMapIfNeeded();

        if (savedInstanceState != null){
            processBundle(savedInstanceState);
        } else {
            processBundle(getArguments());
        }

        trolleyMan.startUpdates();

        return view;
    }

    private void processBundle(Bundle b){
        if(b == null){
            return;
        }

        //Load stop/route data
        if(routeMan == null){
            routeMan = new RouteManager(this);
        }
        routeMan.processBundle(b);

        //Load current trolley position
        if(trolleyMan == null){
            trolleyMan = new TrolleyManager(this);
        }
        trolleyMan.processBundle(b);
    }

    public void tick(DateTime now){
        int min = now.getMinuteOfHour();
        if(min % 30 == 1){
            routeMan.updateActiveRoutes();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        trolleyMan.saveInstanceState(outState);
        routeMan.saveInstanceState(outState);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
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
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                Utils.requestPermission((AppCompatActivity)getActivity(), 1,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
            }
            if (mMap != null) {
                // Access to the location has been granted to the app.
                mMap.setMyLocationEnabled(true);
            }
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
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

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            View mapView = getActivity().findViewById(R.id.map);
            if(mapView.getHeight() == 0 || mapView.getWidth() == 0){
                return; //map layout not rendered yet
            }

            if(!trolleyMan.isEmpty()){
                LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                for(Marker m : trolleyMan.getMarkers()){
                    bounds.include(m.getPosition());
                }
                bounds.include(new LatLng(location.getLatitude(), location.getLongitude()));

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                int padding = ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)? size.x : size.y) / 4;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), padding);

                mMap.animateCamera(cu);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

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
        trolleyMan.startUpdates();
        routeMan.updateRoutesIfNeeded();
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle(R.string.title_fragment_maps);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        trolleyMan.stopUpdates();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (MapFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MapFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public interface MapFragmentListener {
        void onNavigateSchedule();
    }

    private MaterialDialog mNoTrolleysDialog;
    public void showNoTrolleysDialog(){
        if(mNoTrolleysDialog == null || !mNoTrolleysDialog.isShowing()){
            mNoTrolleysDialog = new MaterialDialog.Builder(getContext())
                    .title(R.string.no_trolleys_title)
                    .content(R.string.no_trolleys_message)
                    .positiveText(R.string.no_trolleys_button)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            mListener.onNavigateSchedule();
                        }
                    })
                    .show();
        }
    }
}
