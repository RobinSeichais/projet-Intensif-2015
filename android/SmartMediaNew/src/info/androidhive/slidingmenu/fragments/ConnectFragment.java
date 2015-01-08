package info.androidhive.slidingmenu.fragments;

import info.androidhive.slidingmenu.MainActivity;
import info.androidhive.slidingmenu.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ConnectFragment extends Fragment { 	
	View rootView;
	String scanList;

	public ConnectFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_connect, container, false);
		
		Button boton = (Button) rootView.findViewById(R.id.connect);
		boton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				((MainActivity) getActivity()).connected = true;
				MainActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				MainActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, new MusicFragment()).commit();

			}
		});
		return rootView;
	}
}
