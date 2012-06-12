package de.akuz.android.smsalarm;

import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import de.akuz.android.smsalarm.data.AlarmDataAdapter;

public class BaseActivity extends SherlockFragmentActivity {

	private AlarmDataAdapter alarmDataAdapter;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		alarmDataAdapter = new AlarmDataAdapter(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int id) {
		return (T) findViewById(id);
	}

}
