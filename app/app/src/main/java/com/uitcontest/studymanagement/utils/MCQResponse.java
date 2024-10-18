package com.uitcontest.studymanagement.utils;

import java.util.List;

public class MCQResponse {
    private List<MCQResult> result;

    public List<MCQResult> getMcq_result() {
        return result;
    }

    public void setMcq_result(List<MCQResult> mcq_result) {
        this.result = mcq_result;
    }
}