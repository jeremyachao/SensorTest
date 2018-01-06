package com.imb.jeremy.sensortest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //location requests
    private static final long REQ_INTERVAL = 4000;
    private static final long REQ_FINTERVAL = 3000;
    //map circle
    private static final double RADIUS_VAL = 300;
    private static final double RADIUS_CL_VAL = 25;
    //data point val arraylist size
    private static final int DETERMINE_AL_VAL = 1;

    private static final String TAG = "MapsActivity**";

    //views
    @BindView(R.id.latitutdefield)
    EditText latitutdefield;
    @BindView(R.id.longitudefield)
    EditText longitudefield;
    @BindView(R.id.setdestbtn)
    Button setdestbtn;
    @BindView(R.id.callcustomerbtn)
    Button callcustomerbtn;
    @BindView(R.id.sensorTV)
    TextView sensorTV;
    @BindView(R.id.frameTop)
    FrameLayout frameTop;
    @BindView(R.id.simbtn)
    Button simbtn;


    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ArrayList<LatLng> cla = new ArrayList<>();
    private ArrayList<LatLng> desta = new ArrayList<>();
    private ArrayList<LatLng> prevCL = new ArrayList<>();
    private int i =1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        frameTop.setVisibility(View.GONE);

        simbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Simulation.begin) {
                    Simulation.begin = false;
                    Log.d(TAG, "onClick: " + Simulation.begin);
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                } else {
                    Simulation.begin = true;
                    stopLocationUpdates();
                    Log.d(TAG, "Simulation Status: " + Simulation.begin);
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }

            }
        });

        if (Simulation.begin) {
            cla.clear();
            desta.clear();
            prevCL.clear();

        } else {
            //calls back every n seconds defined by REQ_INTERVAL
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.d(TAG, "onLocationResult: LOCATION UPDATE");
                    updateHints(locationResult);
                    for (Location location : locationResult.getLocations()) {
                        LatLng updatecl = new LatLng(location.getLatitude(), location.getLongitude());
                        cla.clear();
                        cla.add(updatecl);
                        prevCL.add(updatecl);
                        mMap.addMarker(new MarkerOptions().position(updatecl).title("current location"));
                        mMap.addCircle(new CircleOptions().center(updatecl).radius(RADIUS_CL_VAL).strokeColor(Color.RED));

                        if (!desta.isEmpty()) {
                            if (SphericalUtil.computeDistanceBetween(updatecl, desta.get(0)) <= RADIUS_VAL) {
                                Log.d(TAG, "onLocationResult: WITHIN RADIUS");
                                frameTop.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "onLocationResult: OUTSIDE RADIUS");
                                frameTop.setVisibility(View.GONE);
                            }
                        }

                        determineProg(prevCL);


                    }
                }


            };
        }

    }

    private void determineProg(ArrayList<LatLng> prevCL) {
        if (prevCL.size() > DETERMINE_AL_VAL) {
            int fromv = prevCL.size() - 2;
            int tov = prevCL.size() - 1;
            LatLng from = prevCL.get(fromv);
            LatLng to = prevCL.get(tov);
            if (SphericalUtil.computeDistanceBetween(from, to) <= RADIUS_CL_VAL) {
                Toast.makeText(MapsActivity.this, "LOW PROGRESS", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "determineProg: GOOD PROGRESS");
            }
        }
    }

    private void updateHints(LocationResult locationResult) {
        String s = String.valueOf(locationResult.getLastLocation().getLatitude());
        String x = String.valueOf(locationResult.getLastLocation().getLongitude());
        latitutdefield.setHint(s);
        longitudefield.setHint(x);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "User denied permission.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Simulation.begin) {
            ArrayList<LatLng> cl = new ArrayList<>();
            cl.add(new LatLng(51.48, -0.05));
            cl.add(new LatLng(51.48, -0.051));
            cl.add(new LatLng(51.48, -0.0515));
            cl.add(new LatLng(51.48, -0.05153));
            cl.add(new LatLng(51.48, -0.05154));
            cl.add(new LatLng(51.48, -0.05155));
            cl.add(new LatLng(51.48, -0.055));
            cl.add(new LatLng(51.48, -0.060));
            cl.add(new LatLng(51.48, -0.065));
            cl.add(new LatLng(51.48, -0.069));

            if (!cl.isEmpty()) {
                mMap.addMarker(new MarkerOptions().position(cl.get(0)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cl.get(i), 15));
                latitutdefield.setHint("SIMULATION");
                longitudefield.setHint("SIMULATION");
                setdestbtn.setVisibility(View.GONE);
                startSimulationProcess(cl, mMap);
            }
        } else {
            setdestbtn.setVisibility(View.VISIBLE);
            goToCurrentLocation(mMap);
        }
    }

    private void startSimulationProcess(final ArrayList<LatLng> array, final GoogleMap mMap) {
        Toast.makeText(MapsActivity.this, "Running...", Toast.LENGTH_SHORT).show();
        sensorTV.setText("APPROACHING...");
        //dest
        final LatLng dest = new LatLng(51.48, -0.07);
        mMap.addMarker(new MarkerOptions().position(dest).alpha(0.5f));
        mMap.addCircle(new CircleOptions().center(dest).radius(RADIUS_VAL).strokeColor(Color.BLUE));

        //current location
        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (i>=array.size()) {
                            Simulation.fromv =0;
                            Simulation.tov = 0;
                            Toast.makeText(MapsActivity.this, "end", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "END: fromv" + Simulation.fromv );
                        } else {
                            mMap.addMarker(new MarkerOptions().position(array.get(i)).alpha(0.25f));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(array.get(i), 15));
                            mMap.addCircle(new CircleOptions().center(array.get(i)).radius(RADIUS_CL_VAL).strokeColor(Color.RED));

                            //bounds
                            if (SphericalUtil.computeDistanceBetween(array.get(i), dest) <= RADIUS_VAL) {
                                sensorTV.setText("NEAR DESTINATION");
                                frameTop.setVisibility(View.VISIBLE);
                            } else {
                                sensorTV.setText("APPROACHING...");
                                Log.d(TAG, "onLocationResult: OUTSIDE RADIUS");
                                frameTop.setVisibility(View.GONE);
                            }

                            //progress
                            if (array.size()>DETERMINE_AL_VAL) {

                                for (int x = 0; x <array.size()-1;x++) {
                                    Simulation.fromv = x;
                                    Log.d(TAG, "FROM: " + Simulation.fromv);
                                    Log.d(TAG, "FROM: " + array.get(Simulation.fromv));
                                    Simulation.tov = Simulation.fromv+1;
                                    Log.d(TAG, "TO: " + Simulation.tov);
                                    Log.d(TAG, "TO: " + array.get(Simulation.tov));
                                    LatLng from = array.get(Simulation.fromv);
                                    LatLng to = array.get(Simulation.tov);
                                    //cl bounds
                                    if (SphericalUtil.computeDistanceBetween(from, to) <= RADIUS_CL_VAL) {
                                        Log.d(TAG, "LOW PROGRESS ");
                                    } else {
                                        Log.d(TAG, "GOOD PROGRESS");
                                    }
                                }


                            }
                            i++;
                            handler.postDelayed(this, 1000);
                        }
                    }
                }, 500);
            }
        };

        r.run();
    }


    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (Simulation.begin) {

        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        mFusedLocationClient.requestLocationUpdates(createLocationRequest(), mLocationCallback, null);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(REQ_INTERVAL);
        mLocationRequest.setFastestInterval(REQ_FINTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void setDestinationMarker(GoogleMap map, double lat, double lng) {
        if (!cla.isEmpty()) {
            map.clear();
            LatLng b = new LatLng(lat, lng);

            desta.clear();
            desta.add(b);

            map.addMarker(new MarkerOptions().position(cla.get(0)).title("current location"));
            map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("destination"));
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(b).include(cla.get(0)).build(), 50));

            map.addCircle(new CircleOptions()
                    .center(b).radius(RADIUS_VAL).strokeColor(Color.BLUE));
        } else {
            Toast.makeText(this, "array error", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToCurrentLocation(final GoogleMap mMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            Toast.makeText(this, "Allow permission", Toast.LENGTH_SHORT).show();
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Add a marker @ user's current location
                            LatLng cl = new LatLng(location.getLatitude(), location.getLongitude());
                            cla.add(cl);
                            mMap.addMarker(new MarkerOptions().position(cl).title("current"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cl, 15));

                            String s = String.valueOf(cl.latitude);
                            String x = String.valueOf(cl.longitude);
                            latitutdefield.setHint(s);
                            longitudefield.setHint(x);

                        } else {
                            Toast.makeText(MapsActivity.this, "location null", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    @OnClick(R.id.setdestbtn)
    public void onViewClicked() {
        if (latitutdefield.getText().toString().matches("") && longitudefield.getText().toString().matches("")) {

        } else {
            String s = latitutdefield.getText().toString();
            String y = longitudefield.getText().toString();
            setDestinationMarker(mMap, Double.parseDouble(s), Double.parseDouble(y));
        }
    }

}
