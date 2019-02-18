/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Forgot password form, to submit a forgot password request
 */
package com.example.launchcontrol.activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.launchcontrol.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputEditText email;
    Button request_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.forgotPassword_email);
        request_reset = findViewById(R.id.forgotPassword_reset_request);
    }
}
