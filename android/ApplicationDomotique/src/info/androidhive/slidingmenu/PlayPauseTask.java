package info.androidhive.slidingmenu;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParamsType;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class PlayPauseTask extends AsyncTask<String, Void, JSONRPC2Response> {

	@Override
	protected JSONRPC2Response doInBackground(String... urls) {
		Log.d("response PLAYPAUSE", "Entering");
		URL serverURL = null;
		String url = urls[0];
		try {
			serverURL = new URL(url);
		} catch (MalformedURLException e) {
			Log.e("erreur", e.getMessage());
		}

		JSONRPCClient client = JSONRPCClient.create(url,
				JSONRPCParams.Versions.VERSION_2);
		client.setConnectionTimeout(2000);
		client.setSoTimeout(2000);
		try {
			Object o = client.call("playpause");
			Log.d("response PLAYPAUSE", "O : " + o);
		} catch (JSONRPCException e) {
			Log.e("response PLAYPAUSE", e.getMessage());
		}

		return null;
	}

}