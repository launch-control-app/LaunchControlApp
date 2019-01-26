package com.example.myapplication;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {

    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;
    private float from;
    private float  to;

    public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    public ProgressBarAnimation(ProgressBar progressBar, float from, float to, TextView textView, ImageView imageView)
    {
        this(progressBar, from, to);
        this.textView = textView;
        this.imageView = imageView;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
        if (textView != null)
            textView.setText(String.valueOf((int) value));
            imageView.setImageAlpha((int) (value * 2.55));

    }
}
