/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Registration form, to sign up a new user
 */
package com.example.launchcontrol.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.example.launchcontrol.R;
import com.example.launchcontrol.interfaces.AuthenticationListener;
import com.example.launchcontrol.managers.SessionManager;
import com.example.launchcontrol.managers.WebSocketManager;
import com.example.launchcontrol.models.Token;
import com.example.launchcontrol.utilities.LoginUtil;
import com.example.launchcontrol.utilities.SnackbarMaker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements AuthenticationListener {

    ConstraintLayout constraintLayout;

    TextInputEditText email, vin, password, verify_password;

    Button sign_up;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get view
        constraintLayout = findViewById(R.id.registerActivity_rootLayout);

        setupProgressDialog();

        email = findViewById(R.id.registerActivity_email);
        vin = findViewById(R.id.registerActivity_vin);
        password = findViewById(R.id.registerActivity_password);
        verify_password = findViewById(R.id.registerActivity_verify_password);

        sign_up = findViewById(R.id.registerActivity_sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLoginDetails()) {
                    progressDialog.show();
                    LoginUtil.getLaunchControlService().signUp(email.getText().toString(), password.getText().toString(), vin.getText().toString()).enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(Call<Token> call, Response<Token> response) {
                            if (response.body() == null || response.body().getToken() == null)
                            {
                                progressDialog.dismiss();
                                SnackbarMaker.MakeCustomSnackbar(constraintLayout, "Duplicate username!");
                                return;
                            }
                            RegisterActivity.this.processToken(response.body().getToken());
                        }

                        @Override
                        public void onFailure(Call<Token> call, Throwable t) {
                            progressDialog.dismiss();
                            SnackbarMaker.MakeCustomSnackbar(constraintLayout, "Something went wrong with the server! Please try again later.");
                        }
                    });
                }
            }
        });
    }


    private boolean validateLoginDetails()
    {
        boolean fields_check =  !TextUtils.isEmpty(email.getText()) &&
                !TextUtils.isEmpty(password.getText()) &&
                !TextUtils.isEmpty(vin.getText()) &&
                !TextUtils.isEmpty(verify_password.getText());
        if (!fields_check)
        {
            snackbarMaker("One or more fields are empty!");
            return false;
        }

        boolean password_check = password.getText().toString().equals(verify_password.getText().toString());
        if (!password_check)
        {
            snackbarMaker("The passwords do not match!");
            return false;
        }

        boolean email_check = Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches();
        if (!email_check)
        {
            snackbarMaker("Email address isn't valid!");
            return false;
        }

        return true;
    }

    private void snackbarMaker(String text)
    {
        final Snackbar snackbar = Snackbar
                .make(constraintLayout, text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void processToken(String token)
    {
        SessionManager.getSessionManager(this).saveToken(token);
        WebSocketManager.getWebSocket(this, true);
    }

    @Override
    public void onUserLoggedOut() {
        //Not needed...
    }

    @Override
    public void onUserLogin() {
        progressDialog.dismiss();
        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
        SessionManager.getSessionManager(this).unregisterAuthenticationListener(this);
        SessionManager.getSessionManager(this).saveEmail(email.getText().toString());
        SessionManager.getSessionManager(this).savePassword(password.getText().toString());
        startActivity(intent);
    }

    private void setupProgressDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sign Up");
        progressDialog.setMessage("Signing up and logging in...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
    }

}
