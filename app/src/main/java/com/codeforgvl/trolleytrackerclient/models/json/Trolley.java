package com.codeforgvl.trolleytrackerclient.models.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class Trolley implements Parcelable {
    public static final String TROLLEY_KEY = "TROLLEY_KEY";
    public double Lat;
    public double Lon;
    public int ID;
    public int Number;
    public String TrolleyName;

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

    public Trolley(Parcel in){
        Lat = in.readDouble();
        Lon = in.readDouble();
        ID = in.readInt();
        Number = in.readInt();
        TrolleyName = in.readString();
    }

    public static final Parcelable.Creator<Trolley> CREATOR
            = new Parcelable.Creator<Trolley>() {

        public Trolley createFromParcel(Parcel in) {
            return new Trolley(in);
        }

        public Trolley[] newArray(int size) {
            return new Trolley[size];
        }
    };

}
