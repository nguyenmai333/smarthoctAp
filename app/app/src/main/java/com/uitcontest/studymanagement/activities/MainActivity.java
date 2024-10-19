package com.uitcontest.studymanagement.activities;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.api.ApiService;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1, CAMERA_REQUEST = 2;
    private ImageView profileImageView, folderImageView, cameraImageView, microphoneImageView, menuImageView, searchImageView;
    private EditText searchText;
    private ApiService service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize view
        initializeView();

        // Connect to server
        connectServer();

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

    private void handleProfile() {
        // Dynamically change profile icon if needed

        // Handle click event
        profileImageView.setOnClickListener(v -> {
            // Create a PopupMenu when profile image is clicked
            PopupMenu popupMenu = new PopupMenu(this, profileImageView);
            popupMenu.getMenuInflater().inflate(R.drawable.profile_menu, popupMenu.getMenu());

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
        service = ApiClient.getApiService();
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
                Log.d("SELECTED IMAGE URI", imageUri.toString());
                Intent intent = new Intent(MainActivity.this, ImageToTextActivity.class);
                intent.putExtra("imageUri", imageUri.toString());
                startActivity(intent);
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            if (imageBitmap != null) {
                Log.d("IMAGE BITMAP", imageBitmap.toString());
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