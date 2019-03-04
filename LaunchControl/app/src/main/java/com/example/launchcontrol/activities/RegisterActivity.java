/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Registration form, to sign up a new user
 */
package com.example.launchcontrol.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

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
import com.github.nkzawa.socketio.client.Socket;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements AuthenticationListener {

    BluetoothManager bluetoothManager;
    ConstraintLayout constraintLayout;

    TextInputEditText email, vin, password, verify_password;

    Button sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get view
        constraintLayout = findViewById(R.id.registerActivity_rootLayout);


        email = findViewById(R.id.registerActivity_email);
        vin = findViewById(R.id.registerActivity_vin);
        password = findViewById(R.id.registerActivity_password);
        verify_password = findViewById(R.id.registerActivity_verify_password);

        sign_up = findViewById(R.id.registerActivity_sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLoginDetails())
                {
                    LoginUtil.getLaunchControlService().signUp(email.getText().toString(), password.getText().toString(), vin.getText().toString()).enqueue(new Callback<Token>() {
                        @Override
                        public void onResponse(Call<Token> call, Response<Token> response) {
                            Log.d("LOGIN", "TOKEN IN RESPONSE: " + response.body().getToken());
                            RegisterActivity.this.saveToken(response.body().getToken());
                        }

                        @Override
                        public void onFailure(Call<Token> call, Throwable t) {

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
        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
        finish();
        startActivity(intent);

    }

}
