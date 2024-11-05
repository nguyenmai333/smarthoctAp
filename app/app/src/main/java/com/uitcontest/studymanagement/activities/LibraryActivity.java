package com.uitcontest.studymanagement.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.adapters.ProcessedTextAdapter;
import com.uitcontest.studymanagement.models.ProcessedTextModel;
import com.uitcontest.studymanagement.models.UserModel;

import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView rvProcessedText;
    private List<ProcessedTextModel> processedTextModelList;
    private ProcessedTextAdapter processedTextAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        // Initialize view
        initializeView();

        // Get processed text
        getProcessedText();

        // Set adapter
        processedTextAdapter = new ProcessedTextAdapter(this, processedTextModelList);
        rvProcessedText.setAdapter(processedTextAdapter);
        processedTextAdapter.notifyDataSetChanged();
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
        rvProcessedText = findViewById(R.id.documentRecyclerView);

        // Set layout
        rvProcessedText.setLayoutManager(new LinearLayoutManager(this));
    }
}