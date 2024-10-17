package com.uitcontest.studymanagement.requests;

public class TextRequest {
    private String text;

    public TextRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}