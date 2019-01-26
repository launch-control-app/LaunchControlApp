package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class DetailsActivity extends AppCompatActivity {

    private TextView mTextView;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        graphView = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3)
//        });
        series = new LineGraphSeries<>();

        graphView.addSeries(series);
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(1000);
        viewport.setScrollable(true);

        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(100);


    }

//    @Override
//    public void onDataRecieved(String data) {
//        mTextView.append(data + "\n");
//
//        final int scrollAmount = mTextView.getLayout().getLineTop(mTextView.getLineCount()) - mTextView.getHeight();
//        // if there is no need to scroll, scrollAmount will be <=0
//        if (scrollAmount > 0)
//            mTextView.scrollTo(0, scrollAmount);
//        else
//            mTextView.scrollTo(0, 0);
//        //mTextView.setText(data + "\n");
//
////        graphView.addSeries(
////                new DataPoint(counter, Integer.parseInt(data.split(" ")[0]))
////
////        }));
//
//        series.appendData(new DataPoint(counter, Integer.parseInt(data.split(" ")[0])), false, 20);
//        counter++;
//    }
}
