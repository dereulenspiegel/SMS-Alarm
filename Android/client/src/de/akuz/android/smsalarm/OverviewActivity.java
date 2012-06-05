package de.akuz.android.smsalarm;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.akuz.android.smsalarm.fragments.AlarmGroupListFragment;
import de.akuz.android.smsalarm.fragments.AlarmGroupListFragment.GroupSelectedListener;

/**
 * This Activity is the main activity for this app. It provides a short overview
 * over all configured AlarmGroups
 * 
 * @author Till Klocke
 * 
 */
public class OverviewActivity extends BaseActivity implements
		GroupSelectedListener {

	private final static int MENU_ADD_ALARM_GROUP = 11;

	private AlarmGroupListFragment listFragment;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview_activity);
		setTitle(R.string.alarm_groups);
		listFragment = (AlarmGroupListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.alarmGroupListFragment);
		listFragment.setListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(Menu.NONE, MENU_ADD_ALARM_GROUP, Menu.NONE,
				R.string.add_alarm_group);
		item.setIcon(android.R.drawable.ic_menu_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_ADD_ALARM_GROUP) {
			addAlarmGroupButtonClicked();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This method is called when the add alarm group button is clicked. It
	 * starts the ConfigureAlarmActivity without any extras.
	 */
	private void addAlarmGroupButtonClicked() {
		startActivity(new Intent(this, AlarmGroupDetailActivity.class));
	}

	@Override
	public void alarmGroupSelected(long groupId) {
		Intent i = new Intent(this, AlarmGroupDetailActivity.class);
		i.putExtra(AlarmGroupDetailActivity.EXTRA_ALARM_GROUP_ID, groupId);
		startActivity(i);

	}
}