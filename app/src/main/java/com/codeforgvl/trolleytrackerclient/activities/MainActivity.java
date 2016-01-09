package com.codeforgvl.trolleytrackerclient.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.Utils;
import com.codeforgvl.trolleytrackerclient.fragments.MapFragment;
import com.codeforgvl.trolleytrackerclient.fragments.ScheduleFragment;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.joda.time.DateTime;

public class MainActivity extends AppCompatActivity implements MapFragment.MapFragmentListener, FragmentManager.OnBackStackChangedListener {
    public final static int MAP_FRAGMENT_ID = 1;
    public final static int SCHEDULE_FRAGMENT_ID = 2;
    public final static String MAP_FRAGMENT_TAG = "MAP_FRAGMENT";
    public final static String SCHEDULE_FRAGMENT_TAG = "SCHEDULE_FRAGMENT";
    public final static String ACTIVE_FRAGMENT_TAG = "ACTIVE_FRAGMENT";

    private MapFragment mapFragment;
    private ScheduleFragment scheduleFragment;
    private Drawer menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int selectedFragmentID = MAP_FRAGMENT_ID;
        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                mapFragment = (MapFragment)getSupportFragmentManager().getFragment(savedInstanceState, MAP_FRAGMENT_TAG);
                scheduleFragment = (ScheduleFragment)getSupportFragmentManager().getFragment(savedInstanceState, SCHEDULE_FRAGMENT_TAG);

                String activeFragment = savedInstanceState.getString(ACTIVE_FRAGMENT_TAG);
                if(activeFragment != null){
                    switch (activeFragment){
                        case MAP_FRAGMENT_TAG:
                            showMap();
                            selectedFragmentID = MAP_FRAGMENT_ID;
                            break;
                        case SCHEDULE_FRAGMENT_TAG:
                            showSchedule();
                            selectedFragmentID = SCHEDULE_FRAGMENT_ID;
                            break;
                    }
                }

            } else {

                mapFragment = MapFragment.newInstance(getIntent().getExtras());
                scheduleFragment = ScheduleFragment.newInstance(getIntent().getExtras());

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, scheduleFragment, SCHEDULE_FRAGMENT_TAG).hide(scheduleFragment)
                        .add(R.id.fragment_container, mapFragment, MAP_FRAGMENT_TAG)
                        .commit();
                getSupportFragmentManager().addOnBackStackChangedListener(this);
            }
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
        getSupportFragmentManager().beginTransaction().addToBackStack(MAP_FRAGMENT_TAG)
                .show(mapFragment)
                .hide(scheduleFragment)
                .commit();
    }

    private void showSchedule(){
        getSupportFragmentManager().beginTransaction().addToBackStack(SCHEDULE_FRAGMENT_TAG)
                .hide(mapFragment)
                .show(scheduleFragment)
                .commit();
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

        getSupportFragmentManager().putFragment(outState, MAP_FRAGMENT_TAG, mapFragment);
        getSupportFragmentManager().putFragment(outState, SCHEDULE_FRAGMENT_TAG, scheduleFragment);
        outState.putString(ACTIVE_FRAGMENT_TAG, Utils.getActiveFragmentName(getSupportFragmentManager()));
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
                case SCHEDULE_FRAGMENT_TAG:
                    menu.setSelection(SCHEDULE_FRAGMENT_ID, false);
                    break;
            }
        } else {
            menu.setSelection(MAP_FRAGMENT_ID, false);
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
                    mapFragment.tick(now);
                }
            });
        }
    }
}
