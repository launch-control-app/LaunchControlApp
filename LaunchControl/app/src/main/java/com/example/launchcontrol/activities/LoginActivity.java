/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Modifications by: Akash Patel
 * Contents of File: Login form, to login an existing user (also used as a landing page)
 */
package com.example.launchcontrol.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.launchcontrol.R;
import com.example.launchcontrol.interfaces.AuthenticationListener;
import com.example.launchcontrol.interfaces.BluetoothConnectionStatusReceiver;
import com.example.launchcontrol.interfaces.BluetoothDataReceiver;
import com.example.launchcontrol.managers.BluetoothManager;
import com.example.launchcontrol.managers.SessionManager;
import com.example.launchcontrol.managers.WebSocketManager;
import com.example.launchcontrol.models.DataPoint;
import com.example.launchcontrol.models.Token;
import com.example.launchcontrol.utilities.LoginUtil;
import com.example.launchcontrol.utilities.PermsUtil;
import com.example.launchcontrol.utilities.ReconnectSnackbarMaker;
import com.github.nkzawa.socketio.client.Socket;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements AuthenticationListener {

    ConstraintLayout constraintLayout;

    TextInputEditText email, password;

    TextView forgot_password;

    Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Views
        constraintLayout = findViewById(R.id.loginActivity_rootLayout);

        PermsUtil.getPermissions(this);

        //Set up auth listener
        SessionManager.getSessionManager(this).addAuthenticationListener(this);


        email = findViewById(R.id.loginActivity_email);
        password = findViewById(R.id.loginActivity_password);

        forgot_password = findViewById(R.id.loginActivity_forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        login = findViewById(R.id.loginActivity_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement Login
                if (validateLoginDetails()) {
                    LoginUtil.getLaunchControlService().loginAccount(email.getText().toString(), password.getText().toString()).enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(Call<Token> call, Response<Token> response) {
                            Log.d("LOGIN", "TOKEN IN RESPONSE: " + response.body().getToken());
                            LoginActivity.this.saveToken(response.body().getToken());
                        }

                        @Override
                        public void onFailure(Call<Token> call, Throwable t) {

                        }
                    });
                }

            }
        });

        signup = findViewById(R.id.loginActivity_sign_up);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    private boolean validateLoginDetails()
    {
        boolean fields_check =  !TextUtils.isEmpty(email.getText()) &&
                !TextUtils.isEmpty(password.getText());
        if (!fields_check)
        {
            snackbarMaker("One or more fields are empty!");
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

    private void saveToken(String token)
    {
        Log.d("LOGIN", "TOKEN IN LOGINACTIVITY: " + token);
        SessionManager.getSessionManager(this).saveToken(token);
        Socket socket = WebSocketManager.getWebSocket(this);
        BluetoothManager.getBluetoothManager(this).setWebSocket(socket);
    }

    @Override
    public void onUserLoggedOut() {

    }

    @Override
    public void onUserLogin() {
        Log.d("LOGIN", "LOGGED IN!");
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        finish();
        startActivity(intent);

    }
}
