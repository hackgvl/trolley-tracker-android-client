package com.codeforgvl.trolleytrackerclient.models.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adam Hodges on 8/23/2015.
 */
public class Route implements Parcelable {
    public static final String ROUTE_KEY = "ROUTE_KEY";
    public static final String LAST_UPDATED_KEY = "ROUTE_LAST_UPDATED_KEY";
    public String Description;
    public int ID;

    private String ShortName;
    private boolean FlagStopsOnly;
    private String LongName;
    private String RouteColorRGB = "#acb71d";

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
        dest.writeString(RouteColorRGB);
    }

    public Route(){ }

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
        RouteColorRGB = in.readString();
    }

    public String getRouteColorRGB(){
        String routeColor = "#acb71d";
        if(RouteColorRGB != null && !RouteColorRGB.isEmpty()){

            // Add some transparency to standard color
            routeColor = "#CC" + RouteColorRGB.substring(1);
        }
        return routeColor;
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
