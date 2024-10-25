package com.uitcontest.studymanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.chibde.visualizer.BarVisualizer;
import com.google.android.material.textfield.TextInputEditText;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.SharedPrefManager;
import com.uitcontest.studymanagement.api.ApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeechToTextActivity extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST = 100, REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String OUTPUT_FILE_PATH;
    private ImageView recordButton, playButton, stopButton, ivBack;
    private BarVisualizer barVisualizer;
    private TextInputEditText etTitle;
    private AppCompatButton convertButton;
    private AudioRecord audioRecord;
    private MediaPlayer mediaPlayer;
    private Thread recordingThread;
    private FrameLayout progressOverlay;
    private String title;
    private boolean isRecording = false, alreadyRecorded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);

        // Initialize view
        initializeView();

        // Create a directory for storing recordings (Internal Storage)
        File recordingDir = createInternalDirectory();
        Log.d("Directory", "Recording directory path: " + recordingDir.getPath());

        // Disable stop and play button by default
        switchStateButton(stopButton);
        switchStateButton(playButton);

        // Record button click listener
        recordButton.setOnClickListener(v -> requestAudioPermissions());

        // Stop button click listener
        stopButton.setOnClickListener(v -> stopRecording());

        // PLay button click listener
        playButton.setOnClickListener(v -> playAudio(OUTPUT_FILE_PATH));

        // Convert button click listener
        convertButton.setEnabled(true);
        convertButton.setOnClickListener(v -> convertAudio());

        // Check if the user has already uploaded or recorded an audio file
        if (OUTPUT_FILE_PATH != null) {
            alreadyRecorded = true;
            switchStateButton(recordButton);
            if (!stopButton.isEnabled()) switchStateButton(stopButton);
            if (!playButton.isEnabled()) switchStateButton(playButton);
        }

        // Back button click listener
        ivBack.setOnClickListener(v -> finish());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the audio file path
            String audioFilePath = Objects.requireNonNull(data.getData()).getPath();
            Log.d("Audio File", "File path: " + audioFilePath);

            // Get the audio file name
            assert audioFilePath != null;
            String audioFileName = audioFilePath.substring(audioFilePath.lastIndexOf("/") + 1);
            Log.d("Audio File", "File name: " + audioFileName);

            // Update the output file path
            OUTPUT_FILE_PATH = audioFilePath;
        }
    }

    private void convertAudio() {
        // Check if null or empty
        if (OUTPUT_FILE_PATH == null || OUTPUT_FILE_PATH.isEmpty()) {
            Toast.makeText(this, "Please record or upload an audio file", Toast.LENGTH_SHORT).show();
            return;
        }
        title = Objects.requireNonNull(etTitle.getText()).toString();
        isTitleValid(title);
        Toast.makeText(this, "Converting Speech to Text...", Toast.LENGTH_SHORT).show();

        // Create a request body from the audio file (wav)
        File audioFile = new File(OUTPUT_FILE_PATH);
        Log.d("OUTPUTFILEPATH convertAudio", OUTPUT_FILE_PATH);
        Log.d("Audio File", "File name: " + audioFile.getName());
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/wav"), audioFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", audioFile.getName(), requestFile);

        uploadSpeechToServer(body);
    }

    private void uploadSpeechToServer(MultipartBody.Part body) {
        // Get token
        String token = SharedPrefManager.getInstance(SpeechToTextActivity.this).getAuthToken();
        Log.d("Speech to Text", "Token: " + token);

        // Show progress overlay
        progressOverlay.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = ApiClient.getApiService().transcribeAudio("Bearer " + token, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Speech to Text", "Speech upload successful: " + response.body());
                    // Get the response content
                    String convertedText;

                    try {
                        convertedText = response.body().string();
                        // Remove leading "text" and trailing double quotes
                        convertedText = convertedText.substring(9, convertedText.length() - 2);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // Hide progress overlay
                    progressOverlay.setVisibility(View.GONE);

                    Log.d("Speech to Text", "Text: " + convertedText);
                    // Pass the text to the next activity
                    Intent intent = new Intent(SpeechToTextActivity.this, ConvertedTextActivity.class);
                    intent.putExtra("convertedText", convertedText);
                    startActivity(intent);
                } else {
                    // Hide progress overlay
                    progressOverlay.setVisibility(View.GONE);
                    Toast.makeText(SpeechToTextActivity.this, "Error converting audio to text", Toast.LENGTH_SHORT).show();
                    Log.e("Speech to Text", "Error converting audio to text: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Hide progress overlay
                progressOverlay.setVisibility(View.GONE);
                Toast.makeText(SpeechToTextActivity.this, "Error converting audio to text", Toast.LENGTH_SHORT).show();
                Log.e("Speech to Text", "Error converting audio to text: " + t.getMessage());
            }
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
        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();
        isRecording = true;
        int sampleRate = 44100;
        int audioBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        // Check for permission to record audio
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Create a directory for storing the audio file
        File audioFile = getFile();

        try {
            FileOutputStream outputStream = new FileOutputStream(audioFile);

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, audioBufferSize);

            audioRecord.startRecording();

            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[audioBufferSize];
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, buffer.length);

                    if (read > 0) {
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
            if (!stopButton.isEnabled()) switchStateButton(stopButton);    // Enable stop button if it's disabled

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Recording", "Error while recording audio");
        }

    }

    @NonNull
    private File getFile() {
        File recordingDir = createInternalDirectory();
        File audioFile = new File(recordingDir, "audio_record_" + System.currentTimeMillis() + ".pcm");
        OUTPUT_FILE_PATH = audioFile.getAbsolutePath();
        Log.d("OUTPUTFILEPATH startRecording", OUTPUT_FILE_PATH);
        return audioFile;
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

            title = Objects.requireNonNull(etTitle.getText()).toString();
            // Check if the title is valid
            if (!isTitleValid(title)) {
                return;
            }

            Toast.makeText(this, "Recording saved successfully", Toast.LENGTH_SHORT).show();

            // Convert PCM to WAV and save the WAV file
            saveRecord();
        }
    }

    private void saveRecord() {
        File pcmFile = new File(OUTPUT_FILE_PATH);
        if (pcmFile.exists()) {
            // If duplicated title, add timestamp to the title
            if (new File(pcmFile.getParent() + File.separator + title + ".pcm").exists()) {
                title += "_" + System.currentTimeMillis();
            }
            // Define WAV file path
            String wavFilePath = pcmFile.getParent() + File.separator + title + ".wav";
            convertPcmToWav(OUTPUT_FILE_PATH, wavFilePath, 44100, 1, 16);

            // Update OUTPUT_FILE_PATH
            OUTPUT_FILE_PATH = wavFilePath;

            alreadyRecorded = true;

            Log.d("Recording", "WAV file saved at: " + wavFilePath);
        }
    }

    private void playAudio(String audioFilePath) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                // Change play button
                playButton.setImageResource(R.drawable.pause_circle);

                mediaPlayer.setDataSource(audioFilePath);
                Log.d("OUTPUTFILEPATH playAudio", OUTPUT_FILE_PATH);
                mediaPlayer.prepare();
                mediaPlayer.start();

                // Set audio visualizer
                barVisualizer.setPlayer(mediaPlayer.getAudioSessionId());

                mediaPlayer.setOnCompletionListener(mp -> {
                    // Change play button when completed
                    playButton.setImageResource(R.drawable.play_circle);

                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                });

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Audio Player", "Error playing audio: " + e.getMessage());
                Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
            }
        } else {
            toggleMediaPlayer();
        }
    }

    private void toggleMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            playButton.setImageResource(R.drawable.play_circle);
            mediaPlayer.pause();
        } else {
            playButton.setImageResource(R.drawable.pause_circle);
            mediaPlayer.start();
        }
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

    private boolean isTitleValid(String title) {
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initializeView() {
        etTitle = findViewById(R.id.etAudioFileTitle);
        playButton = findViewById(R.id.playButton);
        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        convertButton = findViewById(R.id.img2txtConvertButton);
        barVisualizer = findViewById(R.id.audioVisualizerView);
        progressOverlay = findViewById(R.id.progressOverlay);
        ivBack = findViewById(R.id.ivBack);

        // Set visualizer color
        barVisualizer.setColor(ContextCompat.getColor(this, R.color.purple_200));
        barVisualizer.setDensity(70);
    }
}