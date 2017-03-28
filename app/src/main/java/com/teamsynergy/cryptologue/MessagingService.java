package com.teamsynergy.cryptologue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by Sean on 3/28/2017.
 */

public class MessagingService extends Service {
    private WebSocket mSocket = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            connectSocket();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    public void connectSocket() throws WebSocketException {
        mSocket = new WebSocketConnection();

        URI server = null;
        try {
             server = new URI("ws://ec2-52-33-81-186.us-west-2.compute.amazonaws.com:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect(server, new WebSocket.WebSocketConnectionObserver() {
            @Override
            public void onOpen() {
                Log.d("Socket", "Connected to Server!");
            }

            @Override
            public void onClose(WebSocketCloseNotification webSocketCloseNotification, String s) {
                Log.d("Socket", "Disconnected from Server!");
            }

            @Override
            public void onTextMessage(String s) {

            }

            @Override
            public void onRawTextMessage(byte[] bytes) {

            }

            @Override
            public void onBinaryMessage(byte[] bytes) {

            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
