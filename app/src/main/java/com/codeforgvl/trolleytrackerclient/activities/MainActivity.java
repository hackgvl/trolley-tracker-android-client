package com.codeforgvl.trolleytrackerclient.activities;

import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener {
    private MapFragment mapFragment;
    private ScheduleFragment scheduleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }

            mapFragment = MapFragment.newInstance(getIntent().getExtras());
            scheduleFragment = ScheduleFragment.newInstance(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment).commit();
        }

        //Initialize UI
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Drawer menu = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(myToolbar)
                .withHeader(R.layout.view_menu_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_map).withIcon(new IconDrawable(this, MaterialIcons.md_map)),
                        new PrimaryDrawerItem().withName(R.string.menu_schedule).withIcon(new IconDrawable(this, MaterialIcons.md_schedule)),
                        new DividerDrawerItem()//,
                        //new SecondaryDrawerItem().withName(R.string.menu_settings).withIcon(new IconDrawable(this, MaterialIcons.md_settings))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                getSupportFragmentManager().beginTransaction().show(mapFragment).hide(scheduleFragment).commit();
                                break;
                            case 2:
                                getSupportFragmentManager().beginTransaction().hide(mapFragment).show(scheduleFragment).commit();
                                break;
                        }
                        return false;
                    }
                })
                .build();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
