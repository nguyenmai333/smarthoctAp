package com.uitcontest.studymanagement.utils;

import java.util.List;

public class MCQResult {
    private String Question;
    private String Answer;
    private List<String> Distractor;

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public List<String> getDistractor() {
        return Distractor;
    }

    public void setDistractor(List<String> distractor) {
        Distractor = distractor;
    }
}