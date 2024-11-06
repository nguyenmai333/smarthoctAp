package com.uitcontest.studymanagement.requests;

public class SummarizeRequest {
    public String text;
    public float ratio;

    public SummarizeRequest(String text, float ratio) {
        this.text = text;
        this.ratio = ratio;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }
}
