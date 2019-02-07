/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Modifications by: Akash Patel
 * Contents of File: Dashboard screen, main page that shows all OBD-II metrics
 */
package com.example.launchcontrol.activities;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.launchcontrol.R;
import com.example.launchcontrol.animations.ProgressBarAnimation;
import com.example.launchcontrol.fragments.WorkaroundMapFragment;
import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.managers.BluetoothManager;
import com.example.launchcontrol.models.DataPoint;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class DashboardActivity extends AppCompatActivity implements BluetoothDataReceiver, BluetoothConnectionStatusReceiver, OnMapReadyCallback {

    TextView speed, rpm, runtime, runtimeUnit ,distance, fuel, oiltemp;
    ProgressBar speedRing;
    SupportMapFragment mapFragment;
    BluetoothManager bluetoothManager;
    ScrollView scrollView;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        speed = findViewById(R.id.DashboardActivity_speed);
        rpm = findViewById(R.id.DashboardActivity_rpm);
        runtime = findViewById(R.id.DashboardActivity_runtime);
        runtimeUnit = findViewById(R.id.DashboardActivity_runtime_unit);
        distance = findViewById(R.id.DashboardActivity_distance);
        speedRing = findViewById(R.id.DashboardActivity_speedring);
        fuel = findViewById(R.id.DashboardActivity_fuel);
        oiltemp = findViewById(R.id.DashboardActivity_oiltemp);

        scrollView = findViewById(R.id.DashboardActivity_srollView);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.DashboardActivity_map);
        mapFragment.getMapAsync(this);


        bluetoothManager = BluetoothManager.getBluetoothManager(this);
        bluetoothManager.registerBluetoothDataReceiver(this);
        bluetoothManager.registerBluetoothConnectionStatusReciever(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    @Override
    public void onDeviceConnected(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice bluetoothDevice, String message) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectError(BluetoothDevice bluetoothDevice, String message) {

    }

    @Override
    public void onDataReceived(final DataPoint dataPoint) {
        //Bluetooth info will be received here
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBarAnimation progressBarAnimation
                        = new ProgressBarAnimation(speedRing, speedRing.getProgress(),
                        Math.round(dataPoint.getVehicleSpeed()), speed);
                progressBarAnimation.setDuration(1000);
                speedRing.startAnimation(progressBarAnimation);

                //speed.setText(String.format("%03d", dataPoint.getVehicleSpeed()));
                rpm.setText(String.format("%05d", dataPoint.getEngineRPM()));
                runtime.setText(String.format("%03d", dataPoint.getEngineRunningTime()));
                distance.setText(String.format("%03d", dataPoint.getVehicleRunningDistance()));
                fuel.setText(String.format("%03d", dataPoint.getFuelLevel()));
                oiltemp.setText(String.format("%03d", dataPoint.getEngineOilTemperature()));

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                //Setup code for enabling scroll view
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.DashboardActivity_map))
                        .setListener(new WorkaroundMapFragment.OnTouchListener() {
                            @Override
                            public void onTouch()
                            {
                                scrollView.requestDisallowInterceptTouchEvent(true);
                            }
                        });
                getLocation();



            }
        });

    }

    private void getLocation() {
        try {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(DashboardActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        LatLng loc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(loc).title("Marker at your place!"));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                .zoom(14)
                                .bearing(90)
                                .tilt(30)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Turn on location services to use maps.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            String[] permissionRequested = {Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissionRequested, 2);

        }
    }
}
