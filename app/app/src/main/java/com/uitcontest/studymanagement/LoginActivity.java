package com.uitcontest.studymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.api.ApiService;

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
    private final ApiService service = ApiClient.getApiService();

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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // Handle click for "Forgot Password" text
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });

        // Handle click for "Register Now" text
        registerNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterNow();
            }
        });
    }

    private void handleRegisterNow() {
        switchActivity(this, RegisterActivity.class);
    }

    private void handleForgotPassword() {
    }

    private void handleLogin() {
        String username = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();

        switchActivity(this, MainActivity.class);

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
        Call<ResponseBody> call = service.loginUser(grantType, username, password, scope, clientId, clientSecret);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Log.d("Login", "Response: " + responseBody);
                        switchActivity(LoginActivity.this, MainActivity.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void switchActivity(Context currentContext, Class<?> targetActivity) {
        Intent intent = new Intent(currentContext, targetActivity);
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