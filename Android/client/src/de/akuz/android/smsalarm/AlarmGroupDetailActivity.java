package de.akuz.android.smsalarm;

import android.content.Intent;
import android.os.Bundle;
import de.akuz.android.smsalarm.fragments.AlarmGroupDetailFragment;
import de.akuz.android.smsalarm.fragments.AlarmGroupDetailFragment.GroupIdChangedListener;
import de.akuz.android.smsalarm.fragments.ManageAllowedNumbersFragment;

public class AlarmGroupDetailActivity extends BaseActivity implements
		GroupIdChangedListener {

	private AlarmGroupDetailFragment detailFragment;
	private ManageAllowedNumbersFragment manageAllowedNumbersFragment;

	public final static String EXTRA_ALARM_GROUP_ID = "alarm.group.id";

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.alarm_group_detail_activity);

		detailFragment = (AlarmGroupDetailFragment) getSupportFragmentManager()
				.findFragmentById(R.id.alarmGroupDetailFragment);
		detailFragment.setCurrentGroup(-1);
		detailFragment.setListener(this);
		manageAllowedNumbersFragment = (ManageAllowedNumbersFragment) getSupportFragmentManager()
				.findFragmentById(R.id.manageAllowedNumberFragment);

		long id = getIntent().getLongExtra(EXTRA_ALARM_GROUP_ID, -1);
		if (id > -1) {
			detailFragment.setCurrentGroup(id);
			manageAllowedNumbersFragment.setAlarmGroup(id);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		long id = intent.getLongExtra(EXTRA_ALARM_GROUP_ID, -1);
		detailFragment.setCurrentGroup(id);
		if (id > -1) {
			manageAllowedNumbersFragment.setAlarmGroup(id);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void groupIdChanged(long oldId, long newId) {
		manageAllowedNumbersFragment.setAlarmGroup(newId);

	}

}
