package de.akuz.android.smsalarm;

import de.akuz.android.smsalarm.data.Alarm;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class AlarmActivity extends Activity {
	
	private ListView alarmListView;
	private AlarmListAdapter alarmListAdapter;
	private Intent callingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alarm_activity);
		alarmListView = (ListView)findViewById(R.id.AlarmListView);
		alarmListAdapter = new AlarmListAdapter(this);
		alarmListView.setAdapter(alarmListAdapter);
		callingIntent = this.getIntent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//TODO: Refresh intent
		Alarm[] alarms = 
			(Alarm[]) callingIntent.getParcelableArrayExtra(Alarm.PARCELABLE_KEYWORD);
		for(int i=0;i<alarms.length;i++){
			alarmListAdapter.add(alarms[i]);
		}
		//TODO: Sound the alarm
	}

}
