/**
 * Creation Date: Friday, February 1st 2019
 * Original Author: Rohan Rao
 * Contents of File: Used for interfacing with the web server
 */
package com.example.launchcontrol.managers;

import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class WebSocketManager {

    private Socket socket;
    private static WebSocketManager webSocketManager;
    private String domainName = "https://afternoon-mountain-12604.herokuapp.com/";

    public static Socket getWebSocket(Context context, boolean forceConnect)
    {
        if (webSocketManager == null)
            webSocketManager = new WebSocketManager(context);
        else if (forceConnect)
            webSocketManager.connect(context);

        return webSocketManager.socket;
    }

    private WebSocketManager(final Context context)
    {
        this.connect(context);
    }

    private void connect(final Context context)
    {
        Log.d("LOGIN", "called connect...");
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }

        try {
            socket = IO.socket(domainName);
            socket.connect();
            socket.once("connect", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = new JSONObject();
                    try
                    {
                        obj.put("token",SessionManager.getSessionManager(context).getToken());
                    }catch (Exception e) {
                        //do nothing
                    }
                    socket.emit("authenticate", obj)
                            .once("authenticated", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d("LOGIN", "LOGGED IN!!!");
                                    SessionManager.getSessionManager(context).setAuthenticated(true);

                                }
                            })
                            .once("unauthorized", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d("LOGIN", "UNAUTHORIZED");
                                    SessionManager.getSessionManager(context).setAuthenticated(false);
                                }
                            });
                }
            });

        } catch (URISyntaxException e) {
            Log.d("LOGIN", "HERE_FOUR");
        }
    }

}
