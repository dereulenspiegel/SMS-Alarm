package de.akuz.android.smsalarm;

import java.util.List;

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
import android.view.Window;
import android.view.WindowManager;
import de.akuz.android.smsalarm.data.Alarm;
import de.akuz.android.smsalarm.util.Log;
import de.akuz.android.smsalarm.util.TextUtils;

public class AlarmActivity extends BaseActivity {

	private final static String TAG = "AlarmActivity";

	private Intent callingIntent;

	private String ringtoneUri;
	private int ledColor = -1;

	private boolean continueNotification = false;

	private AudioManager am;
	private PowerManager pm;
	private WakeLock myWakeLock;

	private int oldNotificationVolume;

	private int menu_acknowledge_alarm;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		Log.debug(TAG, "alarmActivity created");
		this.setContentView(R.layout.alarm_activity);
		callingIntent = this.getIntent();
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		callingIntent = getIntent();
		final Bundle extraBundle = callingIntent
				.getBundleExtra(Alarm.EXTRA_ALARM_DATA);
		final List<Alarm> alarmList = extraBundle
				.getParcelableArrayList(Alarm.EXTRA_ALARM_DATA);
		final Alarm[] alarms = alarmList.toArray(new Alarm[] {});

		for (int i = 0; i < alarms.length; i++) {
			// Set the notification settings. We use the first available
			// settings to
			// notify the user
			if (!TextUtils.isNonEmptyString(ringtoneUri)
					&& TextUtils.isNonEmptyString(alarms[i].getRingtoneUri())) {
				ringtoneUri = alarms[i].getRingtoneUri();
			}
			if (ledColor == -1 && alarms[i].getLedColor() > -1) {
				ledColor = alarms[i].getLedColor();
			}
		}
		if (alarms.length > 0) {
			continueNotification = true;
			oldNotificationVolume = am
					.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
			am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
					am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
			startNotification();
		}
	}

	/**
	 * This method starts the whole notification. Plays the ringtone, lets the
	 * LED blink and so on.
	 */
	private void startNotification() {
		// Make sure that the screen goes on and the device doesn't sleep
		myWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
		myWakeLock.acquire();

		// Play the selected ringtone for notification. And play at it again and
		// again
		// until the user acknowlegdes the alarm
		final Ringtone alert = RingtoneManager.getRingtone(this,
				Uri.parse(ringtoneUri));
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (continueNotification) {
					if (alert != null && !alert.isPlaying()) {
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
	private void stopAlarm() {
		continueNotification = false;
		am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
				oldNotificationVolume, 0);
		if (myWakeLock != null && myWakeLock.isHeld()) {
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
		setIntent(callingIntent);
	}

	/**
	 * In case of a notifcation we have to make sure that the user isn't able to
	 * cancel the notification by accident
	 */
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		// If we are notificating the user of an alarm, it should only be
		// possible to leave
		// this activity by acknowledging the alarm correctly
		if (handleKeyEvent(keyCode)) {
			return true;
		}
		Log.debug(TAG, "KeyEvent wasn't Intercepted");
		return super.onKeyDown(keyCode, event);
	}

	private boolean handleKeyEvent(final int keyCode) {
		// If we are notificating the user of an alarm, it should only be
		// possible to leave
		// this activity by acknowledging the alarm correctly
		Log.debug(TAG, "Got KeyEvent");
		if (continueNotification
				&& (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
			Log.debug(TAG, "Intercepting KeyEvent");
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyUp(final int keyCode, final KeyEvent event) {
		if (handleKeyEvent(keyCode)) {
			return true;
		}
		Log.debug(TAG, "KeyEvent wasn't Intercepted");
		return onKeyDown(keyCode, event);
	}

}
