package info.androidhive.slidingmenu;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class MusicFragment extends Fragment {
	
	ListView tagslistview;
	ArrayList<String> listChansons = new ArrayList<String>();
	
	public MusicFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);
        String donnees = "{\"id\" : 1,\"result\" : [{\"titre\" : \"titre1\", \"id\" : 1},{\"titre\" : \"titre2\", \"id\" : 2}],\"jsonrpc\" : \"2.0\"}";
		String reponse = getDonnees(donnees);
		extraireDonnees(reponse);
		tagslistview = (ListView) rootView.findViewById(R.id.taglistview);
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// R.layout.tag_view_item, mapCommande);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.tag_view_item, listChansons);
		tagslistview.setAdapter(adapter);
		tagslistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// Toast.makeText(getApplication(),
				// "Hello " + listChansons.get(position),
				// Toast.LENGTH_LONG).show();
				//new HttpGetTask().execute("http://192.168.137.1:50420?a=1");
			}
		});
        return rootView;
    }
	
	public void extraireDonnees(String reponse) {
		try {
			JSONObject jsonResultat = new JSONObject(reponse);
			JSONArray chansons = (JSONArray) jsonResultat.get("result");
			for (int i = 0; i < chansons.length(); i++) {
				JSONObject chanson = chansons.getJSONObject(i);
				String titre = chanson.getString("titre");
				int id = chanson.getInt("id");
				listChansons.add(titre);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getDonnees(String response) {
		JSONRPC2Response respIn = null;
		try {
			respIn = JSONRPC2Response.parse(response);
		} catch (JSONRPC2ParseException e) {
			e.printStackTrace();
		}
		return respIn.toString();
	}
	
	public class HttpGetTask extends AsyncTask<String, Void, JSONRPC2Response> {

		@Override
		protected JSONRPC2Response doInBackground(String... urls) {
			URL serverURL = null;
			String url = urls[0];
			try {
				serverURL = new URL(url);
			} catch (MalformedURLException e) {
				Log.e("erreur", e.getMessage());
			}
			JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
			String method = "getServerTime";
			int requestID = 0;
			JSONRPC2Request request = new JSONRPC2Request(method, requestID);
			JSONRPC2Response response = null;
			try {
				// response = mySession.send(request);
				mySession.send(request);
			} catch (JSONRPC2SessionException e) {
				Log.e("erreur", e.getMessage());
			}
			// if (response.indicatesSuccess())
			// Log.d("response", response.getResult().toString());
			// else
			// Log.d("response", response.getError().getMessage());
			return response;
		}

	}
}
