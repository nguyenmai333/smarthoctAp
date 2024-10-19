package com.uitcontest.studymanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.SharedPrefManager;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.models.SearchResultModel;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartSearchActivity extends AppCompatActivity {

    private String query;
    private TextView queryTextView, resultTextView;
    private FrameLayout frameLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_search);

        // Initialize view
        initializeView();

        // Get query from intent
        getQueryFromIntent();

        // Handle smart search
        handleSmartSearch(query);
    }

    private void handleSmartSearch(String query) {
        // Show progress bar
        toggleProgressBar(true);
        // Get current token
        String token = SharedPrefManager.getInstance(this).getAuthToken();
        // Call API
        Call<ResponseBody> call = ApiClient.getApiService().smartSearch("Bearer " + token, query);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle response
                    String responseData;
                    try {
                        assert response.body() != null;
                        responseData = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("SmartSearchActivity", "Response: " + responseData);
                    // Parse response
                    Gson gson = new Gson();
                    SearchResultModel searchResultModel = gson.fromJson(responseData, SearchResultModel.class);

                    // Hide progress bar
                    toggleProgressBar(false);

                    // Get result
                    String result = searchResultModel.getResult();
                    resultTextView.setText(result);
                    Log.d("SmartSearchActivity", "Result: " + result);

                } else {
                    // Hide progress bar
                    toggleProgressBar(false);
                    // Handle error
                    try {
                        assert response.errorBody() != null;
                        Log.d("SmartSearchActivity", "Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Handle failure
                Log.d("SmartSearchActivity", "Failure: " + t.getMessage());
            }
        });
    }

    private void toggleProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            frameLayout.setClickable(false);
            // Set foreground color to semi-transparent
            frameLayout.setForeground(new ColorDrawable(Color.parseColor("#80000000")));
        } else {
            progressBar.setVisibility(View.GONE);
            frameLayout.setClickable(true);
            // Set foreground color to transparent
            frameLayout.setForeground(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        }
    }

    private void getQueryFromIntent() {
        query = getIntent().getStringExtra("query");
        Log.d("SmartSearchActivity", "Query: " + query);
        queryTextView.append(query);
    }

    private void initializeView() {
        queryTextView = findViewById(R.id.tvQuery);
        resultTextView = findViewById(R.id.tvResult);
        frameLayout = findViewById(R.id.smartSearchLayout);
        progressBar = findViewById(R.id.progressBar);
    }
}