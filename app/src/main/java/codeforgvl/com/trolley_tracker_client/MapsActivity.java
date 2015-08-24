package codeforgvl.com.trolley_tracker_client;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;
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
        Iconify.with(new FontAwesomeModule()).with((new MaterialModule()));
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
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
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
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
    private void setUpMap() {
        //Show users current location
        mMap.setMyLocationEnabled(true);

        //Load stop/route data
        TrolleyRoute[] routes = new Gson().fromJson(Constants.ROUTE_DATA, TrolleyRoute[].class);
        PolylineOptions routeLine = new PolylineOptions();
        for(RoutePoint p : routes[0].RouteShape){
            routeLine.add(new LatLng(p.Lat,p.Lon));
        }
        mMap.addPolyline(routeLine);

        for(RouteStop s : routes[0].Stops){
            mMap.addMarker(new MarkerOptions().alpha(.5f).icon(IconFactory.getCustomMarker(getBaseContext(), FontAwesomeIcons.fa_map_marker, .4)).position(new LatLng(s.Lat, s.Lon)));
        }
    }

    private class TrolleyUpdateTask extends AsyncTask<Void, TrolleyData[], Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                Log.d(Constants.LOG_TAG, "requesting trolley update");

                String json = TrolleyAPI.getActiveTrolleys();

                Log.d(Constants.LOG_TAG, json);
                TrolleyData[] trolleyList = new Gson().fromJson(json, TrolleyData[].class);

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
            Set<Integer> keySet = new HashSet<>(trolleyMarkers.keySet());
            for(TrolleyData t : trolleyUpdate[0]){
                if(trolleyMarkers.containsKey(t.ID)){
                    trolleyMarkers.get(t.ID).setPosition(new LatLng(t.Lat, t.Lon));
                    keySet.remove(t.ID);
                } else {
                    trolleyMarkers.put(t.ID, mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(IconFactory.getCustomMarker(getBaseContext(), FontAwesomeIcons.fa_bus)).position(new LatLng(t.Lat, t.Lon))));
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
    }
}
