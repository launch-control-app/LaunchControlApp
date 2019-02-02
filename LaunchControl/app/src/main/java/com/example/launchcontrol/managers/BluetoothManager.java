package com.example.launchcontrol.managers;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.models.DataPoint;
import com.github.nkzawa.socketio.client.Socket;

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
        for (BluetoothDataReceiver bluetoothDataReceiver : bluetoothDataReceivers)
            bluetoothDataReceiver.onDataReceived(dataPoint);
        webSocket.emit(dataPoint.toString()); //this happens on a different thread, so low perf overhead
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
}
