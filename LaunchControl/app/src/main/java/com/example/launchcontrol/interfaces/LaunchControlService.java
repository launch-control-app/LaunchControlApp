package com.example.launchcontrol.interfaces;

import com.example.launchcontrol.models.Login;
import com.example.launchcontrol.models.SignUp;
import com.example.launchcontrol.models.Token;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LaunchControlService {
    @POST("/login")
    @FormUrlEncoded
    Call<Token> loginAccount(@Field("email") String email,
                             @Field("password") String password);

    @POST("/signup")
    @FormUrlEncoded
    Call<Token> signUp(@Field("email") String email,
                               @Field("password") String password,
                               @Field("vin") String vin);
}
