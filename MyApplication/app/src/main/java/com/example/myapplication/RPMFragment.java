package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

import me.aflak.bluetooth.DeviceCallback;

public class RPMFragment extends Fragment implements DeviceCallback {

    private TextView rpmLog, rpmTitle;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    int counter = 0;

    public RPMFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rpm, container, false);

        rpmTitle = view.findViewById(R.id.rpmTitle);
        rpmLog = view.findViewById(R.id.rpmLog);
        rpmLog.setMovementMethod(new ScrollingMovementMethod());

        graphView = view.findViewById(R.id.rpmGraph);

        series = new LineGraphSeries<>();

        graphView.addSeries(series);
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(16400);
        viewport.setScrollable(true);

        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(60);

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
                //String rpm = data[1];
                String rpm = data[0];
                String currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                rpmTitle.setText(rpm + " RPM");

                final int scrollAmount = rpmLog.getLayout().getLineTop(rpmLog.getLineCount()) - rpmLog.getHeight();

                // if there is no need to scroll, scrollAmount will be <=0
                if (scrollAmount > 0) {
                    rpmLog.scrollTo(0, scrollAmount);
                } else {
                    rpmLog.scrollTo(0, 0);
                }

                rpmLog.append(rpm + " ........................................... " + currentTime + "\n");

                if (counter < 60)
                    series.appendData(new DataPoint(counter, Integer.parseInt(rpm)), false, 70);
                else
                    series.appendData(new DataPoint(counter, Integer.parseInt(rpm)), true, 70);
                counter++;
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
