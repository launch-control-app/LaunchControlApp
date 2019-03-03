/**
 * Creation Date: Saturday, March 2nd 2019
 * Original Author: Rohan Rao
 * Contents of File: Utility class to create charts
 */

package com.example.launchcontrol.utilities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

import com.example.launchcontrol.R;
import com.example.launchcontrol.models.DataPoint;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class ChartMaker {

    public static void configureChartSettings(LineChart lineChart, Activity activity)
    {
        // Set Description
        Description description = new  Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.setNoDataText("You need to provide data for the chart.");

        LinearGradient gradient = new LinearGradient(
                0, 0, 1400, 0,
                ContextCompat.getColor(activity, R.color.gradient_start),
                ContextCompat.getColor(activity, R.color.gradient_end),
                Shader.TileMode.CLAMP);

        Paint paint = lineChart.getRenderer().getPaintRender();
        paint.setShader(gradient);

        // Enable scaling, dragging, gestures
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);

        // Set background color
        lineChart.setBackgroundColor(activity.getResources().getColor(R.color.backgroundNormal));

        // Add empty data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        lineChart.setData(data);

        // Get font
        Typeface tf = ResourcesCompat.getFont(activity, R.font.poppins_light);

        // Configure Legend
        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        //Configure X axis
        XAxis xl = lineChart.getXAxis();
        xl.setTypeface(tf);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setEnabled(true);

        //Configure Y Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    public static void addSpeedEntry(Activity activity, LineChart speedChart, DataPoint dataPoint, int time) {

        LineData data = speedChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createDataSet("Speed [km/h]", activity);
                data.addDataSet(set);
            }

            // Add new entry
            Entry entry = new Entry();
            entry.setX(time);
            entry.setY(dataPoint.getVehicleSpeed());
            data.addEntry(entry, 0);

            // Update chart
            speedChart.notifyDataSetChanged();
            speedChart.setVisibleXRangeMaximum(10);

            // Move to the latest entry
            speedChart.moveViewToX(time);
        }
    }

    public  static void addRPMEntry(Activity activity, LineChart rpmChart, DataPoint dataPoint, int time) {
        LineData data = rpmChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createDataSet("RPM", activity);
                data.addDataSet(set);
            }

            // Add new entry
            Entry entry = new Entry();
            entry.setX(time);
            entry.setY(dataPoint.getEngineRPM());
            data.addEntry(entry, 0);

            // Update chart
            rpmChart.notifyDataSetChanged();
            rpmChart.setVisibleXRangeMaximum(10);

            // Move to the latest entry
            rpmChart.moveViewToX(time);
        }

    }

    private static LineDataSet createDataSet(String legendName, Activity activity) {
        LineDataSet set = new LineDataSet(null, legendName);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(3);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        return set;
    }



}
