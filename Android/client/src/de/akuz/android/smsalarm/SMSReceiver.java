package de.akuz.android.smsalarm;

import java.util.ArrayList;
import java.util.List;

import de.akuz.android.smsalarm.data.Alarm;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import de.akuz.android.smsalarm.util.TextUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	
	private Context mContext;
	
	private AlarmDataAdapter adapter;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		this.mContext = context;
		final Bundle bundle = intent.getExtras();
		//Get an instance of our AlarmAdapter for later query
		adapter = AlarmDataAdapter.getInstance(context);
		adapter.open();
		
		//get all message "pdus"
		final Object messages[] = (Object[]) bundle.get("pdus");
		/*Thread parsingThread = new Thread(new Runnable(){

			@Override
			public void run() {
				parseSMS(messages);
				
			}
			
		});*/
		//parsingThread.start();
		parseSMS(messages);
		//close the adapter and all its child to free resources
		adapter.closeAllChilds();
		adapter.close();
		
	}
	
	private void parseSMS(final Object[] messagePdus){
		final List<Alarm> alarms = new ArrayList<Alarm>();
		
		//got through all pdus, build sms from them and check if we have an alarm sms
		for (int n = 0; n < messagePdus.length; n++) {
			final SmsMessage temp = SmsMessage.createFromPdu((byte[])messagePdus[n]);
			String body = temp.getDisplayMessageBody();
			String keyWord = "";
			if(TextUtils.isNonEmptyString(body)){
				keyWord = body.substring(0, body.indexOf(' '));
				body = body.substring(body.indexOf(' '));
			} 
			
			final AlarmGroup group = adapter.getAlarmGroupByNumberAndKeyword(
					temp.getDisplayOriginatingAddress(), 
					keyWord);
			//If the sms matches an alarm group, build an alarm and add it to the list
			if(group!=null){
				alarms.add(new Alarm(temp.getDisplayOriginatingAddress(), 
						body, group.getName(), group.getLEDColor(), 
						group.getRingtoneURI(), group.vibrate()));
			}
			
		}
		
		if(alarms.size()>0){
			this.setResultData(null);
			this.setResultCode(1);
			sendAlarm(alarms);	
		}
	}
	
	/**
	 * Start the AlarmActivity and put all the parsed Alarms in the intent, so we now
	 * what all the fuzz is about
	 * @param alarms
	 */
	private void sendAlarm(final List<Alarm> alarms){
		final Intent alarmIntent = new Intent(mContext,AlarmActivity.class);
		alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final Bundle extraBundle = new Bundle();
		extraBundle.putParcelableArrayList(
				Alarm.PARCELABLE_KEYWORD, new ArrayList<Alarm>(alarms));
		alarmIntent.putExtra(Alarm.PARCELABLE_KEYWORD, extraBundle);
		mContext.startActivity(alarmIntent);
	}
	
	

}
