/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Modifications by: Akash Patel
 * Contents of File: Login form, to login an existing user (also used as a landing page)
 */
package com.example.launchcontrol.activities;

import android.bluetooth.BluetoothDevice;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.launchcontrol.R;
import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.managers.BluetoothManager;
import com.example.launchcontrol.models.DataPoint;
import com.example.launchcontrol.utilities.PermsUtil;
import com.example.launchcontrol.utilities.ReconnectSnackbarMaker;

public class LoginActivity extends AppCompatActivity implements BluetoothDataReceiver, BluetoothConnectionStatusReceiver {

    BluetoothManager bluetoothManager;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Views
        constraintLayout = findViewById(R.id.loginActivity_rootLayout);

        PermsUtil.getPermissions(this);

        //Set up Bluetooth
        bluetoothManager = BluetoothManager.getBluetoothManager(this, null);
        bluetoothManager.registerBluetoothDataReceiver(this);
        bluetoothManager.registerBluetoothConnectionStatusReciever(this);
    }

    @Override
    public void onDataReceived(final DataPoint dataPoint) {
        //Bluetooth info will be received here
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               Toast.makeText(LoginActivity.this, dataPoint.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDeviceConnected(BluetoothDevice bluetoothDevice) {
        ReconnectSnackbarMaker.MakeConnectedSnackbar(constraintLayout);
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice bluetoothDevice, String message) {
        ReconnectSnackbarMaker.MakeReconnectSnackbar(constraintLayout, "The bluetooth connection has been lost! Error: " +
                message);
    }

    @Override
    public void onError(String message) {
        ReconnectSnackbarMaker.MakeReconnectSnackbar(constraintLayout, "There was an error with the bluetooth connection. Error: " +
                message);
    }

    @Override
    public void onConnectError(BluetoothDevice bluetoothDevice, String message) {
        ReconnectSnackbarMaker.MakeReconnectSnackbar(constraintLayout, "There was an error connecting with your device. Error: " +
                message);
    }
}
