package com.hackatonglass2014.finderforglass.network;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by anton on 14.12.14.
 */
public interface ApiClient {

    @FormUrlEncoded
    @POST("/image_requests")
    void uploadPhoto(@Field("image_request[locale]") String locale,
                         @Field("image_request[image]") TypedFile file,
                         Callback<UploadResponse> callback);

    @GET("/image_responses/{token}")
    void getPhoto(@Path("token") String imageToken,
                      Callback<RecognizeResponse> callback);
}
