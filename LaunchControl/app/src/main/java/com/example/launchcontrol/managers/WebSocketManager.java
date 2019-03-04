/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Used for interfacing with the web server
 */
package com.example.launchcontrol.managers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.launchcontrol.utilities.ReconnectSnackbarMaker;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class WebSocketManager {

    private Socket socket;
    private static WebSocketManager webSocketManager;
    private String domainName = "https://afternoon-mountain-12604.herokuapp.com/";

    public static Socket getWebSocket(Context context)
    {
        if (webSocketManager == null)
            webSocketManager = new WebSocketManager(context);
        else
            webSocketManager.connect(context);

        return webSocketManager.socket;
    }

    private WebSocketManager(final Context context)
    {
        this.connect(context);
    }

    public void connect(final Context context)
    {
        try
        {
            socket = IO.socket(domainName);
            socket.connect();
            socket.on("connect", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = new JSONObject();
                    try
                    {
                        obj.put("token",SessionManager.getSessionManager(context).getToken());
                    }catch (Exception e)
                    {
                        //do nothing
                    }
                    Log.d("LOGIN", "TOKEN IN WEBSOCKET: " + "{\"token\":" + "\"" + SessionManager.getSessionManager(context).getToken() + "\"" + "}");
                    socket.emit("authenticate", obj)
                            .on("authenticated", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d("LOGIN", "AUTHENTICATION CHANNEL");
                                    SessionManager.getSessionManager(context).setAuthenticated(true);

                                }
                            })
                            .on("unauthorized", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d("LOGIN", "UNAUTHORIZED");
                                }
                            });
                }
            });

        } catch (URISyntaxException e) {}
    }

}
