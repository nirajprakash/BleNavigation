package com.intugine.ble.beacon.server;

import android.app.Activity;
import android.content.Context;

import com.intugine.coalindiaclientdemo.MyWebSocketListener;
import com.intugine.coalindiaclientdemo.SharedPreferenceHelper;
import com.intugine.coalindiaclientdemo.SocketDisconnectInterface;
import com.intugine.coalindiaclientdemo.WifiConnectionStateInterface;
import com.intugine.coalindiaclientdemo.WifiConnectionStateReciever;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import static com.intugine.ble.beacon.util.LogUtils.LOGE;
import static com.intugine.ble.beacon.util.LogUtils.LOGI;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 21-05-2017.
 */

public class WebSocketClientHandler implements SocketDisconnectInterface, WifiConnectionStateInterface {


    //private Web

    private static final String TAG = makeLogTag(WebSocketClientHandler.class);


    //private static WeakReference<WebSocketClientHandler> sClientHandlerReference;
    private static WebSocketClientHandler sClientHandler;

    private OkHttpClient client = new OkHttpClient();

    private WebSocket webSocket;
    private String mWifiName;
    private String mDeviceName;
    private String mReceiverIp;

    private boolean isSending = false;
    private Disposable intervalDisposable;


    private WifiConnectionStateReciever mConnectionStateReciever;
    //private SocketDisconnectInterface mSocketDisconnectInterface;

    private WebSocketClientHandler.ServerClientHandlerListener mServerClientHandlerListener;

    public synchronized static WebSocketClientHandler with(Activity activity){
        //sBleScanHandler = null;
        if(activity !=null){
            WebSocketClientHandler clientHandler = null;// = sClientHandlerReference
            /*if(sClientHandlerReference != null){
                clientHandler = sClientHandlerReference.get();
            }*/
            if(sClientHandler==null){
                sClientHandler = new WebSocketClientHandler();
                boolean isInit = sClientHandler.init(activity);
                if(isInit){
                    LOGI(TAG, "webSocketClientHandler Init");
                    //sClientHandlerReference = new WeakReference<>(clientHandler);
                    return sClientHandler;
                }
            }else{
               /* if(clientHandler.mContext != activity){
                    clientHandler.changeContext(activity);
                }*/
                return sClientHandler;
            }
        }
        return null;
    }

    public void setServerClientHandlerListener(ServerClientHandlerListener pServerClientHandlerListener) {
        this.mServerClientHandlerListener = pServerClientHandlerListener;
    }


    public static void disposeSocket(){
       /* if(sClientHandlerReference==null){
            return;
        }
        WebSocketClientHandler webSocketClientHandler =  sClientHandlerReference.get();*/
        if(sClientHandler==null){
            return;
        }
        sClientHandler.dispose();
    }


    public static void restartSocket(Context context) {
        /*if(sClientHandlerReference==null){
            return;
        }
        WebSocketClientHandler webSocketClientHandler =  sClientHandlerReference.get();
        */
        if(sClientHandler==null){
            return;
        }
        sClientHandler.reconnectSocket(context);
    }

    public static WifiConnectionStateReciever getConnectionStateReciever() {
        LOGI(TAG, "getConnectionStateReciever");

        /*if(sClientHandlerReference==null) {
            return null;
        }

        */
            LOGI(TAG, "getConnectionStateReciever halder not null");
            //WebSocketClientHandler clientHandler = sClientHandlerReference.get();
            if(sClientHandler!=null){

                LOGI(TAG, "getConnectionStateReciever clientHandler not null");
                return sClientHandler.mConnectionStateReciever;
            }

        return null;
    }

    private boolean init(Activity activity) {
        //mContext = activity;
        setupWifiCharacteristics(activity);
        attachWifiConnectReceiver();
        return true;
    }

    /*private void changeContext(Activity activity) {
        mContext = activity;
    }
*/
    private void setupWifiCharacteristics(Context context){
        mWifiName = SharedPreferenceHelper.getInstance(context).getWifiName();
        mDeviceName = SharedPreferenceHelper.getInstance(context).getDeviceName();
        mReceiverIp = SharedPreferenceHelper.getInstance(context).getRecieverIP();
    }

    private boolean attachWifiConnectReceiver(){
        LOGI(TAG, "attachWifiConnectReceiver");
        mConnectionStateReciever = new WifiConnectionStateReciever(this);
        return true;
    }

    private boolean connectSocket() {
        LOGI(TAG , "connect websocket");
        if(mReceiverIp == null){
            setSending(false);
            return false;
        }
        LOGI(TAG , "connect websocket works");
        //this.mSocketDisconnectInterface =  socketDisconnectInterface;
        //Single.create((SingleOnSubscribe<String>))
        Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> e) throws Exception {
                Request request = new Request.Builder()
                        .url("ws://" + mReceiverIp + ":8080")
                        .build();
                MyWebSocketListener webSocketListener = new MyWebSocketListener(WebSocketClientHandler.this);
                webSocket = client.newWebSocket(request, webSocketListener);
                LOGI(TAG , "websocket init");
                e.onSuccess("Socket Connected Successfully");
                sendData();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull String s) {
                        LOGI(TAG,"Socket Status: "+ s);
                        LOGI(TAG,"Next task: Starting to send data");
                        setSending(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        LOGI(TAG,"Wifi: Server not started yet");
                        setSending(false);
                    }
                });

        return true;
    }

    private void disconnectSocket() {
        try {
            intervalDisposable.dispose();
            webSocket.close(1000, "User manually stopped sending");
        } catch (Exception e) {
            LOGE(TAG, e.toString());
        }
    }





    private void sendData() {
        intervalDisposable = Observable.interval(70, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        String data = mDeviceName+ "|";// + "time: " + System.currentTimeMillis();
                        if(mServerClientHandlerListener!=null){
                            String message = mServerClientHandlerListener.onServerSendData();
                            webSocket.send(data + message);
                        }
                    }
                });
    }

    private void setSending(boolean sending) {
        isSending = sending;
        /*
        runOnUiThread(() -> {
            if (isSending) {
                textSendingStatus.setText("Sending data...");
            } else {
                textSendingStatus.setText("Not Sending data");
            }
        });
        */
    }

    private boolean isSending() {
        return isSending;
    }

    public boolean toggleSending(){
        if (isSending) {
            setSending(false);
            disconnectSocket();
            return false;
        } else {
            setSending(true);
            connectSocket();
            return true;
        }
    }

    private void dispose(){
        if(webSocket == null){
            return;
        }
        try {
            if (!intervalDisposable.isDisposed()) {
                intervalDisposable.dispose();
            }
        } catch (Exception e) {
            LOGE(TAG, e.toString());
        }

        webSocket.close(1000, "Activity destroying");
        setSending(false);
    }


    private void reconnectSocket(Context context){

        LOGI(TAG, "reconnetSocket");

        setSending(true);
        disconnectSocket();
        setupWifiCharacteristics(context);
        connectSocket();

    }

    @Override
    public void exceptionOccured() {

        LOGI(TAG, "exception occur");
        setSending(false);
        disconnectSocket();
        reconnectSocket();

    }

    private void reconnectSocket() {
        setSending(true);
        connectSocket();
    }

    @Override
    public void checkSocketConnection() {
        LOGI(TAG, "check socket Connection");
        if (!isSending()) {

            setSending(true);
            disconnectSocket();
            connectSocket();
        }
    }


    public interface ServerClientHandlerListener{
        public String onServerSendData();
    }

}
