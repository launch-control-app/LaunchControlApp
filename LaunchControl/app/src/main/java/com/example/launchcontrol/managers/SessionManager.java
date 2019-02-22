package com.example.launchcontrol.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.launchcontrol.interfaces.AuthenticationListener;
import com.example.launchcontrol.interfaces.LaunchControlService;
import com.example.launchcontrol.interfaces.Session;
import com.example.launchcontrol.models.AccountInfo;
import com.example.launchcontrol.models.Authorization;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SessionManager implements Session, LaunchControlService {

    private static SessionManager sessionManager;

    private static final String myPreferences = "MyPreferences";
    private static final String myToken = "MyToken";
    private static final String myEmailAddress = "MyEmailAddress";
    private static final String myPassword = "MyPassword";

    private Context context;

    private String emailAddress = "";
    private String password = "";
    private String token = "";

    private List<AuthenticationListener> authenticationListenerList;

    public static SessionManager getSessionManager(Context context)
    {
        if (sessionManager == null)
            sessionManager = new SessionManager(context);

        return sessionManager;
    }

    private SessionManager(Context context)
    {
        this.context = context;
        authenticationListenerList = new ArrayList<>(5);
    }

    @Override
    public boolean isLoggedOn() {
        return this.getToken() != null;
    }

    @Override
    public void saveToken(String token) {
        this.token = token;
        saveToSharedPreferences(myToken, token);
    }

    @Override
    public String getToken() {
        if (token != null)
            return token;
        else
            return retrieveFromSharedPreferences(myToken);
    }

    @Override
    public void saveEmail(String email) {
        this.emailAddress = email;
        saveToSharedPreferences(myEmailAddress, email);
    }

    @Override
    public String getEmail() {
        if (emailAddress != null)
            return emailAddress;
        else
            return retrieveFromSharedPreferences(myEmailAddress);
    }

    @Override
    public void savePassword(String password) {
        this.password = password;
        saveToSharedPreferences(myPassword, password);
    }

    @Override
    public String getPassword() {
        if (password != null)
            return  password;
        else
            return retrieveFromSharedPreferences(myPassword);
    }

    @Override
    public void invalidate() {
        removeFromSharedPreferences(myToken);
        for (AuthenticationListener authenticationListener : authenticationListenerList)
            authenticationListener.onUserLoggedOut();
    }

    public void addAuthenticationListener(AuthenticationListener authenticationListener)
    {
        this.authenticationListenerList.add(authenticationListener);
    }

    private void saveToSharedPreferences(String key, String value)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(myPreferences,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String retrieveFromSharedPreferences(String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(myPreferences,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    private void removeFromSharedPreferences(String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(myPreferences,
                Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key))
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }


    @Override
    public Call<Authorization> loginAccount(String authKey) {
        return null;
    }

    @Override
    public Call<AccountInfo> getAccountInfo(String authKey, String accountId) {
        return null;
    }
}
