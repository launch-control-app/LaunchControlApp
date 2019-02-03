/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Defines an interface to receive data via bluetooth
 */
package com.example.launchcontrol.interfaces;

import com.example.launchcontrol.models.DataPoint;

public interface BluetoothDataReceiver {

    void onDataReceived(DataPoint dataPoint);

}
