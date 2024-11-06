package com.uitcontest.studymanagement.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.uitcontest.studymanagement.R;

public class ConvertedTextActivity extends AppCompatActivity {

    private EditText convertedText;
    private AppCompatButton summarizeButton, mindmapButton, quizButton;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converted_text);

        // Initialize view
        initializeView();

        // Get converted text
        getConvertedText();

        // Dynamically change the size of editText field
        convertedText.setMinHeight(0);
        convertedText.setMaxHeight(Integer.MAX_VALUE);

        // Add a copy button to the converted text
        convertedText.setOnLongClickListener(v -> copyText());

        // Handle summarize button click
        summarizeButton.setOnClickListener(v -> summarizeText());

        // Handle mindmap button click
        mindmapButton.setOnClickListener(v -> createMindmap());

        // Handle quiz button click
        quizButton.setOnClickListener(v -> createQuiz());

        // Handle back button click
        ivBack.setOnClickListener(v -> finish());
    }

    private void createQuiz() {
        // Handle create quiz
        Intent intent = new Intent(this, QuizActivity.class);
        // Pass the converted text
        intent.putExtra("convertedText", convertedText.getText().toString());
        startActivity(intent);
    }

    private boolean copyText() {
        String text = convertedText.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Converted Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void summarizeText() {
        // Handle summarize text
        Intent intent = new Intent(this, SummarizeActivity.class);
        // Pass the converted text
        intent.putExtra("convertedText", convertedText.getText().toString());
        startActivity(intent);
    }

    private void createMindmap() {
        // Handle create mindmap
        Intent intent = new Intent(this, MindmapActivity.class);
        // Pass the converted text
        intent.putExtra("convertedText", convertedText.getText().toString());
        startActivity(intent);
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
        quizButton = findViewById(R.id.quizButton);
        ivBack = findViewById(R.id.ivBack);
    }
}