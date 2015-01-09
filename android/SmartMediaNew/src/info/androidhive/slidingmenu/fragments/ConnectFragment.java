package info.androidhive.slidingmenu.fragments;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.MainActivity;
import info.androidhive.slidingmenu.R;
import info.androidhive.slidingmenu.model.Audios;
import info.androidhive.slidingmenu.model.Videos;
import info.androidhive.slidingmenu.tasks.GetMedia;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ConnectFragment extends Fragment { 	
	View rootView;
	String scanList;
	
	public static Audios audios = null;
	public static Videos videos = null;
	
	public static String btName = "";

	public ConnectFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_connect, container, false);
		
		Button boton = (Button) rootView.findViewById(R.id.connect);
		boton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				((MainActivity) getActivity()).connected = true;
				
				try {
					GetMedia o = new GetMedia();
					o.execute(MainActivity.RASPI_ADDRESS,btName);
					Log.d("Music Fragment","Choosen Bluetooth Device name  : " + btName);
					JSONObject donnees = o.get();
					if(donnees == null){
						AlertDialog.Builder pd = new AlertDialog.Builder(getActivity());
						pd.setTitle("No Connections Found");
						pd.setMessage("Unable to Connect to SmartMedia");
						pd.setCancelable(false);
						pd.setPositiveButton("Quit",
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								getActivity().finish();
							}
						});
						AlertDialog alertDialog = pd.create();
						alertDialog.show();
					}
					else{
						extraireDonnees(donnees);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();

				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				
				MainActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				MainActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, new MusicFragment()).commit();

			}
		});
		return rootView;
	}
	
	

	public void extraireDonnees(JSONObject jsonResultat) {
		try {

			ArrayList<String> listChansons = new ArrayList<String>();
			ArrayList<String> listArtistes = new ArrayList<String>();
			ArrayList<Integer> listId = new ArrayList<Integer>();
			
			ArrayList<String> listVideoTitles = new ArrayList<String>();
			ArrayList<Integer> listVideoIds = new ArrayList<Integer>();
			
			JSONArray audio = (JSONArray) jsonResultat.getJSONArray("audio");
			for (int i = 0; i < audio.length(); i++) {
				JSONObject chanson = audio.getJSONObject(i);
				String titre = chanson.getString("title");
				String artist = chanson.getString("artist");
				int id = chanson.getInt("id");
				listChansons.add(titre);
				listArtistes.add(artist);
				listId.add(id);
			}
			
			audios = new Audios(listChansons, listArtistes, listId);

//			JSONArray videoJSONObj = (JSONArray) jsonResultat.getJSONArray("video");
//			for (int i = 0; i < videoJSONObj.length(); i++) {
//				JSONObject video = videoJSONObj.getJSONObject(i);
//				String titre = video.getString("title");
//				int id = video.getInt("id");
//				listVideoTitles.add(titre);
//				listVideoIds.add(id);
//			}
//			
//			videos = new Videos(listVideoTitles, listVideoIds);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
