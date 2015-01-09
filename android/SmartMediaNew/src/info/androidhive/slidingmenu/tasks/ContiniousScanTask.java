package info.androidhive.slidingmenu.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import info.androidhive.slidingmenu.MainActivity;
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
    
    private boolean btScanDone = true;
    
    private BroadcastReceiver blueToothReceiver = new BroadcastReceiver() {
		
		private short firstPower = Short.MIN_VALUE;
		
		private short powerToSave = Short.MIN_VALUE;
		
		private PlayMode currentPlayMode = PlayMode.WAIT_TO_PAUSE;
		
		private Map<String, Short> devices = new HashMap<String, Short>();
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {				
				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
				String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
				
				if(name != null && MainActivity.allowedDevices.contains(name) && !devices.containsKey(name)) {
					devices.put(name, rssi);
					Log.d("Continious Scan", "Found allowed device : " + name + ", with power : " + rssi);
				}				

//				Log.d("response SM", "DEVICE NAME = " + name);
//				Log.d("response SM", "DEFAULT DEVICE NAME = " + ConnectFragment.btName);
//				if(name != null && name.equals(ConnectFragment.btName)) {
//
//					Log.d("response SM", "WALID FOUNDED");
//					Log.d("response POWER", "NEW POWER : " + rssi);
//					
//					if(currentPlayMode == PlayMode.WAIT_TO_PAUSE) {
//						Log.d("response SM", "Checking for pause");
//						if(firstPower != Short.MIN_VALUE) {
//							if((short)(firstPower - rssi) > 10) {
//								Log.d("response SM", "Pausing");
//								tick = true;
//								currentPlayMode = PlayMode.WAIT_TO_START;
//								powerToSave = rssi;
//							}
//						} else {
//							firstPower = rssi;
//							Log.d("response POWER", "REFERENCE POWER : " + firstPower);
//						}
//					} else {
//						Log.d("response SM", "Checking for start");
//						if(rssi > (short)(powerToSave + 10)) {
//							Log.d("response SM", "Playing");
//							tick = true;
//							currentPlayMode = PlayMode.WAIT_TO_PAUSE;
//							powerToSave = Short.MIN_VALUE;
//						}
//					}
//				}

			} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				if(!devices.isEmpty()) {
					if(devices.size() != 1) {
	                	Short higherRSSI = Short.MIN_VALUE;
	                	String closestDevice = "";
	                	for(Entry<String, Short> dev : devices.entrySet()) {
	                		Short devRSSI = dev.getValue();
	                		if(devRSSI > higherRSSI) {
	                			higherRSSI = devRSSI;
	                			closestDevice = dev.getKey();
	                		}
	                	}
	                	
	                	if(!closestDevice.equals(ConnectFragment.btName)) {
	                		Log.d("Continious Scan", "Switching to : " + closestDevice);
	                		new SwitchToTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS, closestDevice);
	                    	ConnectFragment.btName = closestDevice;
	                		reset();
	                	} else {
	                		if(musicFragment.isPaused()) {
	                			musicFragment.clickOnPauseStart();
	                    		reset();
	                		}
	                	}
					} else {
						String closestDevice = devices.entrySet().iterator().next().getKey();
						Short highestRSSI = devices.entrySet().iterator().next().getValue();
						if(devices.get(ConnectFragment.btName) == null) {
	                		Log.d("Continious Scan", "Switching to : " + closestDevice);
							new SwitchToTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS, closestDevice);
	                    	ConnectFragment.btName = closestDevice;
	                		reset();
						} else {
							if(currentPlayMode == PlayMode.WAIT_TO_PAUSE) {
								Log.d("response SM", "Checking for pause");
								if(firstPower != Short.MIN_VALUE) {
									if((short)(firstPower - highestRSSI) > 10) {
										Log.d("response SM", "Pausing");
										musicFragment.clickOnPauseStart();
										new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS);
										currentPlayMode = PlayMode.WAIT_TO_START;
										powerToSave = highestRSSI;
									}
								} else {
									firstPower = highestRSSI;
									Log.d("response POWER", "REFERENCE POWER : " + firstPower);
								}
							} else {
								Log.d("response SM", "Checking for start");
								if(highestRSSI > (short)(powerToSave + 10)) {
									Log.d("response SM", "Playing");
									musicFragment.clickOnPauseStart();
									new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS);
									currentPlayMode = PlayMode.WAIT_TO_PAUSE;
									powerToSave = Short.MIN_VALUE;
								}
							}
						}
					}
            	} else {
            		musicFragment.clickOnPauseStart();
            		reset();
            	}
				
				btScanDone = true;
				devices.clear();
			}
		}
		
		private void reset() {   		
			firstPower = Short.MIN_VALUE;            		
			powerToSave = Short.MIN_VALUE;            		
			currentPlayMode = PlayMode.WAIT_TO_PAUSE;
		}
	};
	
	
	public void stop() {
		try {
	        mainContext.unregisterReceiver(blueToothReceiver);
		} catch(IllegalArgumentException e) {
			Log.e("Continious Scan","Receiver not registred");
		}
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
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mainContext.registerReceiver(blueToothReceiver, filter);


        Log.d("Continious Scan", "Entering in doInBackground");
		while (continu) {

			Log.d("Continious Scan", "ENTERING IN WHILE");
			
			try {
				if( btScanDone) {
					adapter.startDiscovery();
					
				
					if(tick) {
						Log.d("Continious Scan", "CALLING PLAY-PAUSE");
	//					new PlayPauseTask().execute(MainActivity.RASPI_ADDRESS);
						musicFragment.clickOnPauseStart();
						new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS);
						Log.d("Continious Scan", "PLAY-PAUSE CALLED");
						tick = false;
						
					}
	
					btScanDone = false;
				}
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Log.e("Continious Scan", "Error during continious scan");
			}
			
		}

		return null;
    }
}