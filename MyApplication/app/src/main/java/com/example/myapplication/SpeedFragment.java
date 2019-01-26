package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AlignmentSpan;
import android.text.style.LineHeightSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;

import me.aflak.bluetooth.DeviceCallback;

public class SpeedFragment extends Fragment implements DeviceCallback {

    private TextView speedLog, speedTitle;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    int counter = 0;

    public SpeedFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speed, container, false);

        speedTitle = view.findViewById(R.id.speedTitle);
        speedLog = view.findViewById(R.id.speedLog);
        speedLog.setMovementMethod(new ScrollingMovementMethod());

        graphView = view.findViewById(R.id.speedGraph);

        series = new LineGraphSeries<>();

        graphView.addSeries(series);
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(300);
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
                String speed = data[0];
                String currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                speedTitle.setText(speed + " KPH");

                final int scrollAmount = speedLog.getLayout().getLineTop(speedLog.getLineCount()) - speedLog.getHeight();

                // if there is no need to scroll, scrollAmount will be <=0
                if (scrollAmount > 0) {
                    speedLog.scrollTo(0, scrollAmount);
                } else {
                    speedLog.scrollTo(0, 0);
                }

                speedLog.append(speed + " ........................................... " + currentTime + "\n");

                if (counter < 60)
                    series.appendData(new DataPoint(counter, Integer.parseInt(speed)), false, 70);
                else
                    series.appendData(new DataPoint(counter, Integer.parseInt(speed)), true, 70);
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
