package com.codeforgvl.trolleytrackerclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

/**
 * Created by ahodges on 8/26/2015.
 */
public class SplashScreen extends Activity {
    private ProgressBar mProgress;
    private TrolleyData[] mTrolleyData;
    private TrolleyRoute[] mTrolleyRoutes;
    private boolean trolleysLoaded = false;
    private boolean routesLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mProgress = (ProgressBar) findViewById(R.id.splash_progress_bar);
        mProgress.setProgress(10);

        AsyncTask<Void, Integer, TrolleyRoute[]> routeThread = new AsyncTask<Void, Integer, TrolleyRoute[]>() {
            @Override
            protected TrolleyRoute[] doInBackground(Void... params) {
                publishProgress(5);
                //HTTP get active routes
                TrolleyRoute[] activeRoutes = TrolleyAPI.getActiveRoutes();

                int inc = 40 / activeRoutes.length;
                //HTTP get each route
                for(TrolleyRoute route : activeRoutes){
                    TrolleyRoute details = TrolleyAPI.getRouteDetails(route.ID);
                    route.RouteShape = details.RouteShape;
                    route.Stops = details.Stops;
                    publishProgress(inc);
                }

                return activeRoutes;
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                mProgress.setProgress(mProgress.getProgress() + progress[0]);
            }

            @Override
            protected void onPostExecute(TrolleyRoute[] routes) {
                mTrolleyRoutes = routes;
                routesLoaded = true;
                changeActivitiesIfComplete();
            }
        };

        AsyncTask<Void, Integer, TrolleyData[]> trolleyThread = new AsyncTask<Void, Integer, TrolleyData[]>() {
            @Override
            protected TrolleyData[] doInBackground(Void... params) {
                publishProgress(5);
                //HTTP get running trolleys
                TrolleyData[] trolleys = TrolleyAPI.getRunningTrolleys();
                publishProgress(20);
                return trolleys;
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                mProgress.setProgress(mProgress.getProgress() + progress[0]);
            }

            @Override
            protected void onPostExecute(TrolleyData[] trolleys){
                mTrolleyData = trolleys;
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
            intent.putExtra(TrolleyData.TROLLEY_DATA, mTrolleyData);
            intent.putExtra(TrolleyRoute.TROLLEY_ROUTES, mTrolleyRoutes);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
