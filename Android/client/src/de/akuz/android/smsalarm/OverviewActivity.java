package de.akuz.android.smsalarm;

import java.util.ArrayList;

import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class OverviewActivity extends Activity 
		implements OnClickListener, OnItemClickListener{
	
	private ListView alarmGroupListView;
	private Button addAlarmGroupButton;
	private AlarmGroupListAdapter listAdapter;
	private AlarmDataAdapter alarmDataAdapter;
	
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);
        alarmGroupListView = (ListView)findViewById(R.id.AlarmGroupListView);
        addAlarmGroupButton = (Button)findViewById(R.id.AddAlarmGroupButton);
        addAlarmGroupButton.setOnClickListener(this);
        alarmDataAdapter = AlarmDataAdapter.getInstance(this);
        alarmDataAdapter.open();
        listAdapter = new AlarmGroupListAdapter(this,
        		android.R.id.text1,new ArrayList<AlarmGroup>());
        alarmGroupListView.setAdapter(listAdapter);
        alarmGroupListView.setOnItemClickListener(this);
    }


	@Override
	public void onClick(final View view) {
		if(view.getId() == addAlarmGroupButton.getId()){
			addAlarmGroupButtonClicked();
		}
		
	}
	
	private void addAlarmGroupButtonClicked(){
		startActivity(new Intent(this,ConfigureAlarmActivity.class));
	}


	@Override
	protected void onResume() {
		super.onResume();
		listAdapter.clear();
		for(AlarmGroup g : alarmDataAdapter.getAllAlarmGroups()){
			listAdapter.add(g);
		}
	}


	@Override
	protected void onDestroy() {
		alarmGroupListView.setAdapter(null);
		listAdapter.clear();
		alarmDataAdapter.closeAllChilds();
		alarmDataAdapter.close();
		super.onDestroy();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		long alarmGroupId = listAdapter.getItem(position).getId();
		Intent i = new Intent(this,ConfigureAlarmActivity.class);
		i.putExtra(AlarmGroup.EXTRA_ALARM_GROUP_ID, alarmGroupId);
		startActivity(i);
	}
}