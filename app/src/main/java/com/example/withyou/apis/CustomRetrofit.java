package com.example.withyou.apis;

import com.example.withyou.models.ImgBbResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface CustomRetrofit {

    @Multipart
    @POST("1/upload")
    Call<ImgBbResponse> uploadImage(@Part MultipartBody.Part image, @Query("key") String key, @Query("expiration") int expiration);
}
