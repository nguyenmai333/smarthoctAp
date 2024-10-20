package com.uitcontest.studymanagement.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserModel {
    private static UserModel instance;

    private String username;
    private String email;
    @SerializedName("full_name")
    private String fullName;
    private String sex;
    @SerializedName("processed_texts")
    private List<ProcessedTextModel> processedTexts;

    private UserModel () { }

    public static synchronized UserModel getInstance() {
        if (instance == null) {
            instance = new UserModel();
        }
        return instance;
    }

    public static UserModel fromJson(String json) {
        Gson gson = new Gson();
        UserModel userModel = gson.fromJson(json, UserModel.class);
        instance = userModel;
        return userModel;
    }

    public void clear() {
        instance = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<ProcessedTextModel> getProcessedTexts() {
        return processedTexts;
    }

    public void setProcessedTexts(List<ProcessedTextModel> processedTexts) {
        this.processedTexts = processedTexts;
    }

}
