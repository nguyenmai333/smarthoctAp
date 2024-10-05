package com.uitcontest.studymanagement.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.SharedPrefManager;
import com.uitcontest.studymanagement.api.ApiClient;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextView forgotPasswordTextView, registerNowTextView;
    private AppCompatButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize View
        initializeView();

        // Set Click Listeners
        setClickListeners();
    }

    private void setClickListeners() {
        // Handle click for login button
        loginButton.setOnClickListener(v -> handleLogin());

        // Handle click for "Forgot Password" text
        forgotPasswordTextView.setOnClickListener(v -> handleForgotPassword());

        // Handle click for "Register Now" text
        registerNowTextView.setOnClickListener(v -> handleRegisterNow());
    }

    private void handleRegisterNow() {
        switchActivity(this);
    }

    private void handleForgotPassword() {
    }

    private void handleLogin() {
        String username = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();

        // Basic validation checks
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set OAuth2 parameters
        String grantType = "password";
        String scope = "";
        String clientId = "client_id";
        String clientSecret = "client_secret";

        // Call the login API
        Call<ResponseBody> call = ApiClient.getApiService().loginUser(grantType, username, password, scope, clientId, clientSecret);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        String responseBody = response.body().string();
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Log.d("Login", "Response: " + responseBody);
                        // Split the responseBody to get token
                        String[] parts = responseBody.split(",");
                        String token = parts[0].split(":")[1].replace("\"", "");
                        Log.d("Login", "Token: " + token);
                        // Store the token
                        SharedPrefManager.getInstance(LoginActivity.this).saveAuthToken(token);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void switchActivity(Context currentContext) {
        Intent intent = new Intent(currentContext, RegisterActivity.class);
        currentContext.startActivity(intent);
        finish();
    }

    private void initializeView() {
        emailEditText = findViewById(R.id.etEmailText);
        passwordEditText = findViewById(R.id.etPasswordText);
        forgotPasswordTextView = findViewById(R.id.tvForgotPassword);
        registerNowTextView = findViewById(R.id.tvRegisterNow);
        loginButton = findViewById(R.id.loginButton);
    }
}