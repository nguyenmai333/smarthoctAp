package com.uitcontest.studymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uitcontest.studymanagement.api.ApiClient;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageToTextActivity extends AppCompatActivity {

    private ImageView selectedImage;
    private AppCompatButton convertButton;
    private Bitmap imageBitmap = null;
    private String imageUriString = null;

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
    
    }

    private void convertImageToText() {
        // Handle click event
        convertButton.setOnClickListener(v -> {
            uploadImage(imageBitmap);
        });
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
            Picasso.get().load(imageBitmap.toString()).placeholder(R.drawable.imagemode).into(selectedImage);
        }
    }

    public void uploadImage(Bitmap bitmap) {
        // Convert Bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

        Log.d("uploadImage", "Attempting to upload image from Bitmap");

        Call<ResponseBody> call = ApiClient.getApiService().uploadImage(body);
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

    private void initializeView() {
        selectedImage = findViewById(R.id.selectedImage);
        convertButton = findViewById(R.id.img2txtConvertButton);
    }
}