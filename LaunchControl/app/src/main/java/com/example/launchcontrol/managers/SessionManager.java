package com.example.launchcontrol.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.launchcontrol.interfaces.AuthenticationListener;
import com.example.launchcontrol.interfaces.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionManager implements Session {

    private static SessionManager sessionManager;

    private static final String myPreferences = "MyPreferences";
    private static final String myToken = "MyToken";
    private static final String myEmailAddress = "MyEmailAddress";
    private static final String myPassword = "MyPassword";

    private Context context;

    private String emailAddress = "";
    private String password = "";
    private String token = "";

    private boolean authenticated = false;

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
        if (token != null && !token.equals(""))
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
        if (emailAddress != null && !emailAddress.equals(""))
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
        if (password != null && !password.equals(""))
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

    @Override
    public void validate() {
        for (AuthenticationListener authenticationListener : authenticationListenerList)
            authenticationListener.onUserLogin();
    }

    public void registerAuthenticationListener(AuthenticationListener authenticationListener)
    {
        this.authenticationListenerList.add(authenticationListener);
    }

    public void unregisterAuthenticationListener(AuthenticationListener authenticationListener)
    {
        if (authenticationListenerList.contains(authenticationListener))
            this.authenticationListenerList.remove(authenticationListener);

    }

    private void saveToSharedPreferences(String key, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    private String retrieveFromSharedPreferences(String key)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(key, "");
    }

    private void removeFromSharedPreferences(String key)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences.contains(key))
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }


    public void setAuthenticated(boolean authenticated)
    {
        this.authenticated = authenticated;
        if (this.authenticated)
            this.validate();
        else
            this.invalidate();
    }
}
