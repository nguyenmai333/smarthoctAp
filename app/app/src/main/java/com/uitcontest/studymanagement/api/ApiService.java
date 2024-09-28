package com.uitcontest.studymanagement.api;

import com.uitcontest.studymanagement.RegisterUserRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @Multipart
    @POST("/summarize/")
    Call<ResponseBody> uploadText(@Part("text") String text);

    @Multipart
    @POST("/detect-text")
    Call<ResponseBody> uploadImage(
            @Header("Authorization") String token,
            @Part MultipartBody.Part image
    );

    @POST("/register/")
    Call<ResponseBody> registerUser(@Body RegisterUserRequest request);

    @FormUrlEncoded
    @POST("/token")
    Call<ResponseBody> loginUser(
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password,
            @Field("scope") String scope,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );
}
