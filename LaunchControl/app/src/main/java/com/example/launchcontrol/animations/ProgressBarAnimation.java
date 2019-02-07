/**
 * Creation Date: Saturday, February 2nd 2019
 * Original Author: Akash Patel
 * Modifications by: Rohan Rao
 * Contents of File: To define an interpolated animation for the progress bar
 */
package com.example.launchcontrol.animations;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {

    private ProgressBar progressBar;
    private TextView textView;
    private float from;
    private float  to;

    public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    public ProgressBarAnimation(ProgressBar progressBar, float from, float to, TextView textView)
    {
        this(progressBar, from, to);
        this.textView = textView;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
        textView.setText(String.format("%03d",  (int)value));
    }
}
