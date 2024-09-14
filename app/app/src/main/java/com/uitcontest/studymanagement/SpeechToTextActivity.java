package com.uitcontest.studymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.api.ApiService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeechToTextActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String OUTPUT_FILE_PATH;
    private final float visualizerScalingFactor = 10.0f;
    private ImageView recordButton, playButton, stopButton;
    private AudioVisualizerView audioVisualizerView;
    private TextInputEditText etTitle;
    private AppCompatButton convertButton;
    private AudioRecord audioRecord;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private Thread recordingThread;
    private boolean isRecording = false;
    ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);

        // Initialize view
        initializeView();

        // Create a directory for storing recordings
        File recordingDir = createInternalDirectory();
        Log.d("Directory", "Recording directory path: " + recordingDir.getPath());

        // Get current api service
        service = ApiClient.getApiService();

        switchStateButton(stopButton);
        switchStateButton(playButton);

        // Record button click listener
        recordButton.setOnClickListener(v -> requestAudioPermissions());

        // Stop button click listener
        stopButton.setOnClickListener(v -> stopRecording());

        // PLay button click listener
        playButton.setOnClickListener(v -> playAudio());

        // Convert button click listener
        convertButton.setOnClickListener(v -> {
            String title = Objects.requireNonNull(etTitle.getText()).toString();
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Converting Speech to Text...", Toast.LENGTH_SHORT).show();
        });

    }

    private File createInternalDirectory() {
        File internalStorageDir = new File(getFilesDir(), "Recordings");

        if (!internalStorageDir.exists()) {
            boolean isCreated = internalStorageDir.mkdir();
            if (isCreated) {
                Log.d("Directory", "Directory created: " + internalStorageDir.getPath());
            } else {
                Log.e("Directory", "Failed to create directory: " + internalStorageDir.getPath());
            }
        }

        return internalStorageDir;
    }

    private void switchStateButton(ImageView button) {
        if (button.isEnabled()) {
            // Disable the button and set the color to gray
            button.setEnabled(false);
            button.setColorFilter(ContextCompat.getColor(SpeechToTextActivity.this, R.color.dark_gray), PorterDuff.Mode.MULTIPLY);
        } else {
            // Enable the button and reset the color
            button.setEnabled(true);
            button.clearColorFilter();
        }
    }

    private void startRecording() {
        isRecording = true;
        int sampleRate = 44100;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        // Check for permission to record audio
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Create a directory for storing the audio file
        File recordingDir = createInternalDirectory();

        // Define the file to store the recorded audio
        File audioFile = new File(recordingDir, "audio_record_" + System.currentTimeMillis() + ".pcm");
        OUTPUT_FILE_PATH = audioFile.getAbsolutePath();

        try {
            FileOutputStream outputStream = new FileOutputStream(audioFile);

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            audioRecord.startRecording();

            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[bufferSize];
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, buffer.length);

                    if (read > 0) {
                        // Calculate the amplitude from audio data for visualizer
                        float amplitude = calculateAmplitude(buffer, read);
                        runOnUiThread(() -> audioVisualizerView.updateVisualizer(amplitude * visualizerScalingFactor));

                        // Write audio data to file
                        try {
                            outputStream.write(buffer, 0, read);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Close outputStream when recording ends
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            recordingThread.start();
            switchStateButton(recordButton);  // Disable record button
            switchStateButton(stopButton);    // Enable stop button

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Recording", "File not found for recording audio");
        }
    }

    private void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;

            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;

            // Switch button states back
            switchStateButton(stopButton);      // Disable stop button
            switchStateButton(playButton);      // Enable play button

            // Inform the user that the recording has been saved
            Toast.makeText(this, "Recording saved successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(OUTPUT_FILE_PATH);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    private float calculateAmplitude(byte[] buffer, int read) {
        // Calculate amplitude (RMS)
        long sum = 0;
        for (int i = 0; i < read; i++) {
            sum += buffer[i] * buffer[i];
        }

        return (float) Math.sqrt(sum / read);
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

    // Request Audio Recording Permissions
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            // Permission already granted, start recording
            startRecording();
        }
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
        etTitle = findViewById(R.id.etAudioFileTitle);
        audioVisualizerView = findViewById(R.id.audioVisualizerView);
        playButton = findViewById(R.id.playButton);
        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        convertButton = findViewById(R.id.img2txtConvertButton);
    }
}