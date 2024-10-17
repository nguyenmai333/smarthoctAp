package com.uitcontest.studymanagement.models;

import java.util.List;

public class MCQModel {
    private int questionNumber;
    private String questionTitle;
    private String answer;
    private List<String> distractors;

    public MCQModel(int questionNumber, String questionTitle, String answer, List<String> distractors) {
        this.questionNumber = questionNumber;
        this.questionTitle = questionTitle;
        this.answer = answer;
        this.distractors = distractors;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getDistractors() {
        return distractors;
    }

    public void setDistractors(List<String> distractors) {
        this.distractors = distractors;
    }
}

