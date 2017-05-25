package com.intugine.ble.beacon.server;

import android.app.Activity;

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
 * Created by niraj on 20-05-2017.
 */

public class ClientHandler {
    private static final String TAG = makeLogTag(ClientHandler.class);

    private static ClientHandler sClientHandler;

    private OkHttpClient client = new OkHttpClient();

    private WebSocket webSocket;
    private Activity mContext;
    private String mWifiName;
    private String mDeviceName;
    private String mReceiverIp;

    private boolean isSending = false;
    private Disposable intervalDisposable;


    private WifiConnectionStateReciever mConnectionStateReciever;
    private SocketDisconnectInterface mSocketDisconnectInterface;

    private ServerClientHandlerListener mServerClientHandlerListener;

    public synchronized static ClientHandler with(Activity activity){
        //sBleScanHandler = null;
        if(activity !=null){
            if(sClientHandler==null){

                sClientHandler = new ClientHandler();

                boolean isInit = sClientHandler.init(activity);
                if(isInit){
                    return sClientHandler;
                }
            }else{
                if(sClientHandler.mContext != activity){
                    sClientHandler.changeContext(activity);
                }
                return sClientHandler;
            }
        }
        return null;
    }

    private boolean init(Activity activity) {
        mContext = activity;
        setupWifiCharacteristics();
        return true;
    }

    private void changeContext(Activity activity) {
        mContext = activity;
    }

    public boolean attachWifiConnectReceiver(WifiConnectionStateInterface wifiConnectionStateInterface){
        mConnectionStateReciever = new WifiConnectionStateReciever(wifiConnectionStateInterface);
        return true;
    }

    private void setupWifiCharacteristics(){
        mWifiName = SharedPreferenceHelper.getInstance(mContext).getWifiName();
        mDeviceName = SharedPreferenceHelper.getInstance(mContext).getDeviceName();
        mReceiverIp = SharedPreferenceHelper.getInstance(mContext).getRecieverIP();
    }

    public boolean connectSocket(final SocketDisconnectInterface socketDisconnectInterface) {
        if(mReceiverIp == null){
            setSending(false);
            return false;
        }
        this.mSocketDisconnectInterface =  socketDisconnectInterface;

        //Single.create((SingleOnSubscribe<String>))
        Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> e) throws Exception {
                Request request = new Request.Builder()
                        .url("ws://" + mReceiverIp + ":8080")
                        .build();
                MyWebSocketListener webSocketListener = new MyWebSocketListener(socketDisconnectInterface);
                webSocket = client.newWebSocket(request, webSocketListener);
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

    public void disconnectSocket() {
        try {
            intervalDisposable.dispose();
            webSocket.close(1000, "User manually stopped sending");
        } catch (Exception e) {
            LOGE(TAG, e.toString());
        }
    }

    public WifiConnectionStateReciever getConnectionStateReciever() {
        return mConnectionStateReciever;
    }

    public void setServerClientHandlerListener(ServerClientHandlerListener pServerClientHandlerListener) {
        this.mServerClientHandlerListener = pServerClientHandlerListener;
    }

    private void sendData() {
        intervalDisposable = Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        String data = "Device: " + mDeviceName+ ", " + "time: " + System.currentTimeMillis();
                        if(mServerClientHandlerListener!=null){
                            String message = mServerClientHandlerListener.onServerSendData();
                            webSocket.send(data+ " || message: " + message);
                        }
                    }
                });
    }

    public void setSending(boolean sending) {
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

    public boolean isSending() {
        return isSending;
    }

    public boolean toggleSending(){
        if (isSending) {
            setSending(false);
            disconnectSocket();
            return false;
        } else {
            setSending(true);
            connectSocket(mSocketDisconnectInterface);
            return true;
        }
    }

    public void dispose(){
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

    public void reconnectSocket() {

        setSending(true);
        disconnectSocket();
        connectSocket(mSocketDisconnectInterface);
    }


    public interface ServerClientHandlerListener{
        public String onServerSendData();
    }

}
