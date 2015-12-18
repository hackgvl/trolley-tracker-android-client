package com.codeforgvl.trolleytrackerclient.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class Route implements Parcelable {
    public static final String ROUTE_KEY = "ROUTE_KEY";
    public String ShortName;
    public String Description;
    public int ID;
    public boolean FlagStopsOnly;
    public String LongName;

    public RouteStop[] Stops;
    public LatLon[] RouteShape;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ShortName);
        dest.writeString(Description);
        dest.writeInt(ID);
        dest.writeByte((byte) (FlagStopsOnly ? 1 : 0));
        dest.writeString(LongName);
        dest.writeParcelableArray(Stops, 0);
        dest.writeParcelableArray(RouteShape, 0);
    }

    public Route(Parcel in){
        ShortName = in.readString();
        Description = in.readString();
        ID = in.readInt();
        FlagStopsOnly = in.readByte() == 1;
        LongName = in.readString();
        Parcelable[] sParcelable = in.readParcelableArray(RouteStop.class.getClassLoader());
        Stops = new RouteStop[sParcelable.length];
        System.arraycopy(sParcelable, 0, Stops, 0, sParcelable.length);
        Parcelable[] rParcelable = in.readParcelableArray(LatLon.class.getClassLoader());
        RouteShape = new LatLon[rParcelable.length];
        System.arraycopy(rParcelable, 0, RouteShape, 0, rParcelable.length);
    }

    public static final Parcelable.Creator<Route> CREATOR
            = new Parcelable.Creator<Route>() {

        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
