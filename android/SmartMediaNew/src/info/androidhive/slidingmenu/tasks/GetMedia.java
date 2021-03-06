package info.androidhive.slidingmenu.tasks;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCParams;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetMedia extends AsyncTask<String, Void, JSONObject> {

	@Override
	protected JSONObject doInBackground(String... urls) {
		Log.d("JSONRPC communication", "Entering in GetMedia");
		String url = urls[0];
		String btName = urls[1];
		JSONRPCClient client = JSONRPCClient.create(url,
				JSONRPCParams.Versions.VERSION_2);
		client.setConnectionTimeout(2000);
		client.setSoTimeout(2000);
		try {
			JSONObject o = client.callJSONObject("getaudio",btName);
			Log.d("JSONRPC communication", "Result of GetMedia : " + o);
			return o;
		} catch (JSONRPCException e) {
			Log.e("JSONRPC communication", "Error in GetMedia : " + e.getMessage());
		}

		return null;
	}

}