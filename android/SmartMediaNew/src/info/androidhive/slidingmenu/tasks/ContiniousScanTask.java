package info.androidhive.slidingmenu.tasks;

import info.androidhive.slidingmenu.fragments.ConnectFragment;
import info.androidhive.slidingmenu.fragments.MusicFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

public class ContiniousScanTask extends AsyncTask<String, Void, Void> {
	
	private enum PlayMode {
		
		WAIT_TO_START,
		WAIT_TO_PAUSE;
	}

	private boolean tick;
    private Context mainContext; 
	private MusicFragment musicFragment;
    private BluetoothAdapter adapter;

    private boolean continu = true;
    
    final BroadcastReceiver blueToothReceiver = new BroadcastReceiver() {
		
		private short firstPower = Short.MIN_VALUE;
		
		private short powerToSave = Short.MIN_VALUE;
		
		private PlayMode currentPlayMode = PlayMode.WAIT_TO_PAUSE;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
				String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

				Log.d("response SM", "DEVICE NAME = " + name);
				Log.d("response SM", "DEFAULT DEVICE NAME = " + ConnectFragment.btName);
				if(name != null && name.equals(ConnectFragment.btName)) {

					Log.d("response SM", "WALID FOUNDED");
					Log.d("response POWER", "NEW POWER : " + rssi);
					
					if(currentPlayMode == PlayMode.WAIT_TO_PAUSE) {
						Log.d("response SM", "Checking for pause");
						if(firstPower != Short.MIN_VALUE) {
							if((short)(firstPower - rssi) > 10) {
								Log.d("response SM", "Pausing");
								tick = true;
								currentPlayMode = PlayMode.WAIT_TO_START;
								powerToSave = rssi;
							}
						} else {
							firstPower = rssi;
							Log.d("response POWER", "REFERENCE POWER : " + firstPower);
						}
					} else {
						Log.d("response SM", "Checking for start");
						if(rssi > (short)(powerToSave + 10)) {
							Log.d("response SM", "Playing");
							tick = true;
							currentPlayMode = PlayMode.WAIT_TO_PAUSE;
							powerToSave = Short.MIN_VALUE;
						}
					}
				}

			}
		}
	};
	
	public void stop() {
		this.continu = false;
	}

    public ContiniousScanTask(Context context, MusicFragment musicFragment) {
    	this.musicFragment = musicFragment;
        mainContext = context; 
        adapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("Continious Scan", "Entering in constructor");
    }

    @Override
    protected Void doInBackground(String... params) {
    	

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mainContext.registerReceiver(blueToothReceiver, filter);


        Log.d("Continious Scan", "Entering in doInBackground");
		while (continu) {

			Log.d("Continious Scan", "ENTERING IN WHILE");
			try {
				adapter.startDiscovery();
				Thread.sleep(10000);
				
				if(tick) {
					Log.d("Continious Scan", "CALLING PLAY-PAUSE");
//					new PlayPauseTask().execute("http://192.168.43.143:50420");
					musicFragment.clickOnPauseStart();
					new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.143:50420");
					Log.d("Continious Scan", "PLAY-PAUSE CALLED");
					tick = false;
				}
				
			} catch (InterruptedException e) {
				Log.e("Continious Scan", "Error during continious scan");
			}
		}

		return null;
    }

     protected void onDestory() {
          mainContext.unregisterReceiver(blueToothReceiver);
     }
}