package com.codeforgvl.trolleytrackerclient.helpers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.codeforgvl.trolleytrackerclient.Constants;
import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.data.TrolleyAPI;
import com.codeforgvl.trolleytrackerclient.data.TrolleyData;
import com.codeforgvl.trolleytrackerclient.fragments.TrackerFragment;
import com.codeforgvl.trolleytrackerclient.models.json.Trolley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.livefront.bridge.Bridge;

import org.joda.time.DateTime;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import icepick.State;

/**
 * Created by ahodges on 12/18/2015.
 */
public class TrolleyManager {
    private static final String NOTIFIED_EMPTY_KEY = "NOTIFIED_EMPTY";
    private TrackerFragment trackerFragment;
    private HashMap<Integer, Marker> trolleyMarkers = new HashMap<>();
    private TrolleyUpdateTask mUpdateTask;
    @State
    boolean notifiedEmpty = false;

    private Trolley[] lastTrolleyUpdate;
    //DateTime lastUpdatedAt;

    public TrolleyManager(TrackerFragment activity){
        trackerFragment = activity;
    }

    public void processBundle(Bundle b){
        if(b == null){
            return;
        }
        if (b != null){
            Bridge.restoreInstanceState(trackerFragment, b);

            if(lastTrolleyUpdate != null){
                DateTime lastUpdate =  TrolleyData.getInstance().getLastTrolleyUpdateTime();
                if(!lastUpdate.isBefore(DateTime.now().minusMinutes(1))){
                    updateTrolleys(lastTrolleyUpdate);
                }
            }


            notifiedEmpty = b.getBoolean(TrolleyManager.NOTIFIED_EMPTY_KEY, false);
        }
    }

    public void onMapReady(){
        if(lastTrolleyUpdate != null){
            updateTrolleys(lastTrolleyUpdate);
        }
    }

    public Collection<Marker> getMarkers(){
        return trolleyMarkers.values();
    }

    public boolean isEmpty(){
        return trolleyMarkers.isEmpty();
    }

    public void startUpdates(){
        if (mUpdateTask == null || mUpdateTask.isCancelled()){
            mUpdateTask = new TrolleyUpdateTask();
            mUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void stopUpdates(){
        if(mUpdateTask != null){
            mUpdateTask.cancel(false);
        }
    }

    private String TrolleyTitle(Trolley t) {
        String title = "Trolley " + t.ID;
        if ( t.TrolleyName != null && !t.TrolleyName.isEmpty()) {
            title = t.TrolleyName;
        }

        return title;
    }

    private synchronized void updateTrolleys(Trolley[] trolleys){
        if(trackerFragment.getMap() == null || !trackerFragment.fragmentIsAdded())
            return;

        Set<Integer> keySet = new HashSet<>(trolleyMarkers.keySet());

        if (TrolleyData.getInstance().TrolleyColorUpdated()) {
            // Trolley colors changed: redraw markers
            for(Integer deadTrolleyID : keySet){
                trolleyMarkers.get(deadTrolleyID).remove();
                trolleyMarkers.remove(deadTrolleyID);
            }
            keySet.clear(); // All items removed
        }

        for(int i=0; i < trolleys.length; i++){
            Trolley t = trolleys[i];
            if (t.Running) {
                if (trolleyMarkers.containsKey(t.ID)) {
                    trolleyMarkers.get(t.ID).setPosition(new LatLng(t.Lat, t.Lon));
                    keySet.remove(t.ID);
                } else {
                    trolleyMarkers.put(t.ID, trackerFragment.mMap.addMarker(new MarkerOptions()
                            .anchor(0.5f, 1.0f)
                            .title(TrolleyTitle(t))
                            .icon(IconFactory.getTrolleyIcon(trackerFragment.getContext(), Color.parseColor(t.getIconColorRGB())))
                            .position(new LatLng(t.Lat, t.Lon))));
                }
            }
        }

        for(Integer deadTrolleyID : keySet){
            trolleyMarkers.get(deadTrolleyID).remove();
            trolleyMarkers.remove(deadTrolleyID);
        }

        if(trolleyMarkers.isEmpty()){
            if(!notifiedEmpty){
                trackerFragment.showNoTrolleysDialog();
                notifiedEmpty = true;
            }
        } else {
            notifiedEmpty = false;
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

            TrolleyData.getInstance().setRunningTrolleyLocations(trolleyUpdate[0]);
            lastTrolleyUpdate = TrolleyData.getInstance().getTrolleys();

            if (lastTrolleyUpdate != null && trackerFragment.fragmentIsVisible()) {
                // Update after first full data available
                updateTrolleys(lastTrolleyUpdate);
            }
        }
    }

    public void saveInstanceState(Bundle savedInstanceState){
        Bridge.saveInstanceState(trackerFragment, savedInstanceState);
    }
}
