package com.uitcontest.studymanagement.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.uitcontest.studymanagement.R;

public class QuizActivity extends AppCompatActivity {

    private AppCompatButton submitButton;
    private LinearLayout questionContainer;
    private int numberOfQuestions = 6; // Number of questions in the quiz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize view
        initializeView();

        // Dynamically create questions
        createQuestions(numberOfQuestions);

        // Handle submit button click
        submitButton.setOnClickListener(v -> validateAndSubmitAnswers());
    }

    private void createQuestions(int numberOfQuestions) {
        for (int i = 0; i < numberOfQuestions; i++) {
            LinearLayout questionLayout = createSingleQuestion(i + 1);
            questionContainer.addView(questionLayout);
        }
    }

    // Create a single question layout
    private LinearLayout createSingleQuestion(int questionNumber) {
        // Create a TextView for the question
        TextView questionTextView = createQuestionTextView(questionNumber);

        // Create a RadioGroup for the options
        RadioGroup radioGroup = createOptionsGroup();

        // Container layout for each question
        LinearLayout questionLayout = new LinearLayout(this);
        questionLayout.setOrientation(LinearLayout.VERTICAL);
        questionLayout.addView(questionTextView);
        questionLayout.addView(radioGroup);

        return questionLayout;
    }

    private TextView createQuestionTextView(int questionNumber) {
        TextView questionTextView = new TextView(this);
        questionTextView.setText("Question " + questionNumber + ":");
        questionTextView.setTextSize(18);
        questionTextView.setTypeface(questionTextView.getTypeface(), Typeface.BOLD);
        return questionTextView;
    }

    private RadioGroup createOptionsGroup() {
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        // Create four options
        for (int j = 1; j <= 4; j++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText("Option " + j);
            radioGroup.addView(radioButton);
        }
        return radioGroup;
    }

    private void validateAndSubmitAnswers() {
        StringBuilder result = new StringBuilder();
        boolean allQuestionsAnswered = true;

        // Loop through the questionContainer to check answers
        for (int i = 0; i < questionContainer.getChildCount(); i++) {
            View child = questionContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                RadioGroup radioGroup = (RadioGroup) ((LinearLayout) child).getChildAt(1);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    result.append("Answer to Question ").append(i + 1).append(": ")
                            .append(selectedRadioButton.getText().toString()).append("\n");
                } else {
                    allQuestionsAnswered = false;
                }
            }
        }

        if (allQuestionsAnswered) {
            Toast.makeText(QuizActivity.this, result.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(QuizActivity.this, "Please answer all questions", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeView() {
        submitButton = findViewById(R.id.submitButton);
        questionContainer = findViewById(R.id.questionContainer);
    }
}