package de.akuz.android.smsalarm;

import de.akuz.android.smsalarm.data.Alarm;
import de.akuz.android.smsalarm.util.TextUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;

public class AlarmActivity extends Activity {
	
	private ListView alarmListView;
	private AlarmListAdapter alarmListAdapter;
	private Intent callingIntent;
	
	private String ringtoneUri;
	private int ledColor=-1;
	
	private boolean continueNotification = false;
	
	private AudioManager am;
	
	private int oldNotificationVolume;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alarm_activity);
		alarmListView = (ListView)findViewById(R.id.AlarmListView);
		alarmListAdapter = new AlarmListAdapter(this);
		alarmListView.setAdapter(alarmListAdapter);
		callingIntent = this.getIntent();
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//TODO: Refresh intent
		Alarm[] alarms = 
			(Alarm[]) callingIntent.getParcelableArrayExtra(Alarm.PARCELABLE_KEYWORD);
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
	 * and so on
	 */
	private void startNotification(){
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
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		callingIntent = intent;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(continueNotification && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
