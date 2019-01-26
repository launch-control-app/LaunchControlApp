
package com.example.myapplication;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.aflak.bluetooth.DeviceCallback;


public class HomeFragment extends Fragment implements DeviceCallback {

    TextView speedText, rpmText;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        speedText = view.findViewById(R.id.speed);
        rpmText = view.findViewById(R.id.rpm);

        Context context = view.getContext();
        BluetoothManager.getBluetoothManager(context).setDeviceCallback(this);

        return view;
    }


    @Override
    public void onDeviceConnected(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) {

    }

    @Override
    public void onMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] data = message.split(";");
                speedText.setText(data[0] + " KPH");
                rpmText.setText(data[1] + " RPM");
            }
        });
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {

    }
}