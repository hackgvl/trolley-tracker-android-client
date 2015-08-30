package com.codeforgvl.trolleytrackerclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class TrolleyRoute implements Parcelable {
    static final String TROLLEY_ROUTES = "TROLLEY_ROUTES";
    String ShortName;
    String Description;
    int ID;
    boolean FlagStopsOnly;
    String LongName;

    RouteStop[] Stops;
    LatLon[] RouteShape;

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

    public TrolleyRoute(Parcel in){
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

    public static final Parcelable.Creator<TrolleyRoute> CREATOR
            = new Parcelable.Creator<TrolleyRoute>() {

        public TrolleyRoute createFromParcel(Parcel in) {
            return new TrolleyRoute(in);
        }

        public TrolleyRoute[] newArray(int size) {
            return new TrolleyRoute[size];
        }
    };
}
