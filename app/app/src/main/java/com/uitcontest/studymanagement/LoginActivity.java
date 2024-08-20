package com.uitcontest.studymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

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

        // Handle focus change for email input field
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Handle focus event for email input
                    handleEmailFocus();
                }
            }
        });

        // Handle focus change for password input field
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Handle focus event for password input
                    handlePasswordFocus();
                }
            }
        });
    }

    private void handlePasswordFocus() {
    }

    private void handleEmailFocus() {
    }

    private void handleRegisterNow() {
        switchActivity(this, RegisterActivity.class);
    }

    private void handleForgotPassword() {
    }

    private void handleLogin() {
        String email = Objects.requireNonNull(emailEditText.getText()).toString();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();

        switchActivity(this, MainActivity.class);
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