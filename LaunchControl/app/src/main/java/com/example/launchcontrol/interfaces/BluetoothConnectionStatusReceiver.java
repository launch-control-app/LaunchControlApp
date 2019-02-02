package com.example.launchcontrol.interfaces;

import android.bluetooth.BluetoothDevice;

public interface BluetoothConnectionStatusReceiver {

    void onDeviceConnected(BluetoothDevice bluetoothDevice);
    void onDeviceDisconnected(BluetoothDevice bluetoothDevice, String message);

    void onError(String message);
    void onConnectError(BluetoothDevice bluetoothDevice, String message);
}
