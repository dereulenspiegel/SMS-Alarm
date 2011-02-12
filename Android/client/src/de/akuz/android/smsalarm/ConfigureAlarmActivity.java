package de.akuz.android.smsalarm;

import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ConfigureAlarmActivity extends Activity {
	
	private AlarmDataAdapter alarmAdapter;
	private AlarmGroup currentGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_activity);
		alarmAdapter = AlarmDataAdapter.getInstance(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(this.getIntent().getLongExtra(AlarmGroup.EXTRA_ALARM_GROUP_ID, -1)>-1){
			
		}
	}

}
