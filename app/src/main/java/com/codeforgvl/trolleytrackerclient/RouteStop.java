package com.codeforgvl.trolleytrackerclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class RouteStop implements Parcelable{
    String Description;
    double Lat;
    double Lon;
    String Name;
    int ID;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Description);
        dest.writeDouble(Lat);
        dest.writeDouble(Lon);
        dest.writeString(Name);
        dest.writeInt(ID);
    }

    public RouteStop(Parcel in){
        Description = in.readString();
        Lat = in.readDouble();
        Lon = in.readDouble();
        Name = in.readString();
        ID = in.readInt();
    }

    public static final Parcelable.Creator<RouteStop> CREATOR
            = new Parcelable.Creator<RouteStop>() {

        public RouteStop createFromParcel(Parcel in) {
            return new RouteStop(in);
        }

        public RouteStop[] newArray(int size) {
            return new RouteStop[size];
        }
    };
}
