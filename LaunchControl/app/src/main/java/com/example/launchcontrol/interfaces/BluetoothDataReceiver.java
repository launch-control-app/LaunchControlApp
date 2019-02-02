package com.example.launchcontrol.interfaces;

import com.example.launchcontrol.models.DataPoint;

public interface BluetoothDataReceiver {

    void onDataReceived(DataPoint dataPoint);

}
