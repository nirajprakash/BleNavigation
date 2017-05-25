package com.intugine.coalindiaserverdemo;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DataInterface {

    //@BindView(R.id.text_data)
    TextView textData;
    private MyWsServer wsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        wsServer = new MyWsServer(getIP(), 8080, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            wsServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            wsServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void showData(String data) {
        runOnUiThread(() -> textData.setText(data));
    }

    private String getIP() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.d("hostname", ip);
        return ip;
    }
}
