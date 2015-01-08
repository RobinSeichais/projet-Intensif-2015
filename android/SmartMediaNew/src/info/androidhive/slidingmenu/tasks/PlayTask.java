package info.androidhive.slidingmenu.tasks;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCParams;

import android.os.AsyncTask;
import android.util.Log;

public class PlayTask extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... urls) {
		Log.d("JSONRPC communication", "Entering in PlayTask");
		String url = urls[0];
		String artiste = urls[1];
		int id = Integer.parseInt(urls[2]);

		JSONRPCClient client = JSONRPCClient.create(url,
				JSONRPCParams.Versions.VERSION_2);
		client.setConnectionTimeout(2000);
		client.setSoTimeout(2000);
		try {
			Object o = client.call("play",artiste,id);
			Log.d("JSONRPC communication", "Play command result : " + o);
			return null;
		} catch (JSONRPCException e) {
			Log.e("JSONRPC communication", "Error in PlayTask : " + e.getMessage());
		}

		return null;
	}

}