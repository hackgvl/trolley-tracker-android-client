package com.codeforgvl.trolleytrackerclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class TrolleyData implements Parcelable {
    static final String TROLLEY_DATA = "TROLLEY_DATA";
    double Lat;
    double Lon;
    int ID;
    int Number;
    String TrolleyName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(Lat);
        dest.writeDouble(Lon);
        dest.writeInt(ID);
        dest.writeInt(Number);
        dest.writeString(TrolleyName);
    }

    public TrolleyData(Parcel in){
        Lat = in.readDouble();
        Lon = in.readDouble();
        ID = in.readInt();
        Number = in.readInt();
        TrolleyName = in.readString();
    }

    public static final Parcelable.Creator<TrolleyData> CREATOR
            = new Parcelable.Creator<TrolleyData>() {

        public TrolleyData createFromParcel(Parcel in) {
            return new TrolleyData(in);
        }

        public TrolleyData[] newArray(int size) {
            return new TrolleyData[size];
        }
    };

}
