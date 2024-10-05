package com.uitcontest.studymanagement.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Outline;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.SeekBar;

import com.uitcontest.studymanagement.R;

public class SummarizeActivity extends AppCompatActivity {

    private EditText summarizeEditText, summarizedEditText;
    private SeekBar seekBar;
    private AppCompatButton summarizeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summarize);

        // Initialize view
        initializeView();

        // Get converted text from previous context
        getConvertedText();

        // Handle summarize button click
        summarizeButton.setOnClickListener(v -> summarizeText());

        // Handle seekbar change
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                summarizeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void summarizeText() {
        // Handle summarize text
        String text = summarizeEditText.getText().toString();
        int progress = seekBar.getProgress();
        Log.d("Progress", "Progress: " + progress);
        summarizedEditText.setText(text.substring(0, progress));
    }

    private void getConvertedText() {
        // Get the converted text from the previous context
        String convertedText = getIntent().getStringExtra("convertedText");
        summarizeEditText.setText(convertedText);
        Log.d("Converted Text", "Converted Text: " + convertedText);
    }

    private void initializeView() {
        summarizeEditText = findViewById(R.id.etSummarizeText);
        summarizedEditText = findViewById(R.id.etSummarizedText);
        seekBar = findViewById(R.id.seekBar);
        summarizeButton = findViewById(R.id.summarizeButton);

        // Set outline for seekbar
        seekBar.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        seekBar.setClipToOutline(true);

        // Set progress for seekbar
        seekBar.setProgress(0);
    }
}