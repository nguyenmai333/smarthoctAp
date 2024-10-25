package com.uitcontest.studymanagement.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.squareup.picasso.Picasso;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.SharedPrefManager;
import com.uitcontest.studymanagement.api.ApiClient;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageToTextActivity extends AppCompatActivity {

    private ImageView selectedImage, ivBack;
    private AppCompatButton convertButton;
    private Bitmap imageBitmap = null;
    private String imageUriString = null;
    private FrameLayout progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        // Initialize view
        initializeView();
        
        // Handle get image from previous context
        getSelectedImage();
    
        // Handle convert image to text
        convertImageToText();

        // Handle back button
        ivBack.setOnClickListener(v -> finish());
    
    }

    private void convertImageToText() {
        // Handle click event
        convertButton.setOnClickListener(v -> {
            if (imageBitmap != null) {
                uploadImage(imageBitmap);
            } else if (imageUriString != null) {
                uploadImage(Uri.parse(imageUriString));
            } else {
                Log.e("convertImageToText", "Image is null");
            }
        });
    }

    public void uploadImage(Bitmap bitmap) {
        if (bitmap != null) {
            // Convert Bitmap to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

            uploadImageToServer(body);
        } else {
            Log.e("uploadImage", "Bitmap is null");
        }
    }

    public void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            try {
                // Convert Uri to InputStream and then to byte array
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                assert inputStream != null;
                byte[] imageBytes = getBytes(inputStream);

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

                uploadImageToServer(body);
            } catch (FileNotFoundException e) {
                Log.e("uploadImage", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.e("uploadImage", "IO Exception: " + e.getMessage());
            }
        } else {
            Log.e("uploadImage", "ImageUri is null");
        }
    }

    private void uploadImageToServer(MultipartBody.Part body) {
        progressOverlay.setVisibility(ProgressBar.VISIBLE);

        Log.d("uploadImage", "Attempting to upload image");
        String token = SharedPrefManager.getInstance(this).getAuthToken();
        Log.d("uploadImage", "Token: " + token);
        Call<ResponseBody> call = ApiClient.getApiService().uploadImage("Bearer " + token, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    progressOverlay.setVisibility(View.GONE);
                    Log.d("uploadImage", "Image upload successful: " + response.body());
                    // Get the response content
                    String convertedText;

                    try {
                        assert response.body() != null;
                        convertedText = response.body().string();
                        // Remove leading "text" and trailing double quotes
                        convertedText = convertedText.substring(9, convertedText.length() - 2);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("uploadImage", "Converted text: " + convertedText);
                    Intent intent = new Intent(ImageToTextActivity.this, ConvertedTextActivity.class);
                    intent.putExtra("convertedText", convertedText);
                    startActivity(intent);
                } else {
                    Log.e("uploadImage", "Image upload failed with status: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressOverlay.setVisibility(View.GONE);
                Log.e("uploadImage", "Image upload failed: " + t.getMessage(), t);
            }
        });
    }

    // Utility method to convert InputStream to byte array
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int read;
        byte[] buffer = new byte[1024];

        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            byteBuffer.write(buffer, 0, read);
        }
        return byteBuffer.toByteArray();
    }

    private void getSelectedImage() {
        // Retrieve the image URI from Intent
        imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Log.d("IMAGE URI", imageUri.toString());
            Picasso.get().load(imageUri).placeholder(R.drawable.imagemode).into(selectedImage);
        }

        // Get the image bitmap from the intent extras
        imageBitmap = getIntent().getParcelableExtra("imageBitmap");
        if (imageBitmap != null) {
            Log.d("IMAGE BITMAP", imageBitmap.toString());
            selectedImage.setImageBitmap(imageBitmap);
        }
    }

    private void initializeView() {
        selectedImage = findViewById(R.id.selectedImage);
        convertButton = findViewById(R.id.img2txtConvertButton);
        progressOverlay = findViewById(R.id.progressOverlay);
        ivBack = findViewById(R.id.ivBack);
    }
}