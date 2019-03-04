/**
 * Creation Date: Saturday, March 2nd 2019
 * Original Author: Rohan Rao
 * Contents of File: Model class for logging in
 */

package com.example.launchcontrol.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
