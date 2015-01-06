package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConnectFragment extends Fragment { 	
	View rootView;
	private BluetoothAdapter BTAdapter;
	
	public ConnectFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_connect, container, false);
		getActivity().registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		BTAdapter = BluetoothAdapter.getDefaultAdapter();

		Button boton = (Button) rootView.findViewById(R.id.connect);
		boton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				BTAdapter.startDiscovery();
				((MainActivity) getActivity()).detected = true;
			}
		});
		return rootView;
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			TextView rssi_msg = (TextView) rootView.findViewById(R.id.text);
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
