package com.example.bluetoothtest;

import android.os.Bundle;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        Button boton = (Button) findViewById(R.id.connect);
        boton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                BTAdapter.startDiscovery();
            }
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
        	TextView rssi_msg = (TextView) findViewById(R.id.text);
        	rssi_msg.setText("");
        	
            String action = intent.getAction();
            Log.d("int",intent.toString());
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
            	
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                rssi_msg.setText(rssi_msg.getText() + name + " => " + rssi + "dBm, adresse : "+device.getAddress()+"\n");
                
                // Add the name and address to an array adapter to show in a ListView
                

            }
        }
    };
}