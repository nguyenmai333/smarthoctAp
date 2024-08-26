package com.uitcontest.studymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
    }

    private void initializeView() {
        convertedText = findViewById(R.id.etConvertedText);
        summarizeButton = findViewById(R.id.summarizeButton);
        mindmapButton = findViewById(R.id.mindmapButton);
    }
}