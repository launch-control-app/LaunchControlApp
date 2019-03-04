package com.example.launchcontrol.utilities;

import com.example.launchcontrol.interfaces.LaunchControlService;

import retrofit2.Retrofit;

public class LoginUtil {

    private LoginUtil() {}

    public static final String BASE_URL = "https://afternoon-mountain-12604.herokuapp.com";

    public static LaunchControlService  getLaunchControlService()
    {
        return RetrofitUtil.getRetrofitClient(BASE_URL).create(LaunchControlService.class);
    }

}
