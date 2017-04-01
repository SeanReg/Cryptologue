package com.teamsynergy.cryptologue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by Sean on 3/28/2017.
 */

public class MessagingService extends Service {
    private static final int MESSAGE_TYPE_IDENTIFY  = 1;
    private static final int MESSAGE_TYPE_SEND_CHAT = 2;

    private WebSocket mSocket = null;
    private MessageListener mMessageListener = null;

    private static MessagingService mServiceInstance = null;

    private UserAccount mCurAccount = null;
    private List<Chatroom> mChatrooms = null;

    public interface  MessageListener {
        public void onMessageRecieved(Message s);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceInstance = this;

        try {
            connectSocket();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    public static MessagingService getInstance() {
        return mServiceInstance;
    }

    public void connectSocket() throws WebSocketException {
        ParseInit.start(this);

        mCurAccount = AccountManager.getInstance().getCurrentAccount();
        if (mCurAccount == null)
            throw new WebSocketException("No current account!");

        mSocket = new WebSocketConnection();

        URI server = null;
        try {
             server = new URI("ws://ec2-52-33-81-186.us-west-2.compute.amazonaws.com:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect(server, mSocketObserver);
    }

    public void setMessagingListener(MessageListener listener) {
        mMessageListener = listener;
    }

    public void socketSendMessage(String msg, String chatroomId) {
        try {
            checkConnection();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mType", MESSAGE_TYPE_SEND_CHAT);
                jsonObject.put("chatroomId", chatroomId);
                jsonObject.put("msg", msg);

                mSocket.sendTextMessage(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (WebSocketClosedException e) {
            e.printStackTrace();
        }
    }


    private void socketIdentify() throws WebSocketClosedException {
        checkConnection();

        AccountManager accountManager = AccountManager.getInstance();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mType", MESSAGE_TYPE_IDENTIFY);
            jsonObject.put("clientId", accountManager.getCurrentAccount().getParseUser().getObjectId());
            mSocket.sendTextMessage(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkConnection() throws WebSocketClosedException {
        if (mSocket == null || !mSocket.isConnected())
            throw new WebSocketClosedException("WebSocket not connected!");
    }
    
    private class WebSocketClosedException extends WebSocketException {
        public WebSocketClosedException(String message) {
            super(message);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final WebSocket.WebSocketConnectionObserver mSocketObserver = new WebSocket.WebSocketConnectionObserver() {
        @Override
        public void onOpen() {
            Log.d("Socket", "Connected to Server!");
            try {
                socketIdentify();
            } catch (WebSocketClosedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification webSocketCloseNotification, String s) {
            Log.d("Socket", "Disconnected from Server!");
        }

        @Override
        public void onTextMessage(String s) {
            if (mMessageListener != null)
                mMessageListener.onMessageRecieved(new Message(s));

            Log.d("Socket", "Got message " + s);
        }

        @Override
        public void onRawTextMessage(byte[] bytes) {

        }

        @Override
        public void onBinaryMessage(byte[] bytes) {

        }
    };
}
