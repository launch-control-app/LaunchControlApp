package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.aflak.bluetooth.DeviceCallback;

public class TemperatureFragment extends Fragment implements DeviceCallback {


    ProgressBar coolantTempProgressBar;
    TextView coolantTempValue;
    int pStatus = 0;
    private Handler handler = new Handler();

    public TemperatureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        coolantTempProgressBar = view.findViewById(R.id.coolantTemp);
        coolantTempValue = view.findViewById(R.id.txtProgress);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            coolantTempProgressBar.setProgress(pStatus);
                            coolantTempValue.setText(pStatus + " ◦C");
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();


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
    public void onMessage(String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

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
