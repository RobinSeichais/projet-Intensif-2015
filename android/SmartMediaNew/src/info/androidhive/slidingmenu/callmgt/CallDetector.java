package info.androidhive.slidingmenu.callmgt;

import info.androidhive.slidingmenu.MainActivity;
import info.androidhive.slidingmenu.fragments.MusicFragment;
import info.androidhive.slidingmenu.tasks.PlayPauseTask;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallDetector extends PhoneStateListener {

	private boolean callFinished = false;

	public void onCallStateChanged(int state, String incomingNumber) {

		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d("Call Detector", "IDLE");
			if(!callFinished){
				new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS);
				Log.d("Call Detector","Call in idle");
				callFinished = true;
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d("Call Detector", "Call offhook");
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d("Call Detector", "Call ringing");
			callFinished = true;
			if(MusicFragment.isPlaying) {
				new PlayPauseTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, MainActivity.RASPI_ADDRESS);
			}
			break;
		}
	}

}