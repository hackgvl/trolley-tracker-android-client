package com.codeforgvl.trolleytrackerclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.codeforgvl.trolleytrackerclient.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Adam Hodges on 9/10/2015.
 */
public class MapWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context = null;

    public MapWindowAdapter(Context context) {
        this.context = context;
    }

    // Hack to prevent info window from displaying: use a 0dp/0dp frame
    @Override
    public View getInfoWindow(Marker marker) {
        View v = ((Activity) context).getLayoutInflater().inflate(R.layout.adapter_no_info_window, null);
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
