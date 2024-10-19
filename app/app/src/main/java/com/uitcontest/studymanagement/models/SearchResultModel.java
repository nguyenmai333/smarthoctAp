package com.uitcontest.studymanagement.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResultModel {
    @SerializedName("result")
    private String result;

    @SerializedName("Contents")
    private List<String> contents;

    public SearchResultModel(String result, List<String> contents) {
        this.result = result;
        this.contents = contents;
    }

    public String getResult() {
        return result;
    }

    public List<String> getContents() {
        return contents;
    }

}
