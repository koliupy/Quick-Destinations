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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<Pair<String, String>> destinations = new ArrayList<>();
    public ArrayList<Pair<String, LatLng>> latLngs = new ArrayList<>();
    public static boolean homeState = false;
    public static boolean loggedIn = false;
    public Bundle fragmentArgs;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer;
        if (loggedIn == true)
            drawer = (DrawerLayout) findViewById(R.id.logout_layout);
        else
            drawer = (DrawerLayout) findViewById(R.id.login_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer;
        if (loggedIn == true)
            drawer = (DrawerLayout) findViewById(R.id.logout_layout);
        else
            drawer = (DrawerLayout) findViewById(R.id.login_layout);
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
            drawer = (DrawerLayout) findViewById(R.id.logout_layout);
        else
            drawer = (DrawerLayout) findViewById(R.id.login_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void rmDestinations(int position){

        destinations.remove(position);
        latLngs.remove(position+1);

    }

    public ArrayList<Pair<String, String>> getDestinations() {
        return destinations;
    }

    public void setDestinations(Pair<String, String> destination) {
        destinations.add(destination);
    }

    public ArrayList<Pair<String, LatLng>> getLatLngs() {
        return latLngs;
    }

    public void setCurrentLocation(Pair<String, LatLng> latLng) {
        latLngs.set(0, latLng);
    }

    public void setLatLngs(Pair<String, LatLng> latLng) {
        latLngs.add(latLng);
    }

    public void saveFragmentState(Bundle args) {
        fragmentArgs = args;
    }

    public Bundle getFragmentState() {
        return fragmentArgs;
    }
}
