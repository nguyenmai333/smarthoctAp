package com.uitcontest.studymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.uitcontest.studymanagement.api.ApiClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MindmapActivity extends AppCompatActivity {

    private RecyclerView documentList;
    private AppCompatButton addButton, createButton;
    private List<String> documents = new ArrayList<>();
    private DocumentAdapter documentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mindmap);

        // Initialize view
        initializeView();

        // Get documents
        getDocuments();

        // Update the adapter
        documentAdapter.updateDocuments(documents);

        // Handle add button click
        addButton.setOnClickListener(v -> addDocument());

        // Handle create button click
        createButton.setOnClickListener(v -> createMindmap());
    }

    private void getDocuments() {
        // Firstly get the document from the previous intent
        String prevDoc = getIntent().getStringExtra("convertedText");
        Log.d("Document", "Previous Document: " + prevDoc);
        if (prevDoc != null) {
            // Convert it into List<String>
            convertTextToList(prevDoc);
            Log.d("Document", "Document: " + documents.toString());
        }

        // TODO: Get the documents from the server
    }

    private void convertTextToList(String prevDoc) {
        // Split the document by "."
        documents = Arrays.asList(prevDoc.split("\\."));
    }

    private void createMindmap() {
        // Create request
        MindmapRequest request = new MindmapRequest(documents);

        // Send request
        Call<ResponseBody> call = ApiClient.getApiService().createMindmap(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Mindmap", "Mindmap created successfully");
                    String mindmap = null;
                    try {
                        mindmap = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("Mindmap", "Mindmap data: " + mindmap);
                    // Navigate to the mindmap view activity
                    Intent intent = new Intent(MindmapActivity.this, MindmapViewActivity.class);
                    // Pass the mindmap response
                    intent.putExtra("mindmap", mindmap);
                    startActivity(intent);
                } else {
                    Log.e("Mindmap", "Failed to create mindmap: " + response.code());
                    // Get detail error
                    try {
                        assert response.errorBody() != null;
                        Log.e("Mindmap", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e("Mindmap", "Error: " + throwable.getMessage());
            }
        });

    }

    private void addDocument() {

    }

    private void initializeView() {
        documentList = findViewById(R.id.documentList);
        addButton = findViewById(R.id.addButton);
        createButton = findViewById(R.id.createButton);

        // Set up RecyclerView after getting the documents
        documentAdapter = new DocumentAdapter(this, documents);
        documentList.setLayoutManager(new LinearLayoutManager(this));
        documentList.setAdapter(documentAdapter);
    }
}