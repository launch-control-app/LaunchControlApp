/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Registration form, to sign up a new user
 */
package com.example.launchcontrol.activities;

import android.bluetooth.BluetoothDevice;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.Button;

import com.example.launchcontrol.R;
import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.managers.BluetoothManager;
import com.example.launchcontrol.models.DataPoint;

public class RegisterActivity extends AppCompatActivity implements BluetoothDataReceiver, BluetoothConnectionStatusReceiver {

    BluetoothManager bluetoothManager;
    ConstraintLayout constraintLayout;

    TextInputEditText email, vin, password, verify_password;

    Button sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get view
        constraintLayout = findViewById(R.id.registerActivity_rootLayout);

        //Set up Bluetooth
        bluetoothManager = BluetoothManager.getBluetoothManager(this, null);
        bluetoothManager.registerBluetoothDataReceiver(this);
        bluetoothManager.registerBluetoothConnectionStatusReciever(this);

        email = findViewById(R.id.registerActivity_email);
        vin = findViewById(R.id.registerActivity_vin);
        password = findViewById(R.id.registerActivity_password);
        verify_password = findViewById(R.id.registerActivity_verify_password);

        sign_up = findViewById(R.id.registerActivity_sign_up);
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
                // Only try fetching VIN if the field is empty
                if (TextUtils.isEmpty(vin.getText().toString())) {
                    vin.setText(dataPoint.getVIN());
                }
            }
        });
    }
}
