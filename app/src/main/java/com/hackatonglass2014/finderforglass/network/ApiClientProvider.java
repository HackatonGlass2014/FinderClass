package com.hackatonglass2014.finderforglass.network;

import com.hackatonglass2014.finderforglass.BuildConfig;
import com.hackatonglass2014.finderforglass.Constants;

import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by anton on 14.12.14.
 */
public class ApiClientProvider {

    private static final String BASE_ENDPOINT = "https://camfind.p.mashape.com/";

    private static ApiClient client;

    public static ApiClient provideApiClient() {
        if (client == null) {
            client = new RestAdapter.Builder()
                    .setEndpoint(Endpoints.newFixedEndpoint(BASE_ENDPOINT))
                    .setRequestInterceptor(getRequestInterceptor())
                    .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                    .build().create(ApiClient.class);
        }
        return client;
    }

    private static RequestInterceptor getRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("X-Mashape-Key", Constants.TOKEN);
            }
        };
    }
}
