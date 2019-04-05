/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Modifications by: Akash Patel
 * Contents of File: Dashboard screen, main page that shows all OBD-II metrics
 */
package com.example.launchcontrol.activities;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.launchcontrol.managers.SessionManager;
import com.example.launchcontrol.managers.WebSocketManager;
import com.example.launchcontrol.models.DataPoint;
import com.example.launchcontrol.utilities.ChartMaker;
import com.example.launchcontrol.utilities.PermsUtil;
import com.example.launchcontrol.utilities.SnackbarMaker;
import com.example.launchcontrol.utilities.RequestCodes;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class DashboardActivity extends AppCompatActivity implements BluetoothDataReceiver, BluetoothConnectionStatusReceiver,
        OnMapReadyCallback {

    Button logout;
    TextView speed, rpm, runtime, runtimeUnit ,distance, fuel, oiltemp,
        calcEngineLoad, absEngineLoad, engineTorquePercentage, coolanttemp,
        engineRefTorque, intaketemp, intakePressure, baroPressure, mafPressure,
        throttlePos, ctrlVoltage, ambitemp, graphspeed, graphrpm;
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
        graphspeed = findViewById(R.id.DashboardActivity_graphSpeed);
        graphrpm = findViewById(R.id.DashboardActivity_graphRPM);

        //ScrollView
        scrollView = findViewById(R.id.DashboardActivity_srollView);

        //Map Setup
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.DashboardActivity_map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissionRequested = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissionRequested, RequestCodes.LOCATION_ACCESS_CODE);
        }

        //Chart Setup
        speedChart = findViewById(R.id.DashboardActivity_speedchart);
        rpmChart = findViewById(R.id.DashboardActivity_rpmchart);
        ChartMaker.configureChartSettings(speedChart, this);
        ChartMaker.configureChartSettings(rpmChart, this);

        //Bluetooth Setup
        BluetoothManager.clearBluetoothManager(); //force clear
        bluetoothManager = BluetoothManager.getBluetoothManager(this);
        bluetoothManager.registerBluetoothDataReceiver(this);
        bluetoothManager.registerBluetoothConnectionStatusReciever(this);
        bluetoothManager.setWebSocket(WebSocketManager.getWebSocket(this, false));

        logout = findViewById(R.id.DashboardActivity_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebSocketManager.getWebSocket(DashboardActivity.this, false).disconnect();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                WebSocketManager.getWebSocket(DashboardActivity.this, false).disconnect();
                SessionManager.getSessionManager(DashboardActivity.this).setAuthenticated(false);
                bluetoothManager.unRegisterBluetoothConnectionStatusReciever(DashboardActivity.this);
                bluetoothManager.unRegisterBluetoothDataReciever(DashboardActivity.this);
                bluetoothManager.disconnectFromDevice();

                startActivity(intent);
            }
        });
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
                graphspeed.setText(String.format("%03d KPH", dataPoint.getVehicleSpeed()));
                graphrpm.setText(String.format("%05d RPM", dataPoint.getEngineRPM()));

                try
                {
                    if (dataPoint.getLatLng() != null)
                    {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(dataPoint.getLatLng()).title("Current location!")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                    }

                }
                catch (Exception e)
                {}


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
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DashboardActivity.this, R.raw.maps_json));
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
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Current location!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    }
                }
            });
        } catch (SecurityException e) {
            PermsUtil.getPermissions(this);
        }
    }

    //Bluetooth Connection Status Receiver Callbacks
    @Override
    public void onDeviceConnected(BluetoothDevice bluetoothDevice) {
        SnackbarMaker.MakeConnectedSnackbar(scrollView);
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice bluetoothDevice, String message) {
        SnackbarMaker.MakeReconnectSnackbar(scrollView, "The bluetooth connection has been lost! Error: " +
                message);
    }

    @Override
    public void onError(String message) {
        SnackbarMaker.MakeReconnectSnackbar(scrollView, "There was an error with the bluetooth connection. Error: " +
                message);
    }

    @Override
    public void onConnectError(BluetoothDevice bluetoothDevice, String message) {
        SnackbarMaker.MakeReconnectSnackbar(scrollView, "There was an error connecting with your device. Error: " +
                message);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCodes.LOCATION_ACCESS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Request for location granted", Toast.LENGTH_LONG).show();
                getLocation();
            } else {
                Toast.makeText(this, "Unable to request location services", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        WebSocketManager.getWebSocket(this, false).disconnect();
        SessionManager.getSessionManager(this).setAuthenticated(false);
        bluetoothManager.unRegisterBluetoothConnectionStatusReciever(this);
        bluetoothManager.unRegisterBluetoothDataReciever(this);
        bluetoothManager.disconnectFromDevice();
        finish();
    }
}
