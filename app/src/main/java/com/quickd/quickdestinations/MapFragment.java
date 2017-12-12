package com.quickd.quickdestinations;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.quickd.quickdestinations.POJO.Example;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private GoogleMap mMap;
    private int minTime;

    GoogleApiClient mGoogleApiClient;
    //Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ProgressDialog progressDialog;

    LatLng origin;
    LatLng dest;
    TextView ShowDistanceDuration;
    ArrayList<Integer> colors = new ArrayList<>();
    Polyline line;

    boolean test;
    boolean hasEligible;
    int removeIndex;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading map...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);
        colors.add(Color.CYAN);

        minTime = Integer.MAX_VALUE;
        ShowDistanceDuration = view.findViewById(R.id.tvDistanceTime);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //show error dialog if Google Play Services not available
        if (!isGooglePlayServicesAvailable()) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.");
            getActivity().finish();
        } else {
            Log.d("onCreate", "Google Play Services available. Continuing.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_area);
        mapFragment.getMapAsync(this);

        view.findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = view.findViewById(R.id.btnStart);
                button.setText("Next");

                minTime = Integer.MAX_VALUE;
                int size = ((MainActivity) getActivity()).getLatLngs().size();
                if (size > 1) {
                    if(line != null) {
                        line.remove();
                        mMap.clear();
                        //FAKE
                        origin = ((MainActivity) getActivity()).getLatLngs().remove(removeIndex).second;
                        //((MainActivity) getActivity()).getLatLngs().remove(removeIndex);
                        ((MainActivity) getActivity()).getDestinations().remove(removeIndex-1);
                        int i = 0;
                        for (Pair<String, LatLng> temp : ((MainActivity) getActivity()).getLatLngs()) {
                            if (i != 0)
                                mMap.addMarker(new MarkerOptions().position(temp.second).title(temp.first));
                            i++;
                        }
                    }
                    size = ((MainActivity) getActivity()).getLatLngs().size();
                    int c = 0;
                    //FAKE
                    if(line == null)
                        origin = ((MainActivity) getActivity()).getLatLngs().get(0).second;

                    //origin = ((MainActivity) getActivity()).getLatLngs().get(0).second;

                    for (int i = 1; i < size; i++) {
                        dest = ((MainActivity) getActivity()).getLatLngs().get(i).second;
                        buildPolyline("driving", colors.get(c), i);
                        if (i < colors.size() - 1) { c++; }
                        else { c = 0; }
                    }
                    if(!hasEligible && !((MainActivity) getActivity()).getTimedLatLngs().isEmpty()){
                        dest = ((MainActivity) getActivity()).getTimedLatLngs().remove().latLong;
                        buildPolyline("driving", colors.get(c), 0);
                    }
                    else
                        hasEligible = false;
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        int i = 0;
        for (Pair<String, LatLng> temp : ((MainActivity) getActivity()).getLatLngs()) {
            if (i != 0)
                mMap.addMarker(new MarkerOptions().position(temp.second).title(temp.first));
            i++;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        /*
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        */

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        /*
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        */
        ((MainActivity) getActivity()).setCurrentLocation(new Pair("Me", latLng));

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        progressDialog.dismiss();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private void buildPolyline(final String type, final int c, final int index) {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", origin.latitude + "," + origin.longitude, dest.latitude + "," + dest.longitude, type);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                try {
                    //Remove previous line from map
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        int timeTemp = Integer.parseInt(time.substring(0, time.indexOf(" ")));
                        if(timeTemp <= minTime) {
                            if(isEligible(dest, timeTemp, type)) {
                                minTime = timeTemp;
                                if(line != null)
                                    line.remove();
                                removeIndex = index;
                                ShowDistanceDuration.setText("Distance:" + distance + ", Duration:" + time);
                                String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                                List<LatLng> list = parsePolyline(encodedString);
                                line = mMap.addPolyline(new PolylineOptions()
                                        .addAll(list)
                                        .width(5)
                                        //.color(Color.RED)
                                        .color(c)
                                        .geodesic(true)
                                );
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }

    private List<LatLng> parsePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    // Checking if Google Play Services Available or not
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public boolean isEligible(LatLng latLong, final int firstTime, String type){
        return true;
        /*
        if(((MainActivity) getActivity()).getTimedLatLngs().isEmpty()) {
            hasEligible = true;
            return true;
        }
        else {
            LatLng temp = ((MainActivity) getActivity()).getTimedLatLngs().peek().latLong;
            if(latLong.equals(temp))
                return false;
            final int maxTime = ((MainActivity) getActivity()).getTimedLatLngs().peek().minuteTime;
            test = false;

            int hour = Calendar.getInstance().get(Calendar.HOUR);
            int min = Calendar.getInstance().get(Calendar.MINUTE);
            min += hour*60;
            final int currTime = min;
            String url = "https://maps.googleapis.com/maps/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitMaps service = retrofit.create(RetrofitMaps.class);

            Call<Example> call = service.getDistanceDuration("metric", latLong.latitude + "," + latLong.longitude,
                    temp.latitude + "," + temp.longitude, type);
            call.enqueue(new Callback<Example>() {
                @Override
                public void onResponse(Call<Example> call, Response<Example> response) {
                    try {
                        //Remove previous line from map
                        // This loop will go through all the results and add marker on each location.
                        String time = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                        int timeTemp = Integer.parseInt(time.substring(0, time.indexOf(" ")));
                        if(timeTemp + firstTime + currTime <= maxTime-15){
                            test = true;
                            hasEligible = true;
                        }
                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<Example> call, Throwable t) {
                    Log.d("onFailure", t.toString());
                }
            });
            //String time = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
            return test;
        }
        */
    }
}