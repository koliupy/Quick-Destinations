package com.quickd.quickdestinations;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<Pair<String, Integer>> destinations = new ArrayList<>();
    public ArrayList<Pair<String, LatLng>> latLngs = new ArrayList<>();
    public PriorityQueue<SetTimedDest> timedLatLngs = new PriorityQueue<>();
    public static boolean homeState = false;
    public static boolean loggedIn = false;
    public Bundle fragmentArgs;

    public class SetTimedDest implements Comparable<SetTimedDest>{
        public String destination;
        public LatLng latLong;
        public int minuteTime;
        public SetTimedDest(String destination, LatLng latLong, int minuteTime){
            this.destination = destination;
            this.latLong = latLong;
            this.minuteTime = minuteTime;
        }
        @Override
        public int compareTo(SetTimedDest other) {
            return this.minuteTime - other.minuteTime;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loggedIn = extras.getBoolean("loggedIn");
        }

        Fragment fragment;
        if (loggedIn == true) {
            setContentView(R.layout.activity_logout);
            fragment = new NavigationFragment();
        }
        else {
            setContentView(R.layout.activity_login);
            fragment = new LoginFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.screen_area, fragment);
        fragmentTransaction.commit();

        latLngs.add(null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer;
        if (loggedIn == true)
            drawer = findViewById(R.id.logout_layout);
        else
            drawer = findViewById(R.id.login_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer;
        if (loggedIn == true)
            drawer = findViewById(R.id.logout_layout);
        else
            drawer = findViewById(R.id.login_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (homeState)
                homeState = false;
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

        if (id == R.id.nav_login) {
            // Handle the login action
            fragment = new LoginFragment();
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("loggedIn", false);
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
            if (!homeState && (id != R.id.nav_login)) {
                fragmentTransaction.addToBackStack("home");
                homeState = true;
            }
            fragmentTransaction.replace(R.id.screen_area, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer;
        if (loggedIn == true)
            drawer = findViewById(R.id.logout_layout);
        else
            drawer = findViewById(R.id.login_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setDestinations(Pair<String, Integer> destination) {
        destinations.add(destination);
    }

    public ArrayList<Pair<String, Integer>> getDestinations() {
        return destinations;
    }

    public void rmDestinations(int i){
        destinations.remove(i);
        latLngs.remove(i+1);
    }

    public void setTimedLatLngs(String name, LatLng latLng, int minuteTime){
        timedLatLngs.add(new SetTimedDest(name, latLng, minuteTime));
    }

    public PriorityQueue<SetTimedDest> getTimedLatLngs(){ return timedLatLngs; }

    public void setLatLngs(Pair<String, LatLng> latLng) {
        latLngs.add(latLng);
    }

    public ArrayList<Pair<String, LatLng>> getLatLngs() {
        return latLngs;
    }

    public void setCurrentLocation(Pair<String, LatLng> latLng) {
        latLngs.set(0, latLng);
    }

    public void saveFragmentState(Bundle args) {
        fragmentArgs = args;
    }

    public Bundle getFragmentState() {
        return fragmentArgs;
    }
}
