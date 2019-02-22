/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Forgot password form, to submit a forgot password request
 */
package com.example.launchcontrol.activities;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.example.launchcontrol.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputEditText email;
    Button request_reset;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        constraintLayout = findViewById(R.id.forgotPasswordActivity_rootLayout);

        email = findViewById(R.id.forgotPassword_email);
        request_reset = findViewById(R.id.forgotPassword_reset_request);
        request_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail())
                {
                    //TODO: Send forgot password request
                }
            }
        });
    }

    private boolean validateEmail()
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
        {
            Snackbar.make(constraintLayout, "Email address isn't valid!", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
