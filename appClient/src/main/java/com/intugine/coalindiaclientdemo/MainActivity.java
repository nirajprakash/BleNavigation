package com.intugine.coalindiaclientdemo;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity implements SocketDisconnectInterface, WifiConnectionStateInterface {

    //@BindView(R.id.text_wifi_name)
    TextView textWifiName;
    //@BindView(R.id.text_device_name)
    TextView textDeviceName;
    //@BindView(R.id.text_reciever_ip)
    TextView textRecieverIp;
    //@BindView(R.id.edit_wifi_name)
    TextInputEditText editWifiName;
    //@BindView(R.id.edit_device_name)
    TextInputEditText editDeviceName;
    //@BindView(R.id.edit_reciever_ip)
    TextInputEditText editRecieverIp;
    //@BindView(R.id.save_props_button)
    Button savePropsButton;
    //@BindView(R.id.enable_sending_button)
    Button sendingButton;
    //@BindView(R.id.text_sending_status)
    TextView textSendingStatus;
    private Random random;
    private OkHttpClient client = new OkHttpClient();
    private boolean isSending = false;
    private WebSocket webSocket;
    private int w, x, y, z;
    private Disposable intervalDisposable;
    private WifiConnectionStateReciever connectionStateReciever;
    private String wifi_name;
    private String device_name;
    private String reciever_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpViews();
    }

    private void setUpViews() {
        setUpWifiCharecterisitics();
        connectionStateReciever = new WifiConnectionStateReciever(this);
        random = new Random();
        sendingButton.setOnClickListener(v -> {
            if (isSending) {
                setSending(false);
                disconnectSocket();
            } else {
                setSending(true);
                connectSocket();
            }
        });
        savePropsButton.setOnClickListener(v -> {
            SharedPreferenceHelper.getInstance(this).saveCharecteristics(
                    editWifiName.getText().toString(),
                    editDeviceName.getText().toString(),
                    editRecieverIp.getText().toString()
            );
            setUpWifiCharecterisitics();
        });
    }

    private void setUpWifiCharecterisitics() {
        wifi_name = SharedPreferenceHelper.getInstance(this).getWifiName();
        device_name = SharedPreferenceHelper.getInstance(this).getDeviceName();
        reciever_ip = SharedPreferenceHelper.getInstance(this).getRecieverIP();
        textWifiName.setText("Wifi Name: " + wifi_name);
        textDeviceName.setText("Device Name: " + device_name);
        textRecieverIp.setText("Reciever Ip: " + reciever_ip);
        editWifiName.setText(wifi_name);
        editDeviceName.setText(device_name);
        editRecieverIp.setText(reciever_ip);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionStateReciever, filter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(connectionStateReciever);
        super.onStop();
    }

    @Override
    public void checkSocketConnection() {
        if (!isSending) {
            setSending(true);
            disconnectSocket();
            connectSocket();
        }
    }

    @Override
    public void exceptionOccured() {
        setSending(false);
        disconnectSocket();
    }

    private void connectSocket() {
        Single.create((SingleOnSubscribe<String>) emitter -> {
            Request request = new Request.Builder()
                    .url("ws://" + reciever_ip + ":8080")
                    .build();
            MyWebSocketListener webSocketListener = new MyWebSocketListener(this);
            webSocket = client.newWebSocket(request, webSocketListener);
            emitter.onSuccess("Socket Connected Successfully");
            sendData();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull String s) {
                        Log.d("Socket Status", s);
                        Log.d("Next task", "Starting to send data");
                        setSending(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        Log.d("Wifi", "Server not started yet");
                        setSending(false);
                    }
                })
        ;
    }

    private void disconnectSocket() {
        try {
            intervalDisposable.dispose();
            webSocket.close(1000, "User manually stopped sending");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData() {
        intervalDisposable = Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    w = random.nextInt(10);
                    x = random.nextInt(10);
                    y = random.nextInt(10);
                    z = random.nextInt(10);
                    String data =
                            "Device: " + device_name + ", " +
                                    "W: " + w + ", " +
                                    "X: " + x + ", " +
                                    "Y: " + y + ", " +
                                    "Z: " + z;
                    webSocket.send(data);
                });
    }

    @Override
    protected void onDestroy() {
        try {
            if (!intervalDisposable.isDisposed()) {
                intervalDisposable.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        webSocket.close(1000, "Activity destroying");
        setSending(false);
        super.onDestroy();
    }

    public void setSending(boolean sending) {
        isSending = sending;
        runOnUiThread(() -> {
            if (isSending) {
                textSendingStatus.setText("Sending data...");
            } else {
                textSendingStatus.setText("Not Sending data");
            }
        });
    }
}
