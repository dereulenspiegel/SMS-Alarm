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

	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		Bundle bundle = intent.getExtras();
		//Get an instance of our AlarmAdapter for later query
		AlarmDataAdapter adapter = AlarmDataAdapter.getInstance(context);
		
		//get all message "pdus"
		Object messages[] = (Object[]) bundle.get("pdus");
		
		List<Alarm> alarms = new ArrayList<Alarm>();
		
		//got through all pdus, build sms from them and check if we have an alarm sms
		for (int n = 0; n < messages.length; n++) {
			SmsMessage temp = SmsMessage.createFromPdu((byte[])messages[n]);
			String body = temp.getDisplayMessageBody();
			String keyWord = "";
			if(TextUtils.isNonEmptyString(body)){
				keyWord = body.substring(0, body.indexOf(' '));
				body = body.substring(body.indexOf(' '));
			} 
			
			AlarmGroup group = adapter.getAlarmGroupByNumberAndKeyword(
					temp.getDisplayOriginatingAddress(), 
					keyWord);
			//If the sms matches an alarm group, build an alarm and add it to the list
			if(group!=null){
				alarms.add(new Alarm(temp.getDisplayOriginatingAddress(), 
						body, group.getName(), group.getLEDColor(), 
						group.getRingtoneURI(), group.vibrate()));
			}
			
		}
		//close the adapter and all its child to free resources
		adapter.closeAllChilds();
		adapter.close();
		if(alarms.size()>0){
			sendAlarm(alarms);
			this.setResultData(null);
		}
	}
	
	/**
	 * Start the AlarmActivity and put all the parsed Alarms in the intent, so we now
	 * what all the fuzz is about
	 * @param alarms
	 */
	private void sendAlarm(List<Alarm> alarms){
		Intent alarmIntent = new Intent(mContext,AlarmActivity.class);
		alarmIntent.putExtra(Alarm.PARCELABLE_KEYWORD, alarms.toArray(new Alarm[]{}));
		mContext.startActivity(alarmIntent);
	}
	
	

}
