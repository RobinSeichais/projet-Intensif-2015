package info.androidhive.slidingmenu.fragments;

import info.androidhive.slidingmenu.R;
import info.androidhive.slidingmenu.tasks.ContiniousScanTask;
import info.androidhive.slidingmenu.tasks.GetLibraryTask;
import info.androidhive.slidingmenu.tasks.PlayPauseTask;
import info.androidhive.slidingmenu.tasks.PlayTask;
import info.androidhive.slidingmenu.tasks.StopTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ListView;

public class MusicFragment extends Fragment {

	ListView tagslistview;
	ImageButton playBtn = null;
	ArrayList<String> listChansons = new ArrayList<String>();
	ArrayList<String> listArtistes = new ArrayList<String>();
	ArrayList<Integer> listId = new ArrayList<Integer>();
	boolean isPlaying = false;
	public static String btName = "";
	private ContiniousScanTask contScanTask;

	public MusicFragment(){}
	
	public void clickOnPauseStart() {
		if(isPlaying) {
			playBtn.setBackgroundResource(R.drawable.ic_play);
		} else {
			playBtn.setBackgroundResource(R.drawable.ic_pause);
		}
		isPlaying = !isPlaying;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_music, container, false);
		GetLibraryTask o = new GetLibraryTask();
		o.execute("http://192.168.43.143:50420",btName);
		Log.d("Music Fragment","Choosen Bluetooth Device name  : " + btName);
		try {
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
		tagslistview = (ListView) rootView.findViewById(R.id.taglistview);
		playBtn = (ImageButton) rootView.findViewById(R.id.play);
		final TextView songName = (TextView) rootView.findViewById(R.id.song);
		ImageButton stopBtn = (ImageButton) rootView.findViewById(R.id.stop);


		playBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Log.d("Music Fragment", "Clicking on start/pause");
				new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.143:50420");
				//new PlayPauseTask().execute("http://192.168.43.143:50420");
				clickOnPauseStart();
			}
		});

		stopBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Log.d("Music Fragment", "Clicking on stop");
				new StopTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.143:50420");
				//new StopTask().execute("http://192.168.43.143:50420");
				isPlaying = false;
				playBtn.setBackgroundResource(R.drawable.ic_play);
				contScanTask.stop();
			}
		});


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.tag_view_item, listChansons);
		tagslistview.setAdapter(adapter);
		tagslistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				isPlaying = true;
				playBtn.setBackgroundResource(R.drawable.ic_pause);
				songName.setText(listChansons.get(position));
				int idElt = listId.get(position);
				String artiste = listArtistes.get(position);
				new PlayTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.143:50420",artiste,""+idElt);
				//new PlayTask().execute("http://192.168.43.143:50420",artiste,""+idElt);
				contScanTask = new ContiniousScanTask(getActivity(), btName, MusicFragment.this);
				contScanTask.execute();
			}
		});
		return rootView;
	}

	public void extraireDonnees(JSONObject jsonResultat) {
		try {
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
