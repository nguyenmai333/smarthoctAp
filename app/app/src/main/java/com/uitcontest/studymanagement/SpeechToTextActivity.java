package com.uitcontest.studymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.api.ApiService;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeechToTextActivity extends AppCompatActivity {

    private ImageView recordButton, playButton;
    private LottieAnimationView lavPlaying;
    private AppCompatButton convertButton;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);

        // Initialize view
        initializeView();

        // Get current api service
        service = ApiClient.getApiService();

        playButton.setEnabled(false);

        // Record button click listener
        recordButton.setOnClickListener(v -> {
            if (checkPermission()) {
                startRecording();
            } else {
                requestPermission();
            }
        });

        // Play button click listener
        playButton.setOnClickListener(v -> stopRecording());

        // Convert button click listener
        convertButton.setOnClickListener(v -> {
            // Add your speech to text conversion logic here
        });

    }

    private void startRecording() {
        audioFilePath = getExternalFilesDir(null).getAbsolutePath() + "/recorded_audio.3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            // Show animation when recording starts
            lavPlaying.setVisibility(View.VISIBLE);

            recordButton.setEnabled(false);
            playButton.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            // Hide animation when recording stops
            lavPlaying.setVisibility(View.GONE);

            recordButton.setEnabled(true);
            playButton.setEnabled(false);
        }
    }

    public void uploadSpeechText(String text) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), text);

        Log.d("uploadSpeechText", "Attempting to upload speech text: " + text);
        Call<ResponseBody> call = service.uploadText(requestBody.toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("uploadSpeechText", "Speech text upload successful: " + response.body());
                } else {
                    Log.e("uploadSpeechText", "Speech text upload failed with status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("uploadSpeechText", "Speech text upload failed: " + t.getMessage(), t);
            }
        });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecording();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeView() {
        recordButton = findViewById(R.id.recordButton);
        playButton = findViewById(R.id.playButton);
        convertButton = findViewById(R.id.img2txtConvertButton);
        lavPlaying = findViewById(R.id.lavPlaying);
    }
}