/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Modifications by: Akash Patel
 * Contents of File: Dashboard screen, main page that shows all OBD-II metrics
 */
package com.example.launchcontrol.activities;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.launchcontrol.R;
import com.example.launchcontrol.animations.ProgressBarAnimation;
import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.managers.BluetoothManager;
import com.example.launchcontrol.models.DataPoint;

public class DashboardActivity extends AppCompatActivity implements BluetoothDataReceiver, BluetoothConnectionStatusReceiver {

    TextView speed, rpm, runtime, runtimeUnit ,distance, fuel, oiltemp;
    ProgressBar speedRing;

    BluetoothManager bluetoothManager;

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
                ProgressBarAnimation progressBarAnimation = new ProgressBarAnimation(speedRing, speedRing.getProgress(), Math.round(dataPoint.getVehicleSpeed()));

                speed.setText(String.format("%03d", dataPoint.getVehicleSpeed()));
                rpm.setText(String.format("%05d", dataPoint.getEngineRPM()));
                runtime.setText(String.format("%03d", dataPoint.getEngineRunningTime()));
                distance.setText(String.format("%03d", dataPoint.getVehicleRunningDistance()));
                fuel.setText(String.format("%03d", dataPoint.getFuelLevel()));
                oiltemp.setText(String.format("%03d", dataPoint.getEngineOilTemperature()));
                progressBarAnimation.setDuration(500);
                speedRing.startAnimation(progressBarAnimation);
            }
        });
    }
}
