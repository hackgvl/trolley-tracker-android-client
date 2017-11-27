package com.codeforgvl.trolleytrackerclient.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.Utils;
import com.codeforgvl.trolleytrackerclient.fragments.RoutePreviewFragment;
import com.codeforgvl.trolleytrackerclient.fragments.TrackerFragment;
import com.codeforgvl.trolleytrackerclient.fragments.ScheduleFragment;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.joda.time.DateTime;

import icepick.Icepick;
import icepick.State;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, TrackerFragment.MapFragmentListener, FragmentManager.OnBackStackChangedListener {
    public final static int MAP_FRAGMENT_ID = 1;
    public final static int SCHEDULE_FRAGMENT_ID = 2;
    public final static int PREVIEW_FRAGMENT_ID = 3;
    public final static String MAP_FRAGMENT_TAG = "MAP_FRAGMENT";
    public final static String SCHEDULE_FRAGMENT_TAG = "SCHEDULE_FRAGMENT";
    public final static String PREVIEW_FRAGMENT_TAG = "PREVIEW_FRAGMENT";

    TrackerFragment trackerFragment;
    ScheduleFragment scheduleFragment;
    RoutePreviewFragment previewFragment;
    @State
    String activeFragment;

    private Drawer menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Icepick.restoreInstanceState(this, savedInstanceState);

        int selectedFragmentID = MAP_FRAGMENT_ID;
        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                trackerFragment = (TrackerFragment)getSupportFragmentManager().getFragment(savedInstanceState, MAP_FRAGMENT_TAG);
                scheduleFragment = (ScheduleFragment)getSupportFragmentManager().getFragment(savedInstanceState, SCHEDULE_FRAGMENT_TAG);
                previewFragment = (RoutePreviewFragment)getSupportFragmentManager().getFragment(savedInstanceState, PREVIEW_FRAGMENT_TAG);
                if(activeFragment != null){
                    switch (activeFragment){
                        case MAP_FRAGMENT_TAG:
                            showMap(true);
                            selectedFragmentID = MAP_FRAGMENT_ID;
                            break;
                        case SCHEDULE_FRAGMENT_TAG:
                            showSchedule(true);
                            selectedFragmentID = SCHEDULE_FRAGMENT_ID;
                            break;
                        case PREVIEW_FRAGMENT_TAG:
                            showRoutePreview(true);
                            selectedFragmentID = PREVIEW_FRAGMENT_ID;
                            break;
                    }
                }

            } else {
                trackerFragment = TrackerFragment.newInstance(getIntent().getExtras());
                scheduleFragment = ScheduleFragment.newInstance(getIntent().getExtras());
                previewFragment = RoutePreviewFragment.newInstance(getIntent().getExtras());

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, previewFragment, PREVIEW_FRAGMENT_TAG).hide(previewFragment)
                        .add(R.id.fragment_container, scheduleFragment, SCHEDULE_FRAGMENT_TAG).hide(scheduleFragment)
                        .add(R.id.fragment_container, trackerFragment, MAP_FRAGMENT_TAG)
                        .commit();
            }
            getSupportFragmentManager().addOnBackStackChangedListener(this);
        }

        //Initialize UI
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        menu = new DrawerBuilder()
                .withActivity(this)
                .withSelectedItem(selectedFragmentID)
                .withToolbar(myToolbar)
                .withHeader(R.layout.view_menu_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(MAP_FRAGMENT_ID).withName(R.string.menu_map).withIcon(new IconDrawable(this, MaterialIcons.md_map)),
                        new PrimaryDrawerItem().withIdentifier(SCHEDULE_FRAGMENT_ID).withName(R.string.menu_schedule).withIcon(new IconDrawable(this, MaterialIcons.md_schedule)),
                        new DividerDrawerItem()//,
                        //new SecondaryDrawerItem().withName(R.string.menu_settings).withIcon(new IconDrawable(this, MaterialIcons.md_settings))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()) {
                            case MAP_FRAGMENT_ID:
                                showMap();
                                break;
                            case SCHEDULE_FRAGMENT_ID:
                                showSchedule();
                                break;
                        }
                        return false;
                    }
                })
                .build();

    }

    private void showMap(){
        showMap(false);
    }
    private void showMap(boolean forceHide){
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(MAP_FRAGMENT_TAG);
        if(forceHide || !previewFragment.isHidden())
            ft.hide(previewFragment);
        if(forceHide || !scheduleFragment.isHidden())
            ft.hide(scheduleFragment);
        ft.show(trackerFragment);
        ft.commit();
    }

    private void showSchedule(){
        showSchedule(false);
    }
    private void showSchedule(boolean forceHide){
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(SCHEDULE_FRAGMENT_TAG);
        if(forceHide || !previewFragment.isHidden())
            ft.hide(previewFragment);
        if(forceHide || !trackerFragment.isHidden())
            ft.hide(trackerFragment);
        ft.show(scheduleFragment);
        ft.commit();
    }

    public void showRoutePreview(Bundle bundle) {
        previewFragment.processBundle(bundle);
        showRoutePreview();
    }
    private void showRoutePreview(){
        showRoutePreview(false);
    }
    private void showRoutePreview(boolean forceHide){
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(PREVIEW_FRAGMENT_TAG);
        if(forceHide || !trackerFragment.isHidden())
            ft.hide(trackerFragment);
        if(forceHide || !scheduleFragment.isHidden())
            ft.hide(scheduleFragment);
        ft.show(previewFragment);
        ft.commit();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mTimeChangeReceiver != null){
            unregisterReceiver(mTimeChangeReceiver);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mTimeChangeReceiver == null){
            mTimeChangeReceiver = new TimeChangeReceiver(new Handler());
        }
        //Respond to time ticks
        registerReceiver(mTimeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, MAP_FRAGMENT_TAG, trackerFragment);
        getSupportFragmentManager().putFragment(outState, SCHEDULE_FRAGMENT_TAG, scheduleFragment);
        getSupportFragmentManager().putFragment(outState, PREVIEW_FRAGMENT_TAG, previewFragment);

        activeFragment = Utils.getActiveFragmentName(getSupportFragmentManager());
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onNavigateSchedule() {
        menu.setSelection(SCHEDULE_FRAGMENT_ID);
    }

    @Override
    public void onBackStackChanged() {
        String fragTag = Utils.getActiveFragmentName(getSupportFragmentManager());
        if(fragTag != null){
            switch (fragTag){
                case MAP_FRAGMENT_TAG:
                    menu.setSelection(MAP_FRAGMENT_ID, false);
                    break;
                case PREVIEW_FRAGMENT_TAG:
                case SCHEDULE_FRAGMENT_TAG:
                    menu.setSelection(SCHEDULE_FRAGMENT_ID, false);
                    break;
            }
        } else {
            menu.setSelection(MAP_FRAGMENT_ID, false);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(trackerFragment != null){
            trackerFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if(previewFragment != null){
            previewFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private TimeChangeReceiver mTimeChangeReceiver;
    private class TimeChangeReceiver extends BroadcastReceiver {
        private final Handler handler;
        public TimeChangeReceiver(Handler handler){
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DateTime now = DateTime.now();
                    trackerFragment.tick(now);
                }
            });
        }
    }
}
