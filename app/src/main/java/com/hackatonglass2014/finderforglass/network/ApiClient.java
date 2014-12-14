package com.hackatonglass2014.finderforglass.network;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by anton on 14.12.14.
 */
public interface ApiClient {

    @Multipart
    @POST("/image_requests")
    void uploadPhoto(@Part("image_request[locale]") String locale,
                     @Part("image_request[image]") TypedFile file,
                     Callback<UploadResponse> callback);

    @GET("/image_responses/{token}")
    void getPhoto(@Path("token") String imageToken,
                  Callback<RecognizeResponse> callback);
}
