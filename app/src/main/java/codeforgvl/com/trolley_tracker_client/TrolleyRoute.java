package codeforgvl.com.trolley_tracker_client;

/**
 * Created by littl_000 on 8/23/2015.
 */
public class TrolleyRoute {
    String ShortName;
    String Description;
    int ID;
    boolean FlagStopsOnly;
    String LongName;

    RouteStop[] Stops;
    RoutePoint[] RouteShape;
}
