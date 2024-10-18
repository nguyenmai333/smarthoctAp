package com.uitcontest.studymanagement.requests;

public class SummarizeRequest {
    public String text;
    public int ratio;

    public SummarizeRequest(String text, int ratio) {
        this.text = text;
        this.ratio = ratio;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }
}
