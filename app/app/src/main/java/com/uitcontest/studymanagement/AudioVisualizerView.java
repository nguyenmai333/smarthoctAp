package com.uitcontest.studymanagement;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class AudioVisualizerView extends View {

    private Paint paint;
    private float[] amplitudes;
    private int width, height;
    private int index = 0;

    public AudioVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        // Waveform color and width
        paint.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        amplitudes = new float[width];  // Array for amplitude data
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (amplitudes == null) {
            return;
        }

        int middle = height / 2;  // Center line for the waveform

        for (int i = 0; i < width - 1; i++) {
            float startX = i;
            float startY = middle - amplitudes[i];
            float stopX = i + 1;
            float stopY = middle - amplitudes[i + 1];
            canvas.drawLine(startX, startY, stopX, stopY, paint);  // Draw waveform line
        }
    }

    // Update amplitudes with real-time audio data
    public void updateVisualizer(float amplitude) {
        if (amplitudes == null) {
            return;
        }

        // Shift values to left
        System.arraycopy(amplitudes, 1, amplitudes, 0, amplitudes.length - 1);

        amplitudes[amplitudes.length - 1] = amplitude;
        invalidate(); // Redraw the wave
    }

}
