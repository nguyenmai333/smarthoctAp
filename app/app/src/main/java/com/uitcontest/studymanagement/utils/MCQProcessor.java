package com.uitcontest.studymanagement.utils;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.uitcontest.studymanagement.models.MCQModel;

import java.util.ArrayList;
import java.util.List;

public class MCQProcessor {

    public static List<MCQModel> processMCQResponse(String jsonResponse) {
        Gson gson = new Gson();
        List<MCQModel> mcqModels = new ArrayList<>();

        try {
            // Parse the JSON response into MCQResponse
            MCQResponse mcqResponse = gson.fromJson(jsonResponse, MCQResponse.class);
            Log.d("MCQProcessor", "MCQ Response: " + mcqResponse);
            List<MCQResult> mcqResults = mcqResponse.getMcq_result();
            Log.d("MCQProcessor", "MCQ Results: " + mcqResults.size());

            for (int i = 0; i < mcqResults.size(); i++) {
                MCQResult mcqResult = mcqResults.get(i);

                // Create MCQModel for each question
                MCQModel mcqModel = new MCQModel(
                        i + 1, // Question number (starting from 1)
                        mcqResult.getQuestion(), // Question title
                        mcqResult.getAnswer(), // Correct answer
                        mcqResult.getDistractor() // Distractors
                );

                // Add the MCQModel to the list
                mcqModels.add(mcqModel);
            }

        } catch (JsonSyntaxException e) {
            Log.e("MCQProcessor", "Error parsing JSON response: " + e.getMessage());
        }

        return mcqModels;
    }
}

