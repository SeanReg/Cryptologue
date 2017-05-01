package com.teamsynergy.cryptologue;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by Sean on 3/28/2017.
 */

public class MessagingService extends Service {
    private static final int MESSAGE_TYPE_IDENTIFY  = 1;
    private static final int MESSAGE_TYPE_SEND_CHAT = 2;
    private static final int MESSAGE_TYPE_KEY_EXCHANGE = 3;

    public static String SOCKET_IDENTITY = null;

    private WebSocket mSocket = null;
    private MessageListener mMessageListener = Chatroom.CHATROOM_MESSAGE_LISTENER;

    private static MessagingService mServiceInstance = null;

    private UserAccount mCurAccount = null;
    private List<Chatroom> mChatrooms = null;

    private Timer   mTimer = null;
    private Handler mMainServiceThread = null;

    public interface  MessageListener {
        public void onMessageRecieved(Message s);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceInstance = this;
        mMainServiceThread = new Handler();

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

        doConnect();
    }

    private void doConnect() throws WebSocketException {
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

/*    public void setMessagingListener(MessageListener listener) {
        mMessageListener = listener;
    }*/

    /**
     * Sends a message to the server through a Websocket
     */
    public void socketSendMessage(Message m) {
        try {
            //Check if connected
            checkConnection();

            //Send as JSON object
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mType", MESSAGE_TYPE_SEND_CHAT);
                jsonObject.put("chatroomId", m.getChatroom());
                jsonObject.put("msg", m.getText());
                jsonObject.put("tagged", (m.getTag() != null));

                //Send message
                mSocket.sendTextMessage(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (WebSocketClosedException e) {
            e.printStackTrace();
        }
    }

    public void socketSendKey(byte[] key, String chatroomId, User usr) {
        try {
            //Check if connected
            checkConnection();

            //Send as JSON object
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mType", MESSAGE_TYPE_KEY_EXCHANGE);
                jsonObject.put("chatroomId", chatroomId);
                jsonObject.put("sendTo", usr.getParseUser().getObjectId());
                try {
                    byte[] secKey = KeyManager.rsaEncrypt(usr.getPublicKey(), key);
                    jsonObject.put("key", Base64.encodeToString(secKey, 0));
                    String p = "";
                    for (byte b : secKey) {
                        p += Byte.toString(b) + ", ";
                    }
                    Log.d("Got Key", p);
                    //Send message
                    mSocket.sendTextMessage(jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }


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
    public void socketIdentify() throws WebSocketClosedException {
        checkConnection();

        //Create JSON object
        AccountManager accountManager = AccountManager.getInstance();
        JSONObject jsonObject = new JSONObject();
        try {
            //Set message type, identitfy and send message
            jsonObject.put("mType", MESSAGE_TYPE_IDENTIFY);

            SOCKET_IDENTITY = "";
            if (accountManager.getCurrentAccount() != null)
                SOCKET_IDENTITY = accountManager.getCurrentAccount().getParseUser().getObjectId();
            jsonObject.put("clientId", SOCKET_IDENTITY);
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
        if (mSocket == null || !mSocket.isConnected()) {
            throw new WebSocketClosedException("WebSocket not connected!");
        }
    }

    /**
     * Exception for when the Websocket is closed
     */
    public class WebSocketClosedException extends WebSocketException {
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

            //Attempt a reconnect
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mMainServiceThread.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                connectSocket();
                            } catch (WebSocketException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, 10000);
        }

        @Override
        public void onTextMessage(String s) {
                try {
                    JSONObject j = new JSONObject(s);
                    switch (j.getInt("mType")) {
                        case MESSAGE_TYPE_SEND_CHAT:
                            //Decodes the message and forwards it to the listener
                            Message msg = new Message(j.getString("msg"));
                            msg.setChatroom(j.getString("chatroomId"));
                            msg.setSender(j.getString("senderId"));
                            if (j.getBoolean("tagged")) msg.setTag(new User());
                            if (mMessageListener != null) mMessageListener.onMessageRecieved(msg);
                            break;
                        case MESSAGE_TYPE_KEY_EXCHANGE:
                            byte[] key = Base64.decode(j.getString("key"), 0);
                            String chatroomId = j.getString("chatroomId");
                            //String userId = j.getString("userId");
                            try {
                                String p = "";
                                for (byte b : key) {
                                    p += Byte.toString(b) + ", ";
                                }
                                Log.d("Got Key", p);

                                KeyManager manager = new KeyManager(getApplicationContext(), AccountManager.getInstance().getCurrentAccount().getUsername());
                                manager.persistSymmetricKey(chatroomId, manager.rsaDecrypt(key));
                            } catch (KeyManager.KeyGenerationException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
