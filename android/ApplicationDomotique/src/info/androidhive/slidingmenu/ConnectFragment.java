package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ConnectFragment extends Fragment {
	
	private enum PlayMode {
		
		WAIT_TO_START,
		WAIT_TO_PAUSE;
	}
	
	private static final String BT_NAME = "Mehdi";
	
	private boolean tick = false;
	
	View rootView;
	private BluetoothAdapter BTAdapter;
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		
		private short firstPower = Short.MIN_VALUE;
		
		private short powerToSave = Short.MIN_VALUE;
		
		private PlayMode currentPlayMode = PlayMode.WAIT_TO_PAUSE;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("int", intent.toString());
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
				String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

				Log.d("response SM", "DEVICE NAME = " + name);
				Log.d("response SM", "DEFAULT DEVICE NAME = " + BT_NAME);
				if(name != null && name.equals(BT_NAME)) {

					Log.d("response SM", "WALID FOUNDED");
					//Log.d("response POWER", "LAST POWER : " + lastPower);
					Log.d("response POWER", "NEW POWER : " + rssi);
					
					if(currentPlayMode == PlayMode.WAIT_TO_PAUSE) {
						Log.d("response SM", "Checking for pause");
						if(firstPower != Short.MIN_VALUE) {
							if((short)(firstPower - rssi) > 15) {
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
						if(rssi > (short)(powerToSave + 15)) {
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

	public ConnectFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater
				.inflate(R.layout.fragment_connect, container, false);
		getActivity().registerReceiver(receiver,
				new IntentFilter(BluetoothDevice.ACTION_FOUND));
		BTAdapter = BluetoothAdapter.getDefaultAdapter();

		Button boton = (Button) rootView.findViewById(R.id.connect);
		boton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new PlayPauseTask().execute("http://192.168.43.143:50420");
				//new WaitForPause().execute();
			}
		});
		return rootView;
	}

	private class WaitForPause extends AsyncTask<String, Void, Void> {

		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		protected Void doInBackground(String... urls) {

			boolean continu = true;

			while (continu) {

				Log.d("response WHILE", "ENTERING IN WHILE");
				try {
					BTAdapter.startDiscovery();
					Thread.sleep(1500);
					
					if(tick) {
						//continu = false;
						Log.d("response WHILE", "CALLING PLAY-PAUSE");
//						new PlayPauseTask().execute("http://192.168.43.143:50420");
						new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.143:50420");
						Log.d("response WHILE", "PLAY-PAUSE CALLED");
						tick = false;
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}
	}
}
