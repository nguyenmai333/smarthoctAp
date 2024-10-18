package com.uitcontest.studymanagement.api;

import com.uitcontest.studymanagement.requests.MindmapRequest;
import com.uitcontest.studymanagement.requests.RegisterUserRequest;
import com.uitcontest.studymanagement.requests.SummarizeRequest;
import com.uitcontest.studymanagement.requests.TextRequest;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("/detect-text/")
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
    
    @POST("/create_mindmap/")
    Call<ResponseBody> createMindmap(@Body MindmapRequest request);

    @Multipart
    @POST("/transcribe/")
    Call<ResponseBody> transcribeAudio(
            @Header("Authorization") String token,
            @Part MultipartBody.Part audio
    );

    @POST("/seq2mcq")
    Call<ResponseBody> generateMCQ(@Body TextRequest request);

    @POST("/summarize/")
    Call<ResponseBody> summarizeText(@Body SummarizeRequest request);
}
