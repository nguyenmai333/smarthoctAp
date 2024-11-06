package com.uitcontest.studymanagement.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.models.UserModel;

public class ProfileInfoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView, ivBack;
    private TextInputEditText usernameEditText, emailEditText, nameEditText;
    private Spinner genderSpinner;
    private AppCompatButton editSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        // Initialize view
        initializeView();

        // Display user info
        displayInfo();

        // Default to View Mode
        viewMode();

        // Handle edit/save button
        handleEditSaveButton();

        // Handle profile image click
        handleProfileImageClick();

        // Handle back button
        ivBack.setOnClickListener(v -> finish());
    }

    private void handleProfileImageClick() {
        // Allow SharedPreferences to only store user image
        profileImageView.setOnClickListener(v -> {
            if (editSaveButton.getText().toString().equals("Save")) {
                checkAlbumPermission();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void viewMode() {
        // Change button text to "Edit"
        editSaveButton.setText("Edit");

        // Disable all EditText, Spinner and ImageView
        profileImageView.setEnabled(false);
        usernameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        nameEditText.setEnabled(false);
        genderSpinner.setEnabled(false);

        // Hide editable end drawable
        usernameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        nameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        // Gray out the text
        usernameEditText.setTextColor(ContextCompat.getColor(ProfileInfoActivity.this,R.color.gray));
        emailEditText.setTextColor(ContextCompat.getColor(ProfileInfoActivity.this,R.color.gray));
        nameEditText.setTextColor(ContextCompat.getColor(ProfileInfoActivity.this,R.color.gray));

        // Save user info
        saveUserInfo();
    }

    @SuppressLint({"SetTextI18n", "UseCompatTextViewDrawableApis"})
    private void editMode() {
        // Change button text to "Save"
        editSaveButton.setText("Save");

        // Enable all EditText, Spinner and ImageView
        profileImageView.setEnabled(true);
        usernameEditText.setEnabled(true);
        emailEditText.setEnabled(true);
        nameEditText.setEnabled(true);
        genderSpinner.setEnabled(true);

        // Show a editable end drawable
        usernameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit, 0);
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit, 0);
        nameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit, 0);

        // Set drawable tint
        usernameEditText.setCompoundDrawableTintList(ContextCompat.getColorStateList(ProfileInfoActivity.this, R.color.black));
        emailEditText.setCompoundDrawableTintList(ContextCompat.getColorStateList(ProfileInfoActivity.this, R.color.black));
        nameEditText.setCompoundDrawableTintList(ContextCompat.getColorStateList(ProfileInfoActivity.this, R.color.black));

        // Reset text color
        usernameEditText.setTextColor(ContextCompat.getColor(ProfileInfoActivity.this, R.color.black));
        emailEditText.setTextColor(ContextCompat.getColor(ProfileInfoActivity.this,R.color.black));
        nameEditText.setTextColor(ContextCompat.getColor(ProfileInfoActivity.this,R.color.black));

    }

    // Open Gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void checkAlbumPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissionAndOpenFeature(android.Manifest.permission.READ_MEDIA_IMAGES, this::openGallery);
        } else {
            checkPermissionAndOpenFeature(android.Manifest.permission.READ_EXTERNAL_STORAGE, this::openGallery);
        }
    }

    private void checkPermissionAndOpenFeature(String permission, Runnable feature) {
        if (ContextCompat.checkSelfPermission(ProfileInfoActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileInfoActivity.this, new String[]{permission}, ProfileInfoActivity.PICK_IMAGE_REQUEST);
        } else {
            feature.run();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Get image from gallery
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                profileImageView.setImageURI(imageUri);
                Log.d("SELECTED IMAGE URI", imageUri.toString());
            }
        }
    }

    private void handleEditSaveButton() {
        // If user is in View Mode (Button show EDIT)
        editSaveButton.setOnClickListener(v -> {
            if (editSaveButton.getText().toString().equals("Save")) {
                viewMode();
            }
            // If user is in Edit Mode (Button show SAVE)
            else {
                editMode();
            }
        });
    }

    private void saveUserInfo() {
    }

    private void displayInfo() {
        // Get user info from instance
        UserModel userModel = UserModel.getInstance();
        String name = userModel.getFullName();
        String username = userModel.getUsername();
        String email = userModel.getEmail();
        String gender = userModel.getSex();

        // Display user info
        profileImageView.setImageResource(R.drawable.profile); // Set default image
        nameEditText.setText(name);
        usernameEditText.setText(username);
        emailEditText.setText(email);

        // Set spinner value based on user
        if (gender.contains("Male")) {
            genderSpinner.setSelection(0);
        } else {
            genderSpinner.setSelection(1);
        }
    }

    private void initializeView() {
        profileImageView = findViewById(R.id.ivProfileAvatar);
        usernameEditText = findViewById(R.id.etProfileUsernameText);
        emailEditText = findViewById(R.id.etProfileEmailText);
        nameEditText = findViewById(R.id.etProfileNameText);
        genderSpinner = findViewById(R.id.spinnerProfileGender);
        editSaveButton = findViewById(R.id.btnProfileEditSave);
        ivBack = findViewById(R.id.ivBack);
    }

}