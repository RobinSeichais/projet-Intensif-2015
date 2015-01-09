package info.androidhive.slidingmenu.fragments;

import info.androidhive.slidingmenu.MainActivity;
import info.androidhive.slidingmenu.R;
import info.androidhive.slidingmenu.tasks.ContiniousScanTask;
import info.androidhive.slidingmenu.tasks.PlayPauseTask;
import info.androidhive.slidingmenu.tasks.PlayTask;
import info.androidhive.slidingmenu.tasks.StopTask;
import android.app.Fragment;
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
import android.widget.Toast;

public class MusicFragment extends Fragment {

	private ListView tagslistview;
	private ImageButton playBtn = null;
	private boolean isPlaying = false;
	private ContiniousScanTask contScanTask;
	
	private boolean elementSelected = false;

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
	public void onDestroyView() {
		super.onDestroyView();
		if(this.contScanTask != null) {
			this.contScanTask.stop();
		}
	}
	
	public boolean isPaused() {
		return !this.isPlaying;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_music, container, false);
		tagslistview = (ListView) rootView.findViewById(R.id.taglistview);
		playBtn = (ImageButton) rootView.findViewById(R.id.play);
		final TextView songName = (TextView) rootView.findViewById(R.id.song);
		ImageButton stopBtn = (ImageButton) rootView.findViewById(R.id.stop);


		playBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(!elementSelected) {
					Toast.makeText(getActivity(), "Please select an element", Toast.LENGTH_SHORT).show();
					return;
				}
				Log.d("Music Fragment", "Clicking on start/pause");
				new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS);
				//new PlayPauseTask().execute(MainActivity.RASPI_ADDRESS);
				clickOnPauseStart();
			}
		});

		stopBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(!elementSelected) {
					Toast.makeText(getActivity(), "Please select an element", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Log.d("Music Fragment", "Clicking on stop");
				new StopTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS);
				//new StopTask().execute(MainActivity.RASPI_ADDRESS);
				isPlaying = false;
				elementSelected = false;
				songName.setText("");
				playBtn.setBackgroundResource(R.drawable.ic_play);
				contScanTask.stop();
			}
		});


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.tag_view_item, ConnectFragment.audios.getListChansons());
		tagslistview.setAdapter(adapter);
		tagslistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				elementSelected = true;
				isPlaying = true;
				playBtn.setBackgroundResource(R.drawable.ic_pause);
				songName.setText(ConnectFragment.audios.getListChansons().get(position));
				int idElt = ConnectFragment.audios.getListId().get(position);
				String artiste = ConnectFragment.audios.getListArtistes().get(position);
				new PlayTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS,artiste,""+idElt);
				//new PlayTask().execute(MainActivity.RASPI_ADDRESS,artiste,""+idElt);
				contScanTask = new ContiniousScanTask(getActivity(), MusicFragment.this);
				contScanTask.execute();
			}
		});
		return rootView;
	}
}
