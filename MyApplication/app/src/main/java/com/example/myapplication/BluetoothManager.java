package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DeviceCallback;

public class BluetoothManager implements  DeviceCallback {
    private Bluetooth bluetooth;
    private static BluetoothManager bluetoothManager;
    private List<DeviceCallback> deviceCallbacks;

    private String deviceName = "DESKTOP-B4D2HN2";

    public static BluetoothManager getBluetoothManager(Context context)
    {
        if (bluetoothManager == null)
            bluetoothManager = new BluetoothManager(context);

        return bluetoothManager;


    }

    private BluetoothManager(Context context)
    {
        bluetooth = new Bluetooth(context);
        bluetooth.setDeviceCallback(this);
        deviceCallbacks = new ArrayList<DeviceCallback>();
        bluetooth.onStart();
        bluetooth.enable();

        List<BluetoothDevice> bluetoothDeviceList = bluetooth.getPairedDevices();

        for (BluetoothDevice bluetoothDevice: bluetoothDeviceList)
        {
            if (bluetoothDevice.getName().equals(deviceName))
            {
                bluetooth.connectToAddress(bluetoothDevice.getAddress());
            }

        }
    }

    public void setDeviceCallback(DeviceCallback deviceCallback)
    {
        deviceCallbacks.add(deviceCallback);
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) {

    }

    @Override
    public void onMessage(String message) {
        for (DeviceCallback deviceCallback : deviceCallbacks)
        {
            deviceCallback.onMessage(message);
        }
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {

    }
}
