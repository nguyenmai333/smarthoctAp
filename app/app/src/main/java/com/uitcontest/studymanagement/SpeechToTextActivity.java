package com.uitcontest.studymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.api.ApiService;

import java.io.File;
import java.io.FileInputStream;
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

    private static ColorStateList PLAY_BUTTON_COLOR_STATE;
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

        // Create a directory for storing recordings (Internal Storage)
        File recordingDir = createInternalDirectory();
        Log.d("Directory", "Recording directory path: " + recordingDir.getPath());

        // Get current api service
        service = ApiClient.getApiService();

        // Disable stop and play button by default
        switchStateButton(stopButton);
        switchStateButton(playButton);

        // Get the current color state of play button
        PLAY_BUTTON_COLOR_STATE = playButton.getImageTintList();

        // Record button click listener
        recordButton.setOnClickListener(v -> requestAudioPermissions());

        // Stop button click listener
        stopButton.setOnClickListener(v -> stopRecording());

        // PLay button click listener
        playButton.setOnClickListener(v -> playAudio(OUTPUT_FILE_PATH));

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
        File internalStorageDir = new File(getApplicationContext().getExternalFilesDir(null), "Recordings");

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

    private File createExternalDirectory() {
        File externalStorageDir = getExternalFilesDir(null);

        if (externalStorageDir != null) {
            // Create the "Recordings" directory in the external storage path
            File recordingsDir = new File(externalStorageDir, "Recordings");

            if (!recordingsDir.exists()) {
                boolean isCreated = recordingsDir.mkdir();
                if (isCreated) {
                    Log.d("Directory", "Directory created: " + recordingsDir.getPath());
                } else {
                    Log.e("Directory", "Failed to create directory: " + recordingsDir.getPath());
                }
            }

            return recordingsDir;
        } else {
            // Handle case if external storage is not available
            Log.e("Directory", "External storage not available");
            return null;
        }
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

    private void convertPcmToWav(String pcmFilePath, String wavFilePath, int sampleRate, int channels, int bitsPerSample) {
        FileInputStream pcmInputStream = null;
        FileOutputStream wavOutputStream = null;

        try {
            File pcmFile = new File(pcmFilePath);
            File wavFile = new File(wavFilePath);

            pcmInputStream = new FileInputStream(pcmFile);
            wavOutputStream = new FileOutputStream(wavFile);

            // Get the size of PCM
            long pcmFileSize = pcmFile.length();

            // Write WAV header
            writeWavHeader(wavOutputStream, pcmFileSize, sampleRate, channels, bitsPerSample);

            // Copy PCM to WAV
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = pcmInputStream.read(buffer)) != -1) {
                wavOutputStream.write(buffer, 0, bytesRead);
            }

            Log.d("PCM to WAV", "Conversion Successful!");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PCM to WAV", "Conversion Failed: " + e.getMessage());
        } finally {
            try {
                if (pcmInputStream != null) pcmInputStream.close();
                if (wavOutputStream != null) wavOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeWavHeader(FileOutputStream out, long pcmFileSize, int sampleRate, int channels, int bitsPerSample) throws IOException {
        long totalDataLen = pcmFileSize + 36;
        long byteRate = (long) sampleRate * channels * bitsPerSample / 8;

        byte[] header = new byte[44];

        // RIFF header
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        // Total file size (except the first 8 bytes)
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);

        // WAVE header
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        // Fmt subchunk
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        // Subchunk1 size (16 for PCM)
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;

        // Audio format (1 for PCM)
        header[20] = 1;
        header[21] = 0;

        // Number of channels
        header[22] = (byte) channels;
        header[23] = 0;

        // Sample rate
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);

        // Byte rate
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);

        // Block align
        header[32] = (byte) (channels * bitsPerSample / 8);
        header[33] = 0;

        // Bits per sample
        header[34] = (byte) bitsPerSample;
        header[35] = 0;

        // Data subchunk
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        // Data size
        header[40] = (byte) (pcmFileSize & 0xff);
        header[41] = (byte) ((pcmFileSize >> 8) & 0xff);
        header[42] = (byte) ((pcmFileSize >> 16) & 0xff);
        header[43] = (byte) ((pcmFileSize >> 24) & 0xff);

        // Write the header to the output stream
        out.write(header, 0, 44);
    }

    private void startRecording() {
        isRecording = true;
        int sampleRate = 44100;
        int audioBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        // Check for permission to record audio
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Create a directory for storing the audio file
        File recordingDir = createInternalDirectory();
        File audioFile = new File(recordingDir, "audio_record_" + System.currentTimeMillis() + ".pcm");
        OUTPUT_FILE_PATH = audioFile.getAbsolutePath();

        try (FileOutputStream outputStream = new FileOutputStream(audioFile)) {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, audioBufferSize);

            audioRecord.startRecording();

            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[audioBufferSize];
                while (isRecording) {
                    int bytesRead = audioRecord.read(buffer, 0, buffer.length);

                    if (bytesRead > 0) {
                        // Update visualizer and write audio data to file
                        float amplitude = calculateAmplitude(buffer, bytesRead);
                        runOnUiThread(() -> audioVisualizerView.updateVisualizer(amplitude * visualizerScalingFactor));

                        // Write audio data to file
                        try {
                            outputStream.write(buffer, 0, bytesRead);
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
            if (playButton.isEnabled()) switchStateButton(playButton);  // Disable play button only if it's enabled
            switchStateButton(stopButton);    // Enable stop button

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Recording", "File creation failed", e);
        }
    }

    private void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;

            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;

            switchStateButton(stopButton);      // Disable stop button
            if (!playButton.isEnabled()) switchStateButton(playButton);  // Enable play button only if it's disabled
            switchStateButton(recordButton);    // Re-enable record button

            // Check if the title is valid
            if (!isTitleValid()) {
                return;
            }

            Toast.makeText(this, "Recording saved successfully", Toast.LENGTH_SHORT).show();

            File pcmFile = new File(OUTPUT_FILE_PATH);
            if (pcmFile.exists()) {
                // Define WAV file path
                String wavFilePath = pcmFile.getParent() + "/" + etTitle.getText() + ".wav";
                convertPcmToWav(OUTPUT_FILE_PATH, wavFilePath, 44100, 1, 16);

                // Update OUTPUT_FILE_PATH
                OUTPUT_FILE_PATH = wavFilePath;

                Toast.makeText(this, "WAV file saved successfully at " + wavFilePath, Toast.LENGTH_LONG).show();
                Log.d("Recording", "WAV file saved at: " + wavFilePath);
            }
        }
    }

    private void playAudio(String audioFilePath) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                // Change play button
                playButton.setImageResource(R.drawable.pause_circle);

                mediaPlayer.setDataSource(audioFilePath);
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(mp -> {
                    // Change play button when completed
                    playButton.setImageResource(R.drawable.play_circle);

                    mediaPlayer.release();
                    mediaPlayer = null;
                });

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mediaPlayer.isPlaying()) {
                // Change play button when player is playing
                playButton.setImageResource(R.drawable.play_circle);
                mediaPlayer.pause();
            }
            else {
                // Change play button when player is paused
                playButton.setImageResource(R.drawable.pause_circle);
                mediaPlayer.start();
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

    private boolean isTitleValid() {
        String title = Objects.requireNonNull(etTitle.getText()).toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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