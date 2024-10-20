package com.uitcontest.studymanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.uitcontest.studymanagement.adapters.MindmapNodeAdapter;
import com.uitcontest.studymanagement.requests.MindmapRequest;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.api.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MindmapActivity extends AppCompatActivity {

    private static final int ADD_DOCUMENT_REQUEST_CODE = 1;
    private RecyclerView documentList;
    private AppCompatButton addButton, createButton;
    private List<String> documents = new ArrayList<>();
    private MindmapNodeAdapter mindmapNodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mindmap);

        // Initialize view
        initializeView();

        // Get documents
        getDocuments();

        // Update the adapter
        mindmapNodeAdapter.updateDocuments(documents);

        // Handle add button click
        addButton.setOnClickListener(v -> addDocument());

        // Handle create button click
        createButton.setOnClickListener(v -> createMindmap());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DOCUMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Retrieve the selected document
            String selectedDocument = data.getStringExtra("selectedDocument");
            Log.d("Document Before", "Document Size: " + documents.size());
            if (selectedDocument != null) {
                // Convert it into List<String>
                convertTextToList(selectedDocument);
                Log.d("Document", "Selected Document: " + selectedDocument);
                Log.d("Document After", "Document Size: " + documents.size());
                mindmapNodeAdapter.updateDocuments(documents);
            }
        }
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
    }

    private void convertTextToList(String prevDoc) {
        // Split the document by "."
        String[] docs = prevDoc.split("\\.");
        List<String> newDocuments = new ArrayList<>(Arrays.asList(docs));

        // Trim each document in the new list
        newDocuments.replaceAll(String::trim);

        // Append the new documents to the existing list
        documents.addAll(newDocuments);
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
                    Log.d("MindmapModel", "MindmapModel created successfully");
                    String mindmap = null;
                    try {
                        mindmap = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("MindmapModel", "MindmapModel data: " + mindmap);
                    // Navigate to the mindmap view activity
                    Intent intent = new Intent(MindmapActivity.this, MindmapViewActivity.class);
                    // Pass the mindmap response
                    intent.putExtra("mindmap", mindmap);
                    startActivity(intent);
                } else {
                    Log.e("MindmapModel", "Failed to create mindmap: " + response.code());
                    // Get detail error
                    try {
                        assert response.errorBody() != null;
                        Log.e("MindmapModel", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e("MindmapModel", "Error: " + throwable.getMessage());
            }
        });

    }

    private void addDocument() {
        // Go to the add document activity
        startActivityForResult(new Intent(MindmapActivity.this, AddDocumentActivity.class), ADD_DOCUMENT_REQUEST_CODE);
    }

    private void initializeView() {
        documentList = findViewById(R.id.documentList);
        addButton = findViewById(R.id.addButton);
        createButton = findViewById(R.id.createButton);

        // Set up RecyclerView after getting the documents
        mindmapNodeAdapter = new MindmapNodeAdapter(this, documents);
        documentList.setLayoutManager(new LinearLayoutManager(this));
        documentList.setAdapter(mindmapNodeAdapter);
    }
}