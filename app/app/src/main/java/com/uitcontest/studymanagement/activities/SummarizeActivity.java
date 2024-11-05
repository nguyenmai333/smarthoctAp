package com.uitcontest.studymanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.api.ApiService;
import com.uitcontest.studymanagement.requests.SummarizeRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SummarizeActivity extends AppCompatActivity {

    private EditText summarizeEditText, summarizedEditText;
    private SeekBar seekBar;
    private FrameLayout progressOverlay;
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
                // Do nothing
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
        progressOverlay.setVisibility(ProgressBar.VISIBLE);
        // Handle summarize text
        String text = summarizeEditText.getText().toString();
        int progress = seekBar.getProgress();
        Log.d("Progress", "Progress: " + progress);

        // Summarize the text
        // Create request
        SummarizeRequest request = new SummarizeRequest(text, progress);
        // Call API
        Call<ResponseBody> call = ApiClient.getApiService().summarizeText(request);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        progressOverlay.setVisibility(ProgressBar.GONE);
                        String summarizedText = response.body().string();

                        // Remove json quotes "{"content":" and "}"
                        summarizedText = summarizedText.substring(12, summarizedText.length() - 2);
                        summarizedEditText.setText(summarizedText);
                        Log.d("Summarized Text", "Summarized Text: " + summarizedText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("Summarize Text", "Failed to summarize text");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressOverlay.setVisibility(ProgressBar.VISIBLE);
                Log.d("Summarize Text Error", "Error: " + t.getMessage());
            }
        });

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
        progressOverlay = findViewById(R.id.progressOverlay);

        // Make seekbar minimal value to 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(1);
        }
        // Set progress for seekbar
        seekBar.setProgress(1);
    }
}