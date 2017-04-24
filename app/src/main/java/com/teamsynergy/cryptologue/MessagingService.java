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

    /**
     * Gets the singleton instance of the MessagingService
     * @return the instance of the MessagingService
     */
    public static MessagingService getInstance() {
        return mServiceInstance;
    }

    /**
     * Connects the application to the server socket
     * @throws WebSocketException thrown when a connection cannot be made to the server
     */
    public void connectSocket() throws WebSocketException {
        ParseInit.start(this);

        mCurAccount = AccountManager.getInstance().getCurrentAccount();
        if (mCurAccount == null)
            throw new WebSocketException("No current account!");

        //Connect to the server
        mSocket = new WebSocketConnection();
        URI server = null;
        try {
            //The server location
             server = new URI("ws://ec2-52-33-81-186.us-west-2.compute.amazonaws.com:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect(server, mSocketObserver);
    }

    public void setMessagingListener(MessageListener listener) {
        mMessageListener = listener;
    }

    /**
     * Sends a message to the server through a Websocket
     * @param msg the message to send to the sever
     * @param chatroomId the Chatroom associated with the message
     */
    public void socketSendMessage(String msg, String chatroomId) {
        try {
            //Check if connected
            checkConnection();

            //Send as JSON object
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mType", MESSAGE_TYPE_SEND_CHAT);
                jsonObject.put("chatroomId", chatroomId);
                jsonObject.put("msg", msg);

                //Send message
                mSocket.sendTextMessage(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (WebSocketClosedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify the server of the user identity
     * @throws WebSocketClosedException Thrown when the Websocket is unexpectedly closed
     */
    private void socketIdentify() throws WebSocketClosedException {
        checkConnection();

        //Create JSON object
        AccountManager accountManager = AccountManager.getInstance();
        JSONObject jsonObject = new JSONObject();
        try {
            //Set message type, identitfy and send message
            jsonObject.put("mType", MESSAGE_TYPE_IDENTIFY);
            jsonObject.put("clientId", accountManager.getCurrentAccount().getParseUser().getObjectId());
            mSocket.sendTextMessage(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the Websocket is connected or not
     * @throws WebSocketClosedException thrown if the Websocket is not connected
     */
    private void checkConnection() throws WebSocketClosedException {
        if (mSocket == null || !mSocket.isConnected())
            throw new WebSocketClosedException("WebSocket not connected!");
    }

    /**
     * Exception for when the Websocket is closed
     */
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

    /**
     * Callback listener for incoming WebSocket messages
     */
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
            if (mMessageListener != null) {
                try {
                    //Decodes the message and forwards it to the listener
                    JSONObject j = new JSONObject(s);
                    Message msg = new Message(j.getString("msg"));
                    msg.setSender(j.getString("senderId"));
                    mMessageListener.onMessageRecieved(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

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
