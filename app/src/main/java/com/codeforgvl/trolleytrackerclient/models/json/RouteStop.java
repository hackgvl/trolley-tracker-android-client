package com.codeforgvl.trolleytrackerclient.models.json;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.HashMap;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class RouteStop implements Parcelable{
    public HashMap<Integer, DateTime> TrolleyETAs;
    public HashMap<Integer, DateTime> LastTrolleyArrivalTimes;
    public String Description;
    public double Lat;
    public double Lon;
    public String Name;
    public int ID;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //TODO: dest.writeParcelable(LastTrolleyArrivalTimes);
        dest.writeString(Description);
        dest.writeDouble(Lat);
        dest.writeDouble(Lon);
        dest.writeString(Name);
        dest.writeInt(ID);
    }

    public RouteStop(Parcel in){
        //TODO: LastTrolleyArrivalTimes = read
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
