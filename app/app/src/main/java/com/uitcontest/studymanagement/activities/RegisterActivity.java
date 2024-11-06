package com.uitcontest.studymanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.requests.RegisterUserRequest;
import com.uitcontest.studymanagement.api.ApiClient;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText, emailEditText, passwordEditText, fullnameEditText;
    private TextView loginNowTextView;
    private RadioGroup genderRadioGroup;
    private AppCompatButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize View
        initializeView();

        // Set Click Listeners
        setClickListeners();
    }

    private void setClickListeners() {
        // Handle click for register button
        registerButton.setOnClickListener(v -> handleRegister());

        // Handle click for "Login Now" text
        loginNowTextView.setOnClickListener(v -> handleLoginNow());
    }

    private boolean isAnyFieldEmpty(TextInputEditText... fields) {
        boolean isEmpty = false;
        for (TextInputEditText field : fields) {
            if (Objects.requireNonNull(field.getText()).toString().isEmpty()) {
                showError(field);
                isEmpty = true;
            }
        }
        return isEmpty;
    }

    private void showError(TextInputEditText field) {
        field.setError("This field is required");
        field.requestFocus();
    }

    private void showRadioGroupError(RadioGroup radioGroup) {
        TextView errorText = (TextView) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
        errorText.setError("Please select your gender");
        errorText.requestFocus();
    }

    private void handleLoginNow() {
        switchActivity(this);
    }

    private void handleRegister() {
        String username = Objects.requireNonNull(usernameEditText.getText()).toString();
        String email = Objects.requireNonNull(emailEditText.getText()).toString();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        String fullname = Objects.requireNonNull(Objects.requireNonNull(fullnameEditText.getText()).toString());
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();

        if (isAnyFieldEmpty(usernameEditText, emailEditText, passwordEditText, fullnameEditText)) {
            return;
        }

        if (selectedGenderId == -1) {
            showRadioGroupError(genderRadioGroup);
            return;
        }

        RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
        String gender = selectedGenderRadioButton.getText().toString();

        // Create the request model
        RegisterUserRequest request = new RegisterUserRequest(username, password, email, fullname, gender);

        // Call the register API
        Call<ResponseBody> call = ApiClient.getApiService().registerUser(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    // Navigate to the login screen or another screen
                    handleLoginNow();
                } else {

                    try {
                        assert response.errorBody() != null;
                        String errorBody = response.errorBody().string();
                        // Format json
                        errorBody = errorBody.substring(1, errorBody.length() - 1);
                        Log.e("RegisterActivity", "Error response: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                    Toast.makeText(RegisterActivity.this, "Registration Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void switchActivity(Context currentContext) {
        Intent intent = new Intent(currentContext, LoginActivity.class);
        currentContext.startActivity(intent);
        finish();
    }

    private void initializeView() {
        usernameEditText = findViewById(R.id.etRegisterUsernameText);
        emailEditText = findViewById(R.id.etRegisterEmailText);
        passwordEditText = findViewById(R.id.etRegisterPasswordText);
        genderRadioGroup = findViewById(R.id.rgGender);
        fullnameEditText = findViewById(R.id.etRegisterFullnameText);
        loginNowTextView = findViewById(R.id.tvLoginNow);
        registerButton = findViewById(R.id.registerButton);
    }
}