package com.example.launchcontrol.utilities;

import android.content.Context;

import com.example.launchcontrol.managers.TokenRenewInterceptor;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    public static Retrofit getRetrofitClient(String url, Context context)
    {
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(RetrofitUtil.getOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    private static OkHttpClient getOkHttpClient(Context context)
    {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();

        okhttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);

        okhttpClientBuilder.addInterceptor(new TokenRenewInterceptor(context));

        return okhttpClientBuilder.build();
    }
}
