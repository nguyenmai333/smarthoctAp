package com.uitcontest.studymanagement;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.api.ApiService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1, SPEECH_REQUEST_CODE = 0;
    private AppCompatButton uploadImageButton, summarizeButton;
    private ImageView speechToTextImageView;
    ApiService service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize view
        initializeView();

        // Connect to server
        connectServer();

        // Handle upload image to application
        handleUploadImage();

        // Handle summarize inputted text
        handleSummarize();

        // Handle upload speech to application
        handleSpeech();

    }


    private void connectServer() {
        service = ApiClient.getClient().create(ApiService.class);
        Log.d("Server Status", "Success!");
    }

    private void handleSpeech() {
        // Handle click event
        speechToTextImageView.setOnClickListener(view -> startSpeechToText());
    }

    private void handleSummarize() {
        // Handle click event
        summarizeButton.setOnClickListener(v -> {
            openDialog();
        });
    }

    private void handleUploadImage() {
        // Handle click event
        uploadImageButton.setOnClickListener(view -> {
            checkPermission(); // Request permission and open the gallery if granted
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(new File(Objects.requireNonNull(getPathFromURI(imageUri))));
        } else if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);
            Log.d("SPOKENTEXT", spokenText);
            uploadSpeechText(spokenText);
        }
    }

    private void openDialog() {
        // Create an EditText to use in the dialog
        EditText input = new EditText(MainActivity.this);
        input.setHint("Enter text to summarize");

        // Build the AlertDialog
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Summarize Text")
                .setMessage("Please enter the text you want to summarize:")
                .setView(input)
                .setPositiveButton("Summarize", (dialog, whichButton) -> {
                    // Get the input text
                    String textToSummarize = input.getText().toString();

                    // Handle the summarization process here
                    Log.d("Summarize", "Text to summarize: " + textToSummarize);
                    uploadText(textToSummarize);

                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> {
                    // Handle cancel button
                    dialog.dismiss();
                })
                .show();
    }

    private void uploadText(String text) {
        // Create the request body
        Call<ResponseBody> call = service.uploadText(text);

        // Make the network request
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Get the response body and parse it as needed
                        String responseBody = response.body().string();
                        Log.d("Summarize", "Summary response: " + responseBody);

                        // Example: Show the summarized text in a dialog
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Summary")
                                .setMessage(responseBody)
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Summarize", "Summary request failed with status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Summarize", "Summary request failed: " + t.getMessage(), t);
            }
        });
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
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

    public void uploadImage(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Log.d("uploadImage", "Attempting to upload image: " + file.getName());
        Call<ResponseBody> call = service.uploadImage(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("uploadImage", "Image upload successful: " + response.body());
                } else {
                    Log.e("uploadImage", "Image upload failed with status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("uploadImage", "Image upload failed: " + t.getMessage(), t);
            }
        });
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void initializeView() {
        uploadImageButton = findViewById(R.id.uploadImageButton);
        speechToTextImageView = findViewById(R.id.speechToTextImageView);
        summarizeButton = findViewById(R.id.summarizeButton);
    }

    // Request permission if not already granted
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
        } else {
            openGallery();
        }
    }

}