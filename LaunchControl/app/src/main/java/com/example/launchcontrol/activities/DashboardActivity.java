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

import com.example.launchcontrol.R;
import com.example.launchcontrol.animations.ProgressBarAnimation;
import com.example.launchcontrol.fragments.WorkaroundMapFragment;
import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.managers.BluetoothManager;
import com.example.launchcontrol.models.DataPoint;
import com.example.launchcontrol.utilities.ChartMaker;
import com.example.launchcontrol.utilities.ReconnectSnackbarMaker;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class DashboardActivity extends AppCompatActivity implements BluetoothDataReceiver, BluetoothConnectionStatusReceiver,
        OnMapReadyCallback {

    TextView speed, rpm, runtime, runtimeUnit ,distance, fuel, oiltemp,
        calcEngineLoad, absEngineLoad, engineTorquePercentage, coolanttemp,
        engineRefTorque, intaketemp, intakePressure, baroPressure, mafPressure,
        throttlePos, ctrlVoltage, ambitemp;
    ProgressBar speedRing;
    SupportMapFragment mapFragment;
    BluetoothManager bluetoothManager;
    ScrollView scrollView;
    GoogleMap googleMap;
    FusedLocationProviderClient mFusedLocationClient;
    Location currentLocation;
    LineChart speedChart, rpmChart;
    int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Statistics
        speed = findViewById(R.id.DashboardActivity_speed);
        rpm = findViewById(R.id.DashboardActivity_rpm);
        runtime = findViewById(R.id.DashboardActivity_runtime);
        runtimeUnit = findViewById(R.id.DashboardActivity_runtime_unit);
        distance = findViewById(R.id.DashboardActivity_distance);
        speedRing = findViewById(R.id.DashboardActivity_speedring);
        fuel = findViewById(R.id.DashboardActivity_fuel);
        oiltemp = findViewById(R.id.DashboardActivity_oiltemp);
        calcEngineLoad = findViewById(R.id.DashboardActivity_calcEngineLoad);
        absEngineLoad = findViewById(R.id.DashboardActivity_absEngineLoad);
        engineTorquePercentage = findViewById(R.id.DashboardActivity_engineTorque);
        coolanttemp = findViewById(R.id.DashboardActivity_coolantTemp);
        engineRefTorque = findViewById(R.id.DashboardActivity_engineRefTorque);
        intaketemp = findViewById(R.id.DashboardActivity_intakeTemp);
        intakePressure = findViewById(R.id.DashboardActivity_intakePressure);
        baroPressure = findViewById(R.id.DashboardActivity_baroPressure);
        mafPressure = findViewById(R.id.DashboardActivity_mafPressure);
        throttlePos = findViewById(R.id.DashboardActivity_throttlePosition);
        ctrlVoltage = findViewById(R.id.DashboardActivity_controlVoltage);
        ambitemp = findViewById(R.id.DashboardActivity_ambtemp);

        //ScrollView
        scrollView = findViewById(R.id.DashboardActivity_srollView);

        //Map Setup
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.DashboardActivity_map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Chart Setup
        speedChart = findViewById(R.id.DashboardActivity_speedchart);
        rpmChart = findViewById(R.id.DashboardActivity_rpmchart);
        ChartMaker.configureChartSettings(speedChart, this);
        ChartMaker.configureChartSettings(rpmChart, this);

        //Bluetooth Setup
        bluetoothManager = BluetoothManager.getBluetoothManager(this);
        bluetoothManager.registerBluetoothDataReceiver(this);
        bluetoothManager.registerBluetoothConnectionStatusReciever(this);
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
                calcEngineLoad.setText(String.format("%03d", dataPoint.getCalculatedEngineLoad()));
                absEngineLoad.setText(String.format("%03d", dataPoint.getAbsoluteEngineLoad()));
                engineTorquePercentage.setText(String.format("%03d", dataPoint.getTorquePercentage()));
                coolanttemp.setText(String.format("%03d", dataPoint.getEngineCoolantTemperature()));
                engineRefTorque.setText(String.format("%05d", dataPoint.getReferenceTorque()));
                intaketemp.setText(String.format("%03d", dataPoint.getIntakeTemperature()));
                intakePressure.setText(String.format("%03d", dataPoint.getIntakePressure()));
                baroPressure.setText(String.format("%03d", dataPoint.getBarometricPressure()));
                mafPressure.setText(String.format("%03d", dataPoint.getFlowPressure()));
                throttlePos.setText(String.format("%03d", dataPoint.getThrottlePosition()));
                ctrlVoltage.setText(String.format("%03d", dataPoint.getControlModuleVoltage()));
                ambitemp.setText(String.format("%03d", dataPoint.getAmbientTemperature()));

                ChartMaker.addSpeedEntry(DashboardActivity.this, speedChart, dataPoint, time);
                ChartMaker.addRPMEntry(DashboardActivity.this, rpmChart, dataPoint, time);
                time++;
            }
        });
    }

    //Map callback
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                //Setup code for enabling scroll view
                DashboardActivity.this.googleMap = googleMap;
                DashboardActivity.this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                DashboardActivity.this.googleMap.getUiSettings().setZoomControlsEnabled(true);
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

    //Get current location
    private void getLocation() {
        try {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(DashboardActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                .zoom(14)
                                .bearing(90)
                                .tilt(30)
                                .build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            });
        } catch (SecurityException e) {
            String[] permissionRequested = {Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissionRequested, 2);
        }
    }

    //Bluetooth Connection Status Receiver Callbacks
    @Override
    public void onDeviceConnected(BluetoothDevice bluetoothDevice) {
        ReconnectSnackbarMaker.MakeConnectedSnackbar(scrollView);
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice bluetoothDevice, String message) {
        ReconnectSnackbarMaker.MakeReconnectSnackbar(scrollView, "The bluetooth connection has been lost! Error: " +
                message);
    }

    @Override
    public void onError(String message) {
        ReconnectSnackbarMaker.MakeReconnectSnackbar(scrollView, "There was an error with the bluetooth connection. Error: " +
                message);
    }

    @Override
    public void onConnectError(BluetoothDevice bluetoothDevice, String message) {
        ReconnectSnackbarMaker.MakeReconnectSnackbar(scrollView, "There was an error connecting with your device. Error: " +
                message);
    }
}
