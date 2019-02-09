/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Utilities class that generates pre-defined snackbars for BT reconnect requests
 */
package com.example.launchcontrol.utilities;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.launchcontrol.managers.BluetoothManager;

public class ReconnectSnackbarMaker {

    public static void MakeReconnectSnackbar(View rootView, String message)
    {
        final Snackbar snackbar = Snackbar
                .make(rootView, message, Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Reconnect", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BluetoothManager.getBluetoothManager(v.getContext(), null).tryReconnectToDevice();
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public static void MakeConnectedSnackbar(View rootView)
    {
        final Snackbar snackbar = Snackbar
                .make(rootView, "Device reconnected!", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
