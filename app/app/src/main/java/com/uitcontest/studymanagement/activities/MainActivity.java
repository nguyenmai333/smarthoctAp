package com.uitcontest.studymanagement.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.SharedPrefManager;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.api.ApiService;
import com.uitcontest.studymanagement.models.UserModel;

import java.util.Arrays;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1, CAMERA_REQUEST = 2;
    private ImageView profileImageView, folderImageView, cameraImageView, microphoneImageView, menuImageView, searchImageView;
    private EditText searchText;
    private UserModel userModel = UserModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize view
        initializeView();

        // Connect to server
        connectServer();

        // Get user info
        getUserInfo();

        // Handle profile info
        handleProfile();

        // Handle smart search
        handleSmartSearch();

        // Handle upload image to application
        handleUploadImage();

        // Handle capture image
        handleCameraCapture();

        // Handle upload speech to application
        handleSpeech();

    }

    private void getUserInfo() {
        // Get user info from server
        String token = SharedPrefManager.getInstance(this).getAuthToken();
        Call<ResponseBody> call = ApiClient.getApiService().getUserInfo("Bearer " + token);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        String responseString = response.body().string();

                        // Parse Json
                        userModel = UserModel.fromJson(responseString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void handleProfile() {
        // Handle click event
        profileImageView.setOnClickListener(v -> {
            // Create a PopupMenu when profile image is clicked
            PopupMenu popupMenu = new PopupMenu(this, profileImageView);
            popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

            // Handle menu item clicks
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_profile) {
                    // Handle Profile click
                    openProfile();
                    return true;
                } else if (itemId == R.id.action_signout) {
                    // Handle Signout click
                    performSignout();
                    return true;
                }
                return false;
            });

            // Show the popup menu
            popupMenu.show();
        });
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileInfoActivity.class);
        startActivity(intent);
    }

    private void performSignout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleSmartSearch() {
        // Handle click event
        menuImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
            startActivity(intent);
        });

        searchImageView.setOnClickListener(v -> {
            String query = searchText.getText().toString();
            if (!query.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, SmartSearchActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
            }
        });
    }

    private void connectServer() {
        ApiService service = ApiClient.getApiService();
        Log.d("Server Status", "Connected!");
    }

    private void handleCameraCapture() {
        // Handle click event
        cameraImageView.setOnClickListener(v -> {
            checkCameraPermission();
        });
    }

    private void handleSpeech() {
        // Handle click event
        microphoneImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SpeechToTextActivity.class);
            startActivity(intent);
        });
    }

    private void handleUploadImage() {
        // Handle click event
        folderImageView.setOnClickListener(view -> {
            checkAlbumPermission();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Check if the file size is within 50 MB limit
                try {
                    long fileSizeInBytes = Objects.requireNonNull(getContentResolver().openAssetFileDescriptor(imageUri, "r")).getLength();
                    long fileSizeInMB = fileSizeInBytes / (1024 * 1024);

                    if (fileSizeInMB > 50) {
                        // Show an error message if file size exceeds 50 MB
                        Log.e("ERROR", "Selected image exceeds 50 MB limit.");
                        Toast.makeText(this, "Selected image exceeds 50 MB. Please select a smaller image.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d("FILE SIZE", "File size: " + fileSizeInMB + " MB");
                    Log.d("SELECTED IMAGE URI", imageUri.toString());
                    Intent intent = new Intent(MainActivity.this, ImageToTextActivity.class);
                    intent.putExtra("imageUri", imageUri.toString());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            if (imageBitmap != null) {
                Log.d("IMAGE BITMAP", String.valueOf(imageBitmap));
                Intent intent = new Intent(MainActivity.this, ImageToTextActivity.class);
                intent.putExtra("imageBitmap", imageBitmap);
                startActivity(intent);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void initializeView() {
        profileImageView = findViewById(R.id.profileIcon);
        menuImageView = findViewById(R.id.ivMenu);
        searchImageView = findViewById(R.id.ivSearch);
        searchText = findViewById(R.id.etSearch);
        folderImageView = findViewById(R.id.navFolder);
        cameraImageView = findViewById(R.id.navCamera);
        microphoneImageView = findViewById(R.id.navMic);
    }

    private void checkPermissionAndOpenFeature(String permission, int requestCode, Runnable feature) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            feature.run();
        }
    }

    private void checkAlbumPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissionAndOpenFeature(android.Manifest.permission.READ_MEDIA_IMAGES, PICK_IMAGE_REQUEST, this::openGallery);
        } else {
            checkPermissionAndOpenFeature(android.Manifest.permission.READ_EXTERNAL_STORAGE, PICK_IMAGE_REQUEST, this::openGallery);
        }
    }

    private void checkCameraPermission() {
        checkPermissionAndOpenFeature(android.Manifest.permission.CAMERA, CAMERA_REQUEST, this::openCamera);
    }

}