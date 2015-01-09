package info.androidhive.slidingmenu.tasks;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCParams;

import android.os.AsyncTask;
import android.util.Log;

public class StopTask extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... urls) {
		Log.d("JSONRPC communication", "Entering in StopTask");
		String url = urls[0];

		JSONRPCClient client = JSONRPCClient.create(url,
				JSONRPCParams.Versions.VERSION_2);
		client.setConnectionTimeout(2000);
		client.setSoTimeout(2000);
		try {
			Object o = client.call("stop");
			Log.d("JSONRPC communication", "Stop call result : " + o);
		} catch (JSONRPCException e) {
			Log.e("JSONRPC communication", "Error in StopTask : " + e.getMessage());
		}

		return null;
	}

}