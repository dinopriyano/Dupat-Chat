package com.dupat.dupatchat.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface APIInterface {

    @GET
    Call<String> pushNotif(
            @Url String url,
            @QueryMap Map<String,String> data);

}
