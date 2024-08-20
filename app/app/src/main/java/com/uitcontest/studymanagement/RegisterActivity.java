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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private TextView loginNowTextView;
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

        // Handle focus change for username input field
        usernameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                handleUsernameFocus();
            }
        });


        // Handle focus change for email input field
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                handleEmailFocus();
            }
        });

        // Handle focus change for password input field
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                handlePasswordFocus();
            }
        });

        // Handle focus change for confirm password input field
        confirmPasswordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                handleConfirmPasswordFocus();
            }
        });
    }
    private void handleConfirmPasswordFocus() {
    }

    private void handlePasswordFocus() {
    }

    private void handleEmailFocus() {
    }

    private void handleUsernameFocus() {
    }

    private void handleLoginNow() {
        switchActivity(this, LoginActivity.class);
    }

    private void handleForgotPassword() {
    }

    private void handleRegister() {
        String username = Objects.requireNonNull(usernameEditText.getText().toString());
        String email = Objects.requireNonNull(emailEditText.getText()).toString();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString();

        handleLoginNow();
    }

    private void switchActivity(Context currentContext, Class<?> targetActivity) {
        Intent intent = new Intent(currentContext, targetActivity);
        currentContext.startActivity(intent);
        finish();
    }

    private void initializeView() {
        usernameEditText = findViewById(R.id.etRegisterUsernameText);
        emailEditText = findViewById(R.id.etRegisterEmailText);
        passwordEditText = findViewById(R.id.etRegisterPasswordText);
        confirmPasswordEditText = findViewById(R.id.etRegisterConfirmPasswordText);
        loginNowTextView = findViewById(R.id.tvLoginNow);
        registerButton = findViewById(R.id.registerButton);
    }
}