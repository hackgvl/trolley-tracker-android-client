package com.codeforgvl.trolleytrackerclient.models.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class LatLon implements Parcelable {
    public double Lat;
    public double Lon;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(Lat);
        dest.writeDouble(Lon);
    }

    public LatLon(Parcel in) {
        Lat = in.readDouble();
        Lon = in.readDouble();
    }

    public static final Parcelable.Creator<LatLon> CREATOR
            = new Parcelable.Creator<LatLon>() {

        public LatLon createFromParcel(Parcel in) {
            return new LatLon(in);
        }

        public LatLon[] newArray(int size) {
            return new LatLon[size];
        }
    };
}
