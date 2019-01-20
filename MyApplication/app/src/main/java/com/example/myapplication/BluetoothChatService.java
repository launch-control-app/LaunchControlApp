/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService {

    private BluetoothAdapter currentBTAdapter;
    private BluetoothDevice currentCarDevice;
    private BluetoothSocket CarSocket;
    private OutputStream CarOutputStream;
    private InputStream CarInputStream;


    private String deviceName = "raspberrypi";

    Thread workerThread;
    final Handler handler = new Handler();
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    List<dataListener> mDataListeners;


    public BluetoothChatService(dataListener dataListener)
    {
        mDataListeners = new ArrayList<dataListener>();
        mDataListeners.add(dataListener);
    }

    public void tryConnection(){
        if(getCarConnection()){

            try {
                openConnectionToCar();
            } catch (IOException e) {
                // TODO: Throw error and restart
                e.printStackTrace();
                return;
            }
            startListeningForData();
        }
        // Could not connect to car
        else{
        }
    }

    public void addDataListener(dataListener dataListener)
    {
        mDataListeners.add(dataListener);
    }

    public boolean getCarConnection() {
        currentBTAdapter = BluetoothAdapter.getDefaultAdapter();

        String btDeviceName = deviceName;

        Set<BluetoothDevice> pairedDevices = currentBTAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals(btDeviceName))
                {
                    currentCarDevice = device;
                    return true;
                }
            }
        }
        return false;
    }


    void openConnectionToCar() throws IOException
    {
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        CarSocket = currentCarDevice.createRfcommSocketToServiceRecord(UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"));
        CarSocket.connect();
        CarOutputStream = CarSocket.getOutputStream();
        CarInputStream = CarSocket.getInputStream();
        //CarSocket.close();
    }

    BluetoothSocket getCurrentSocket()
    {
        return CarSocket;
    }

    void startListeningForData()
    {
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = CarInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            CarInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;



                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            //PrintData
                                            for (dataListener d: mDataListeners)
                                            {
                                                d.onDataRecieved(data);
                                            }

                                        }
                                    });

                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }


}