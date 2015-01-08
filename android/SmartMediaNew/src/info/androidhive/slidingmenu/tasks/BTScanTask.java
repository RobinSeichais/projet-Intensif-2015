package info.androidhive.slidingmenu.tasks;

import info.androidhive.slidingmenu.fragments.ConnectFragment;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class BTScanTask extends AsyncTask<String, Void, Void> {


    ProgressDialog dialog;
    Context mainContext; 
    BluetoothAdapter adapter; 
    private BroadcastReceiver blueToothReceiver;

    public BTScanTask(Context context) {
        mainContext = context; 
        dialog = new ProgressDialog(mainContext); 
        adapter = BluetoothAdapter.getDefaultAdapter();
        blueToothReceiver = null;
    }


    protected void onPreExecute() {
          dialog.setTitle("Please Wait");
          dialog.setMessage("Searching for devices..");
          dialog.setIndeterminate(true);
          dialog.setCancelable(false);
          dialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        adapter.startDiscovery();
        final Map<String, Short> devices = new HashMap<String, Short>();

        blueToothReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
                    if(!devices.containsKey(device.getName())) {
                        devices.put(device.getName(), rssi);	
                    }
                    Log.d("BTScanTask","Device founded : " + device.getName());
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                	if(!devices.isEmpty()) {
	                	Short lowestRSSI = Short.MIN_VALUE;
	                	String lowestDevice = "";
	                	for(Entry<String, Short> dev : devices.entrySet()) {
	                		Short devRSSI = dev.getValue();
	                		if(devRSSI > lowestRSSI) {
	                			lowestRSSI = devRSSI;
	                			lowestDevice = dev.getKey();
	                		}
	                	}
	                	ConnectFragment.btName = lowestDevice;
	                    dialog.dismiss();
	                	mainContext.unregisterReceiver(blueToothReceiver);
	                    Toast.makeText(mainContext, "Finished with the discovery! The closest device is " + lowestDevice, Toast.LENGTH_LONG).show();
                	}
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mainContext.registerReceiver(blueToothReceiver, filter);

        return null;
    }
}