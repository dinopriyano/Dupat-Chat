package com.dupat.dupatchat.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    public static String BASE_URL = "https://dinopriyano.my.id";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient()
    {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 50, TimeUnit.SECONDS))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new RetrofitClient();
        }

        return mInstance;
    }

    public APIInterface getApi()
    {
        return retrofit.create(APIInterface.class);
    }
}
