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
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_activity);
		alarmAdapter = AlarmDataAdapter.getInstance(this);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(this.getIntent().getLongExtra(AlarmGroup.EXTRA_ALARM_GROUP_ID, -1)>-1){
			currentGroup = 
				alarmAdapter.getAlarmGroupById(
						this.getIntent().getLongExtra(
								AlarmGroup.EXTRA_ALARM_GROUP_ID, -1));
		}
	}

}
