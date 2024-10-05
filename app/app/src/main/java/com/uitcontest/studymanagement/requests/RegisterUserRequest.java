package com.uitcontest.studymanagement.requests;

public class RegisterUserRequest {
    private String username;
    private String password;
    private String email;
    private String full_name;
    private String sex;

    public RegisterUserRequest(String username, String password, String email, String full_name, String sex) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.full_name = full_name;
        this.sex = sex;
    }
}
