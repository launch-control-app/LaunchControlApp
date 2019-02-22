package com.example.launchcontrol.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//TODO: What does authorization do?
public class Authorization {
    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("token")
    @Expose
    private String token;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
