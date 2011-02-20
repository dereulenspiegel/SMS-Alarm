package de.akuz.android.smsalarm;

import java.util.List;

import de.akuz.android.smsalarm.data.Alarm;
import de.akuz.android.smsalarm.util.TextUtils;
import de.akuz.android.smsalarm.util.Log;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class AlarmActivity extends Activity {
	
	private final static String TAG = "AlarmActivity";
	
	private ListView alarmListView;
	private AlarmListAdapter alarmListAdapter;
	private Intent callingIntent;
	
	private String ringtoneUri;
	private int ledColor=-1;
	
	private boolean continueNotification = false;
	
	private AudioManager am;
	private PowerManager pm;
	private WakeLock myWakeLock;
	
	private int oldNotificationVolume;
	
	private int menu_acknowledge_alarm;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.debug(TAG, "alarmActivity created");
		this.setContentView(R.layout.alarm_activity);
		alarmListView = (ListView)findViewById(R.id.AlarmListView);
		alarmListAdapter = new AlarmListAdapter(this);
		alarmListView.setAdapter(alarmListAdapter);
		callingIntent = this.getIntent();
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//TODO: Refresh intent
		Bundle extraBundle = callingIntent.getBundleExtra(Alarm.PARCELABLE_KEYWORD);
		List<Alarm> alarmList = 
			extraBundle.getParcelableArrayList(Alarm.PARCELABLE_KEYWORD);
		Alarm[] alarms = alarmList.toArray(new Alarm[]{});
		alarmListAdapter.clear();
		
		for(int i=0;i<alarms.length;i++){
			//Set the notification settings. We use the first available settings to+
			//notify the user
			if(!TextUtils.isNonEmptyString(ringtoneUri) && 
					TextUtils.isNonEmptyString(alarms[i].getRingtoneUri())){
				ringtoneUri = alarms[i].getRingtoneUri();
			}
			if(ledColor == -1 && alarms[i].getLedColor()>-1){
				ledColor = alarms[i].getLedColor();
			}
			alarmListAdapter.add(alarms[i]);
		}
		if(alarms.length > 0){
			continueNotification = true;
			oldNotificationVolume = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 
					am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
			startNotification();
		}
	}
	
	/**
	 * This method starts the whole notification. Plays the ringtone, lets the LED blink
	 * and so on.
	 */
	private void startNotification(){
		//Make sure that the screen goes on and the device doesn't sleep
		myWakeLock = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, 
				TAG);
		myWakeLock.acquire();
		//Disable the Keyguard
		final KeyguardManager  myKeyGuard = 
			(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		final KeyguardLock myLock = myKeyGuard.newKeyguardLock(TAG);
		myLock.disableKeyguard();
		
		//Play the selected ringtone for notification. And play at it again and again
		//until the user acknowlegdes the alarm
		final Ringtone alert = RingtoneManager.getRingtone(this, Uri.parse(ringtoneUri));
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(continueNotification){
					if(!alert.isPlaying()){
						alert.play();
					}
				}
				alert.stop();
			}
			
		}).start();
		
	}
	
	/**
	 * This stops the alarm and resets the notification volume
	 */
	private void stopAlarm(){
		continueNotification = false;
		am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, oldNotificationVolume, 0);
		if(myWakeLock != null && myWakeLock.isHeld()){
			myWakeLock.release();
		}
	}
	
	/**
	 * This activity is singleTop so we have to handle new intents
	 */
	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		callingIntent = intent;
	}
	
	/**
	 * In case of a notifcation we have to make sure that the user isn't able to cancel
	 * the notification by accident
	 */
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		//If we are notificating the user of an alarm, it should only be possible to leave
		//this activity by acknowledging the alarm correctly
		if(handleKeyEvent(keyCode)){
			return true;
		}
		Log.debug(TAG, "KeyEvent wasn't Intercepted");
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean handleKeyEvent(int keyCode){
		//If we are notificating the user of an alarm, it should only be possible to leave
		//this activity by acknowledging the alarm correctly
		Log.debug(TAG,"Got KeyEvent");
		if(continueNotification && (keyCode == KeyEvent.KEYCODE_BACK 
				|| keyCode == KeyEvent.KEYCODE_HOME)){
			Log.debug(TAG, "Intercepting KeyEvent");
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuItem item = menu.add(getString(R.string.acknowlegde_alarm));
		this.menu_acknowledge_alarm = item.getItemId();
		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if(item.getItemId() == this.menu_acknowledge_alarm){
			//TODO:Show a dialog
			stopAlarm();
		}
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(handleKeyEvent(keyCode)){
			return true;
		}
		Log.debug(TAG, "KeyEvent wasn't Intercepted");
		return onKeyDown(keyCode, event);
	}

}
