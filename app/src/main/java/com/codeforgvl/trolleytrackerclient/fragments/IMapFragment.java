package com.codeforgvl.trolleytrackerclient.fragments;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

public interface IMapFragment {
    GoogleMap getMap();
    Context getContext();
}
