package com.codeforgvl.trolleytrackerclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.codeforgvl.trolleytrackerclient.data.Trolley;
import com.codeforgvl.trolleytrackerclient.data.Route;

/**
 * Created by ahodges on 8/26/2015.
 */
public class SplashScreen extends Activity {
    private ProgressBar mProgress;
    private Trolley[] mTrolley;
    private Route[] mRoutes;
    private boolean trolleysLoaded = false;
    private boolean routesLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mProgress = (ProgressBar) findViewById(R.id.splash_progress_bar);
        mProgress.setProgress(10);

        AsyncTask<Void, Integer, Route[]> routeThread = new AsyncTask<Void, Integer, Route[]>() {
            @Override
            protected Route[] doInBackground(Void... params) {
                publishProgress(5);
                //HTTP get active routes
                Route[] activeRoutes = TrolleyAPI.getActiveRoutes();
                publishProgress(20);

                if(activeRoutes.length > 0){
                    int inc = 40 / activeRoutes.length;
                    //HTTP get each route
                    for(Route route : activeRoutes){
                        Route details = TrolleyAPI.getRouteDetails(route.ID);
                        route.RouteShape = details.RouteShape;
                        route.Stops = details.Stops;
                        publishProgress(inc);
                    }
                } else {
                    publishProgress(40);
                }

                return activeRoutes;
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                mProgress.setProgress(mProgress.getProgress() + progress[0]);
            }

            @Override
            protected void onPostExecute(Route[] routes) {
                mRoutes = routes;
                routesLoaded = true;
                changeActivitiesIfComplete();
            }
        };

        AsyncTask<Void, Integer, Trolley[]> trolleyThread = new AsyncTask<Void, Integer, Trolley[]>() {
            @Override
            protected Trolley[] doInBackground(Void... params) {
                publishProgress(5);
                //HTTP get running trolleys
                Trolley[] trolleys = TrolleyAPI.getRunningTrolleys();
                publishProgress(20);
                return trolleys;
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                mProgress.setProgress(mProgress.getProgress() + progress[0]);
            }

            @Override
            protected void onPostExecute(Trolley[] trolleys){
                mTrolley = trolleys;
                trolleysLoaded = true;
                changeActivitiesIfComplete();
            }
        };

        routeThread.execute();
        trolleyThread.execute();
    }

    public void changeActivitiesIfComplete(){
        if(trolleysLoaded && routesLoaded){
            Intent intent = new Intent(SplashScreen.this, MapsActivity.class);
            intent.putExtra(Trolley.TROLLEY_KEY, mTrolley);
            intent.putExtra(Route.ROUTE_KEY, mRoutes);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
