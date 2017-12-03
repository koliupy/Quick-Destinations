package com.quickd.quickdestinations;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class LogoutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<Pair<String, String>> destinations = new ArrayList<>();
    public ArrayList<LatLng> latLngs = new ArrayList<>();
    public static boolean homeState = false;
    public static boolean loggedIn = false;
    public Bundle fragmentArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Fragment fragment = new NavigationFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.screen_area, fragment);
        fragmentTransaction.commit();

        latLngs.add(null);
        loggedIn = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.logout_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.logout_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_logout) {
            // Handle the camera action
            loggedIn = false;
            Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_start) {
            fragment = new NavigationFragment();
        } else if (id == R.id.nav_description) {

        } else if (id == R.id.nav_license) {

        } else if (id == R.id.nav_tosa) {

        } else if (id == R.id.nav_contact_us) {

        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (!homeState) {
                fragmentTransaction.addToBackStack("home");
                homeState = true;
            }
            fragmentTransaction.replace(R.id.screen_area, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.logout_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<Pair<String, String>> getDestinations() {
        return destinations;
    }

    public void setDestinations(Pair<String, String> destination) {
        destinations.add(destination);
    }

    public ArrayList<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setCurrentLocation(LatLng latLng) {
        latLngs.set(0, latLng);
    }

    public void setLatLngs(LatLng latLng) {
        latLngs.add(latLng);
    }

    public void saveFragmentState(Bundle args) {
        fragmentArgs = args;
    }

    public Bundle getFragmentState() {
        return fragmentArgs;
    }
}
