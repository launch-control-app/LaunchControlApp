/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Defines an interface to receive connection statuses via bluetooth
 */
package com.example.launchcontrol.interfaces;

import android.bluetooth.BluetoothDevice;

public interface BluetoothConnectionStatusReceiver {

    void onDeviceConnected(BluetoothDevice bluetoothDevice);
    void onDeviceDisconnected(BluetoothDevice bluetoothDevice, String message);

    void onError(String message);
    void onConnectError(BluetoothDevice bluetoothDevice, String message);
}
