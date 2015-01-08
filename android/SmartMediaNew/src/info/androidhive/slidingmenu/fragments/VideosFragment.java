package info.androidhive.slidingmenu.fragments;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class VideosFragment extends Fragment {
	
	
	private ListView tagslistview;
	private ImageButton playBtn = null;
	private boolean isPlaying = false;
	private ContiniousScanTask contScanTask;
	
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
//		if(this.contScanTask != null) {
//			this.contScanTask.stop();
//		}
	}
	
	public VideosFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_videos, container, false);
         
        tagslistview = (ListView) rootView.findViewById(R.id.videoslistview);
		playBtn = (ImageButton) rootView.findViewById(R.id.play);
		final TextView videoName = (TextView) rootView.findViewById(R.id.video);
		ImageButton stopBtn = (ImageButton) rootView.findViewById(R.id.stop);


		playBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Log.d("Video Fragment", "Clicking on start/pause");
				new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.143:50420");
				//new PlayPauseTask().execute("http://192.168.43.143:50420");
				clickOnPauseStart();
			}
		});

		stopBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Log.d("Video Fragment", "Clicking on stop");
				new StopTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.143:50420");
				//new StopTask().execute("http://192.168.43.143:50420");
				isPlaying = false;
				playBtn.setBackgroundResource(R.drawable.ic_play);
				contScanTask.stop();
			}
		});


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.tag_view_item, ConnectFragment.videos.getListTitles());
		tagslistview.setAdapter(adapter);
		tagslistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				isPlaying = true;
				playBtn.setBackgroundResource(R.drawable.ic_pause);
				videoName.setText(ConnectFragment.videos.getListTitles().get(position));
				int idElt = ConnectFragment.videos.getListIds().get(position);
				//new PlayTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.143:50420",artiste,""+idElt);
				//new PlayTask().execute("http://192.168.43.143:50420",artiste,""+idElt);
				//contScanTask = new ContiniousScanTask(getActivity(), MusicFragment.this);
				//contScanTask.execute();
			}
		});
        
        return rootView;
    }
}
