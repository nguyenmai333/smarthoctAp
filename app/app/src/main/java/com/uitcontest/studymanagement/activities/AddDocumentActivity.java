package com.uitcontest.studymanagement.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.adapters.AddDocumentAdapter;
import com.uitcontest.studymanagement.adapters.ProcessedTextAdapter;
import com.uitcontest.studymanagement.models.ProcessedTextModel;
import com.uitcontest.studymanagement.models.UserModel;

import java.util.List;

public class AddDocumentActivity extends AppCompatActivity {

    private RecyclerView rvDocument;
    private List<ProcessedTextModel> processedTextModelList;
    private AddDocumentAdapter addDocumentAdapter;
    private AppCompatButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        // Initialize view
        initializeView();

        // Get processed text
        getProcessedText();

        // Set adapter
        addDocumentAdapter = new AddDocumentAdapter(this, processedTextModelList);
        rvDocument.setAdapter(addDocumentAdapter);
        addDocumentAdapter.notifyDataSetChanged();

        // Handle add button click
        addButton.setOnClickListener(v -> addDocument());
    }

    private void addDocument() {
        // Pass the selected document content to the previous activity
        ProcessedTextModel selectedDocument = addDocumentAdapter.getSelectedDocument();
        if (selectedDocument != null) {
            Intent intent = new Intent();
            intent.putExtra("selectedDocument", selectedDocument.getText());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void getProcessedText() {
        UserModel userModel = UserModel.getInstance();
        processedTextModelList = userModel.getProcessedTexts();
        // Sort by date
        processedTextModelList.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        // Remove empty text
        processedTextModelList.removeIf(processedTextModel -> processedTextModel.getText().isEmpty());
    }

    private void initializeView() {
        rvDocument = findViewById(R.id.addDocumentRecyclerView);
        addButton = findViewById(R.id.addDocumentButton);

        // Set layout
        rvDocument.setLayoutManager(new LinearLayoutManager(this));
    }
}