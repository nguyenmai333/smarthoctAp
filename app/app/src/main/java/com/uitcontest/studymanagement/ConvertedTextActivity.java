package com.uitcontest.studymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class ConvertedTextActivity extends AppCompatActivity {

    private EditText convertedText;
    private AppCompatButton summarizeButton, mindmapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converted_text);

        // Initialize view
        initializeView();

        // Get converted text
        getConvertedText();

        // Handle summarize button click
        summarizeButton.setOnClickListener(v -> {
            // Handle summarize button click
        });

        // Handle mindmap button click
        mindmapButton.setOnClickListener(v -> {
            // Handle mindmap button click
        });
    }

    private void getConvertedText() {
        // Handle get converted text
        Intent intent = getIntent();
        String text = intent.getStringExtra("convertedText");
        convertedText.setText(text);
    }

    private void initializeView() {
        convertedText = findViewById(R.id.etConvertedText);
        summarizeButton = findViewById(R.id.summarizeButton);
        mindmapButton = findViewById(R.id.mindmapButton);
    }
}