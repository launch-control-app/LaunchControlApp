package com.example.launchcontrol.managers;

import android.content.Context;

import com.example.launchcontrol.interfaces.Session;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class TokenRenewInterceptor implements Interceptor {

    private Context context;

    public TokenRenewInterceptor(Context context)
    {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        //TODO: Verify this response header
        String newToken = response.header("x-auth-token");
        if (newToken != null)
            SessionManager.getSessionManager(context).saveToken(newToken);

        return response;
    }
}
