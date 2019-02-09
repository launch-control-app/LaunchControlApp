/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Used for interfacing the bluetooth connection with the teensy microcontroller
 */
package com.example.launchcontrol.managers;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.location.Location;

import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.models.DataPoint;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;

public class BluetoothManager implements DeviceCallback {

    private Bluetooth bluetooth;
    private static BluetoothManager bluetoothManager;

    private List<BluetoothDataReceiver> bluetoothDataReceivers;
    private List<BluetoothConnectionStatusReceiver> bluetoothConnectionStatusReceivers;

    private String deviceName = "DESKTOP-B4D2HN2";
    private Socket webSocket = WebSocketManager.getWebSocket();

    private Context context;
    private Location location;

    public static BluetoothManager getBluetoothManager(Context context)
    {
        if (bluetoothManager == null)
            bluetoothManager = new BluetoothManager(context);

        return bluetoothManager;
    }

    private BluetoothManager(Context context)
    {
        this.context = context;
        bluetooth = new Bluetooth(context);
        bluetooth.setDeviceCallback(this);
        bluetoothDataReceivers = new ArrayList<>();
        bluetoothConnectionStatusReceivers = new ArrayList<>();
        bluetooth.onStart();
        bluetooth.enable();
        getLocation();
        connectToDevice();
    }

    private void connectToDevice()
    {
        List<BluetoothDevice> bluetoothDeviceList = bluetooth.getPairedDevices();
        boolean foundDevice = false;
        for (BluetoothDevice bluetoothDevice: bluetoothDeviceList)
        {
            if (bluetoothDevice.getName().equals(deviceName))
            {
                bluetooth.connectToAddress(bluetoothDevice.getAddress());
                foundDevice = true;

            }
        }
        if (!foundDevice)
            this.onConnectError(null, "Unable to find device");
    }

    public void tryReconnectToDevice()
    {
        connectToDevice();
    }

    public void registerBluetoothDataReceiver(BluetoothDataReceiver bluetoothDataReceiver)
    {
        if (!bluetoothDataReceivers.contains(bluetoothDataReceiver))
            bluetoothDataReceivers.add(bluetoothDataReceiver);
    }

    public void unRegisterBluetoothDataReciever(BluetoothDataReceiver bluetoothDataReceiver)
    {
        if (bluetoothDataReceivers.contains(bluetoothDataReceiver))
            bluetoothDataReceivers.remove(bluetoothDataReceiver);
    }

    public void registerBluetoothConnectionStatusReciever(BluetoothConnectionStatusReceiver bluetoothConnectionStatusReceiver)
    {
        if (!bluetoothConnectionStatusReceivers.contains(bluetoothConnectionStatusReceiver))
            bluetoothConnectionStatusReceivers.add(bluetoothConnectionStatusReceiver);
    }

    public void unRegisterBluetoothConnectionStatusReciever(BluetoothConnectionStatusReceiver bluetoothConnectionStatusReceiver)
    {
        if (bluetoothConnectionStatusReceivers.contains(bluetoothConnectionStatusReceiver))
            bluetoothConnectionStatusReceivers.remove(bluetoothConnectionStatusReceiver);
    }

    @Override
    public void onMessage(String message) {
        getLocation();
        DataPoint dataPoint = new DataPoint(message);
        dataPoint.setLocation(location);
        for (BluetoothDataReceiver bluetoothDataReceiver : bluetoothDataReceivers)
            bluetoothDataReceiver.onDataReceived(dataPoint);
        webSocket.emit("data", dataPoint.toString()); //this happens on a different thread, so low perf overhead
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        for (BluetoothConnectionStatusReceiver bluetoothConnectionStatusReceiver : bluetoothConnectionStatusReceivers)
            bluetoothConnectionStatusReceiver.onDeviceConnected(device);
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) {
        for (BluetoothConnectionStatusReceiver bluetoothConnectionStatusReceiver : bluetoothConnectionStatusReceivers)
            bluetoothConnectionStatusReceiver.onDeviceDisconnected(device, message);
    }

    @Override
    public void onError(String message) {
        for (BluetoothConnectionStatusReceiver bluetoothConnectionStatusReceiver : bluetoothConnectionStatusReceivers)
            bluetoothConnectionStatusReceiver.onError(message);
    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {
        for (BluetoothConnectionStatusReceiver bluetoothConnectionStatusReceiver : bluetoothConnectionStatusReceivers)
            bluetoothConnectionStatusReceiver.onConnectError(device, message);
    }

    private void getLocation()
    {
        try
        {
            LocationServices.getFusedLocationProviderClient(context).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    BluetoothManager.this.location = location;
                }
            }) ;
        }
        catch (SecurityException e)
        { }
    }
}
