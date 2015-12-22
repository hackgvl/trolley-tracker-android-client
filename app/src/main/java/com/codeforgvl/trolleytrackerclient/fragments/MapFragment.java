package com.codeforgvl.trolleytrackerclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.adapters.MapWindowAdapter;
import com.codeforgvl.trolleytrackerclient.helpers.RouteManager;
import com.codeforgvl.trolleytrackerclient.helpers.TrolleyManager;
import com.codeforgvl.trolleytrackerclient.models.Route;
import com.codeforgvl.trolleytrackerclient.models.RouteSchedule;
import com.codeforgvl.trolleytrackerclient.models.Trolley;
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

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener  {
    private OnFragmentInteractionListener mListener;

    public GoogleMap mMap; // Might be null if Google Play services APK is not available.

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

    private Bundle extras;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getArguments();
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

        MapsInitializer.initialize(getContext());

        Trolley[] trolleys = null;
        Route[] routes = null;
        if (extras != null){
            Parcelable[] tParcels = extras.getParcelableArray(Trolley.TROLLEY_KEY);
            trolleys = new Trolley[tParcels.length];
            System.arraycopy(tParcels, 0, trolleys, 0, tParcels.length);

            Parcelable[] rParcels = extras.getParcelableArray(Route.ROUTE_KEY);
            routes = new Route[rParcels.length];
            System.arraycopy(rParcels, 0, routes, 0, rParcels.length);
        }
        setUpMapIfNeeded(trolleys, routes);
        return view;
    }

    private void setUpMapIfNeeded(Trolley[] trolleys, Route[] routes) {
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
                selectedMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).visible(false));

                mMap.setInfoWindowAdapter(new MapWindowAdapter(getContext()));

                //Show users current location
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(this);
            }

            //Load stop/route data
            if(routeMan == null){
                routeMan = new RouteManager(this, routes);
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

                Display display = getActivity().getWindowManager().getDefaultDisplay();
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

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded(null, null);
    }

    @Override
    public void onPause(){
        super.onPause();
        trolleyMan.stopUpdates();
        routeMan.stopUpdates();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
