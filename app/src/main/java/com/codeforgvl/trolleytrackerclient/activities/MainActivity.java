package com.codeforgvl.trolleytrackerclient.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.codeforgvl.trolleytrackerclient.R;
import com.codeforgvl.trolleytrackerclient.fragments.MapFragment;
import com.codeforgvl.trolleytrackerclient.fragments.ScheduleFragment;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity implements MapFragment.OnNavigateScheduleRequest, FragmentManager.OnBackStackChangedListener {
    public final static int MAP_FRAGMENT_ID = 1;
    public final static int SCHEDULE_FRAGMENT_ID = 2;
    public final static String MAP_FRAGMENT_TAG = "MAP_FRAGMENT";
    public final static String SCHEDULE_FRAGMENT_TAG = "SCHEDULE_FRAGMENT";

    private MapFragment mapFragment;
    private ScheduleFragment scheduleFragment;
    private Drawer menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }

            getSupportFragmentManager().addOnBackStackChangedListener(this);
            mapFragment = MapFragment.newInstance(getIntent().getExtras());
            scheduleFragment = ScheduleFragment.newInstance(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, scheduleFragment, SCHEDULE_FRAGMENT_TAG).hide(scheduleFragment)
                    .add(R.id.fragment_container, mapFragment, MAP_FRAGMENT_TAG)
                    .commit();
        }

        //Initialize UI
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        menu = new DrawerBuilder()
                .withActivity(this)
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
                                getSupportFragmentManager().beginTransaction().addToBackStack(MAP_FRAGMENT_TAG)
                                        .show(mapFragment)
                                        .hide(scheduleFragment)
                                        .commit();
                                break;
                            case SCHEDULE_FRAGMENT_ID:
                                getSupportFragmentManager().beginTransaction().addToBackStack(SCHEDULE_FRAGMENT_TAG)
                                        .hide(mapFragment)
                                        .show(scheduleFragment)
                                        .commit();
                                break;
                        }
                        return false;
                    }
                })
                .build();
    }

    @Override
    public void onNavigateSchedule() {
        menu.setSelection(SCHEDULE_FRAGMENT_ID);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getSupportFragmentManager();
        int stackSize = fm.getBackStackEntryCount();
        if(stackSize > 0){
            String fragTag = fm.getBackStackEntryAt(stackSize - 1).getName();

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
}
