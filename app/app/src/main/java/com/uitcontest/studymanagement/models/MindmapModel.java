package com.uitcontest.studymanagement.models;

import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Node;

import java.util.List;

public class MindmapModel {
    @SerializedName("main_topic")
    private String main_topic;

    @SerializedName("Childs")
    private List<MindmapNodeModel> childNodes;

    public MindmapModel(String main_topic, List<MindmapNodeModel> childNodes) {
        this.main_topic = main_topic;
        this.childNodes = childNodes;
    }

    public String getMainTopic() {
        return main_topic;
    }

    public void setMainTopic(String main_topic) {
        this.main_topic = main_topic;
    }

    public List<MindmapNodeModel> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<MindmapNodeModel> childNodes) {
        this.childNodes = childNodes;
    }

}


