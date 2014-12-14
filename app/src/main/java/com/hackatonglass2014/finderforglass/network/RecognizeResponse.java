package com.hackatonglass2014.finderforglass.network;

/**
 * Created by anton on 14.12.14.
 */
public class RecognizeResponse {

    public String status;
    public String name;

    @Override
    public String toString() {
        return "RecognizeResponse{" +
                "status='" + status + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
