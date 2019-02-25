/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Used for interfacing the bluetooth connection with the teensy microcontroller
 */
package com.example.launchcontrol.managers;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.example.launchcontrol.activities.DashboardActivity;
import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.models.DataPoint;
import com.example.launchcontrol.utilities.RequestCodes;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;

public class BluetoothManager implements DeviceCallback, LocationListener {

    private Bluetooth bluetooth;
    private static BluetoothManager bluetoothManager;

    private List<BluetoothDataReceiver> bluetoothDataReceivers;
    private List<BluetoothConnectionStatusReceiver> bluetoothConnectionStatusReceivers;

    private String deviceName = "DESKTOP-B4D2HN2";
    private Socket webSocket = WebSocketManager.getWebSocket();

    private Context context;
    private Location location;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    public static BluetoothManager getBluetoothManager(Context context)
    {
        if (bluetoothManager == null)
            bluetoothManager = new BluetoothManager(context);

        return bluetoothManager;
    }


    private BluetoothManager(Context context)
    {
        this.context = context;

        //configure location
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1500);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(5f);
        try
        {
            fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    if (locationResult.getLastLocation() != null)
                        BluetoothManager.this.location = locationResult.getLastLocation();
                }
            }, null);
        }
        catch (SecurityException e)
        {}


        //configure bluetooth
        bluetooth = new Bluetooth(context);
        bluetooth.setDeviceCallback(this);
        bluetoothDataReceivers = new ArrayList<>();
        bluetoothConnectionStatusReceivers = new ArrayList<>();
        bluetooth.onStart();
        bluetooth.enable();

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
        DataPoint dataPoint = new DataPoint(message);
        dataPoint.setLocation(location);
        for (BluetoothDataReceiver bluetoothDataReceiver : bluetoothDataReceivers)
            bluetoothDataReceiver.onDataReceived(dataPoint);
        //TODO: Add emit auth?
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

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
