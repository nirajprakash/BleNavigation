package com.intugine.coalindiaserverdemo;

import android.util.Log;

import java.io.IOException;

import fi.iki.elonen.NanoWSD;

/**
 * Created by gaurav on 18/5/17.
 */

public class MyWsServer extends NanoWSD {
    private DataInterface dataInterface;
    public MyWsServer(int port) {
        super(port);
    }
    public MyWsServer(String hostname, int port,DataInterface dataInterface) {
        super(hostname, port);
        this.dataInterface = dataInterface;
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new MyWebSocket(this, handshake);
    }

    private class MyWebSocket extends WebSocket {
        MyWsServer myWsServer;

        public MyWebSocket(MyWsServer server, IHTTPSession handshakeRequest) {
            super(handshakeRequest);
            myWsServer = server;
        }

        @Override
        protected void onOpen() {

        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {

        }

        @Override
        protected void onMessage(WebSocketFrame message) {
            String s = message.getTextPayload();
            Log.d("Message Recieved", s);
            dataInterface.showData(s);
        }

        @Override
        protected void onPong(WebSocketFrame pong) {

        }

        @Override
        protected void onException(IOException exception) {
            exception.printStackTrace();
        }
    }
}
