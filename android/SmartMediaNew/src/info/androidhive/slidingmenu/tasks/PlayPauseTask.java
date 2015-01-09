package info.androidhive.slidingmenu.tasks;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCParams;

import android.os.AsyncTask;
import android.util.Log;

public class PlayPauseTask extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... urls) {
		Log.d("JSONRPC communication", "Entering in PlayPauseTask");
		String url = urls[0];

		JSONRPCClient client = JSONRPCClient.create(url,
				JSONRPCParams.Versions.VERSION_2);
		client.setConnectionTimeout(2000);
		client.setSoTimeout(2000);
		try {
			Object o = client.call("playpause");
			Log.d("JSONRPC communication", "Result of playpause : " + o);
		} catch (JSONRPCException e) {
			Log.e("JSONRPC communication", "Error in PlayPauseTask : " + e.getMessage());
		}

		return null;
	}

}