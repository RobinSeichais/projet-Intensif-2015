package info.androidhive.slidingmenu;

import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.fragments.ConnectFragment;
import info.androidhive.slidingmenu.fragments.MusicFragment;
import info.androidhive.slidingmenu.fragments.VideosFragment;
import info.androidhive.slidingmenu.model.NavDrawerItem;
import info.androidhive.slidingmenu.tasks.BTScanTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	public static final String RASPI_ADDRESS = "http://192.168.43.143:50420";
	
	public static Set<String> allowedDevices = new HashSet<String>();
	 
	static {
		allowedDevices.add("RobinS");
		//allowedDevices.add("REMI-PC");
		allowedDevices.add("SPEINFO-THINK2");
		//allowedDevices.add("Walid");
	}
	
	public static DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	public boolean detected = false;
	public boolean connected = false;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();
		
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));


		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.app_name,
				R.string.app_name
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			displayView(0);
		}

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if(!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			BluetoothAdapter.getDefaultAdapter().enable();
		}
		
		new BTScanTask(this).execute();
		
		if (!mWifi.isConnected()) {
			AlertDialog.Builder pd = new AlertDialog.Builder(this);
			pd.setTitle("WiFi");
			pd.setMessage("You must be connected to a WiFi Network");
			pd.setCancelable(false);
			pd.setPositiveButton("Quit",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
					finish();
				}
			});
			AlertDialog alertDialog = pd.create();
			alertDialog.show();
		}
	}
	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(connected){
			if (mDrawerToggle.onOptionsItemSelected(item)) 
				return true;
		}
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void displayView(int position) {
		Fragment fragment = null;
		
		if(!connected){
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			fragment = new ConnectFragment();
		}
		else {
			switch (position) {
			case 0:
				fragment = new MusicFragment();
				break;
			case 1:
				fragment = new VideosFragment();
				break;
			default:
				break;
			}
		}


		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment).commit();

			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
