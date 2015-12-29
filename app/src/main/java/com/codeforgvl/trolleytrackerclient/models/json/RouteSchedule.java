package com.codeforgvl.trolleytrackerclient.models.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahodges on 10/18/2015.
 */
public class RouteSchedule implements Parcelable{
    public static final String SCHEDULE_KEY = "SCHEDULE_KEY";
    public int ID;
    public int RouteID;
    public String RouteLongName;
    public String DayOfWeek;
    public String StartTime;
    public String EndTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeInt(RouteID);
        dest.writeString(RouteLongName);
        dest.writeString(DayOfWeek);
        dest.writeString(StartTime);
        dest.writeString(EndTime);
    }

    public RouteSchedule(Parcel in){
        ID = in.readInt();
        RouteID = in.readInt();
        RouteLongName = in.readString();
        DayOfWeek = in.readString();
        StartTime = in.readString();
        EndTime = in.readString();
    }

    public static final Parcelable.Creator<RouteSchedule> CREATOR
            = new Parcelable.Creator<RouteSchedule>() {

        public RouteSchedule createFromParcel(Parcel in) {
            return new RouteSchedule(in);
        }

        public RouteSchedule[] newArray(int size) {
            return new RouteSchedule[size];
        }
    };
}
