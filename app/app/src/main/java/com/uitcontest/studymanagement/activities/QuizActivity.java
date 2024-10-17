package com.uitcontest.studymanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.api.ApiClient;
import com.uitcontest.studymanagement.models.MCQModel;
import com.uitcontest.studymanagement.requests.TextRequest;
import com.uitcontest.studymanagement.utils.MCQProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class QuizActivity extends AppCompatActivity {

    private AppCompatButton submitButton;
    private LinearLayout questionContainer;
    private String convertedText;
    private List<MCQModel> mcqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize view
        initializeView();

        // Get converted text
        getConvertedText();

        // Generate MCQ
        createMCQ(convertedText);

        // Handle submit button click
        submitButton.setOnClickListener(v -> validateAndSubmitAnswers());
    }

    private void getConvertedText() {
        convertedText = getIntent().getStringExtra("convertedText");
        Log.d("Converted Text", "Converted Text: " + convertedText);
    }

    private void createMCQ(String convertedText) {
        // Generate RequestBody
        TextRequest request = new TextRequest(convertedText);
        // Call the API to generate MCQ
        Call<ResponseBody> call = ApiClient.getApiService().generateMCQ(request);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        String responseString = response.body().string();
                        Log.d("MCQ", "MCQ Response: " + responseString);

                        // Parse the response to MCQModel list
                        mcqList = MCQProcessor.processMCQResponse(responseString);
                        createQuestions(mcqList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("MCQ", "Failed to generate MCQ: " + t.getMessage());
                Toast.makeText(QuizActivity.this, "Failed to generate MCQ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createQuestions(List<MCQModel> mcqModels) {
        for (int i = 0; i < mcqModels.size(); i++) {
            MCQModel mcqModel = mcqModels.get(i);
            LinearLayout questionLayout = createSingleQuestion(mcqModel, i + 1);
            questionContainer.addView(questionLayout);
        }
    }

    private LinearLayout createSingleQuestion(MCQModel mcqModel, int questionNumber) {
        TextView questionTextView = createQuestionTextView(questionNumber, mcqModel.getQuestionTitle());
        RadioGroup radioGroup = createOptionsGroup(mcqModel);

        LinearLayout questionLayout = new LinearLayout(this);
        questionLayout.setOrientation(LinearLayout.VERTICAL);
        questionLayout.addView(questionTextView);
        questionLayout.addView(radioGroup);

        return questionLayout;
    }

    private TextView createQuestionTextView(int questionNumber, String questionTitle) {
        TextView questionTextView = new TextView(this);
        questionTextView.setText("Question " + questionNumber + ": " + questionTitle);
        questionTextView.setTextSize(18);
        questionTextView.setTypeface(questionTextView.getTypeface(), Typeface.BOLD);
        return questionTextView;
    }

    private RadioGroup createOptionsGroup(MCQModel mcqModel) {
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        // Combine the correct answer and distractors into one list and shuffle them
        List<String> options = new ArrayList<>(mcqModel.getDistractors());
        options.add(mcqModel.getAnswer()); // Add the correct answer
        Collections.shuffle(options); // Shuffle to randomize

        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioGroup.addView(radioButton);
        }

        return radioGroup;
    }

    private void validateAndSubmitAnswers() {
        StringBuilder result = new StringBuilder();
        boolean allQuestionsAnswered = true;
        int correctAnswers = 0;

        for (int i = 0; i < questionContainer.getChildCount(); i++) {
            View child = questionContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                RadioGroup radioGroup = (RadioGroup) ((LinearLayout) child).getChildAt(1);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedAnswer = selectedRadioButton.getText().toString();
                    result.append("Answer to Question ").append(i + 1).append(": ")
                            .append(selectedAnswer).append("\n");

                    if (isCorrectAnswer(mcqList.get(i), selectedAnswer)) {
                        correctAnswers++;
                    }
                    else {
                        result.append("Correct Answer: ").append(mcqList.get(i).getAnswer()).append("\n");
                    }

                } else {
                    allQuestionsAnswered = false;
                }
            }
        }

        if (allQuestionsAnswered) {
            showResultDialog(result.toString(), correctAnswers);
        } else {
            Toast.makeText(QuizActivity.this, "Please answer all questions", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResultDialog(String result, int correctAnswers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Results");
        builder.setMessage(result + "\nTotal Correct Answers: " + correctAnswers);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isCorrectAnswer(MCQModel mcqModel, String selectedAnswer) {
        return mcqModel.getAnswer().equals(selectedAnswer);
    }

    private void initializeView() {
        submitButton = findViewById(R.id.submitButton);
        questionContainer = findViewById(R.id.questionContainer);
    }
}