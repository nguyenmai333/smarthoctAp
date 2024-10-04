package com.uitcontest.studymanagement;

import java.util.List;

public class MindmapRequest {
    private List<String> texts;

    public MindmapRequest(List<String> texts) {
        this.texts = texts;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}

