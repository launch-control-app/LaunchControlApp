/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Modifications by: Akash Patel
 * Contents of File: Dashboard screen, main page that shows all OBD-II metrics
 */
package com.example.launchcontrol.activities;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
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

public class DashboardActivity extends AppCompatActivity implements BluetoothDataReceiver, BluetoothConnectionStatusReceiver, OnMapReadyCallback,
        OnChartValueSelectedListener {

    TextView speed, rpm, runtime, runtimeUnit ,distance, fuel, oiltemp;
    ProgressBar speedRing;
    SupportMapFragment mapFragment;
    BluetoothManager bluetoothManager;
    ScrollView scrollView;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location currentLocation;
    private LineChart mChart;
    private int counter = 0;

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

        mChart = findViewById(R.id.DashboardActivity_speedchart);
        mChart.setOnChartValueSelectedListener(this);
        configureChart();
        //addEntry();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();


        bluetoothManager = BluetoothManager.getBluetoothManager(this);
        bluetoothManager.registerBluetoothDataReceiver(this);
        bluetoothManager.registerBluetoothConnectionStatusReciever(this);
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

                addEntry(dataPoint);

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
                        //LatLng loc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(loc).title("Marker at your place!"));
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                .zoom(14)
                                .bearing(90)
                                .tilt(30)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            });
        } catch (SecurityException e) {
            String[] permissionRequested = {Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissionRequested, 2);
        }
    }

    private void configureChart()
    {
        // no description text
        Description description = new  Description();
        description.setText("");
        mChart.setDescription(description);

        mChart.setNoDataText("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(tf);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTypeface(tf);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaxValue(100f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry(DataPoint dataPoint) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            Entry entry = new Entry();
            entry.setX(counter);
            entry.setY(dataPoint.getFuelLevel());
            // add a new x-value first
            data.addEntry(entry, 0);
            counter++;
            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(100);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(counter);

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
