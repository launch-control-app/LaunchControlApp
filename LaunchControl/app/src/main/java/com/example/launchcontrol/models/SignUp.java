package com.example.launchcontrol.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUp {

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("VIN")
    @Expose
    private String VIN;

    @SerializedName("password")
    @Expose
    private String password;

    public SignUp(String email,  String password,  String VIN)
    {
        this.email = email;
        this.password = password;
        this.VIN = VIN;
    }

}
