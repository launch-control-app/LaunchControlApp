package com.example.launchcontrol.interfaces;

import com.example.launchcontrol.models.AccountInfo;
import com.example.launchcontrol.models.Authorization;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LaunchControlService {
    //TODO: Replace this Dummy URL
    String URL = "https://api.example.com" + "/v1/";

    @POST("login")
    Call<Authorization> loginAccount(@Header("Authorization") String authKey);

    @GET("accounts/{accountId}")
    Call<AccountInfo> getAccountInfo(@Header("Authorization") String authKey,
                                     @Path("accountId") String accountId);
}
