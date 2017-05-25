package com.intugine.coalindiaclientdemo;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by gaurav on 18/5/17.
 */

public class MyWebSocketListener extends WebSocketListener {
    public static final int CLOSURE_CODE = 1000;
    SocketDisconnectInterface disconnectInterface;

    public MyWebSocketListener(SocketDisconnectInterface disconnectInterface) {
        this.disconnectInterface = disconnectInterface;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        Log.d("Socket", "Opened connection");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Log.d("Socket", "Message recieved: " + text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        Log.d("Socket", "Closing connection");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        Log.d("Socket", "Closed connection");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        Log.d("Socket", "Exception occured");
        t.printStackTrace();
        Log.e("MywebSocketListener: ", t.toString());
        disconnectInterface.exceptionOccured();
    }
}
