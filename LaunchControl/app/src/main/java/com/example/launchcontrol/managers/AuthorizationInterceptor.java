package com.example.launchcontrol.managers;

import android.content.Context;
import android.util.Base64;

import com.example.launchcontrol.models.Authorization;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    private Context context;

    public AuthorizationInterceptor(Context context)
    {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        Request request = chain.request();

        if (response.code() == 401 || response.code() == 403)
        {
            SessionManager sessionManager = SessionManager.getSessionManager(context);
            //sessionManager.invalidate();

            //TODO Figure this out?
            String authKey = getAuthoratizationHeader(sessionManager.getEmail(), sessionManager.getPassword());

            retrofit2.Response<Authorization> loginResponse = sessionManager.loginAccount(authKey).execute();

            if (loginResponse.isSuccessful())
            {
                Authorization authorization = loginResponse.body();

                sessionManager.saveToken(authorization.getToken());

                Request.Builder builder = request.newBuilder().header("Authorization", sessionManager.getToken()).
                        method(request.method(), request.body());
                response = chain.proceed(builder.build());
            }


        }

        return response;
    }

    private static String getAuthoratizationHeader(String email, String password)
    {
        String credential = email + ":" + password;
        return "Basic " + Base64.encodeToString(credential.getBytes(), Base64.DEFAULT);
    }
}
