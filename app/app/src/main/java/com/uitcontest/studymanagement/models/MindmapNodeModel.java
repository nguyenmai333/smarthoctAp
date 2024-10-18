package com.uitcontest.studymanagement.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MindmapNodeModel {
    @SerializedName("main_content")
    private String main_content;

    @SerializedName("childs")
    private List<String> childs;

    public MindmapNodeModel(String main_content, List<String> childs) {
        this.main_content = main_content;
        this.childs = childs;
    }

    public String getMainContent() {
        return main_content;
    }

    public void setMainContent(String main_content) {
        this.main_content = main_content;
    }

    public List<String> getChilds() {
        return childs;
    }

    public void setChilds(List<String> childs) {
        this.childs = childs;
    }
}