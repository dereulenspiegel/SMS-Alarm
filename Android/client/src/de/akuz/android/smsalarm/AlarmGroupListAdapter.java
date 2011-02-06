package de.akuz.android.smsalarm;

import java.util.List;

import de.akuz.android.smsalarm.data.AlarmGroup;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AlarmGroupListAdapter extends ArrayAdapter<AlarmGroup> {

	public AlarmGroupListAdapter(Context context, int textViewResourceId,
			List<AlarmGroup> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlarmGroup group = getItem(position);
		View alarmGroupView;
		if(convertView != null){
			alarmGroupView = convertView;
		} else {
			alarmGroupView = 
				LayoutInflater.from(
						parent.getContext()).inflate(R.layout.alarm_group_view, null);
		}
		
		TextView alarmName = 
			(TextView)alarmGroupView.findViewById(R.id.AlarmGroupNameTextView);
		alarmName.setText(group.getName());
		TextView alarmKeyword = 
			(TextView)alarmGroupView.findViewById(R.id.alarmKeywordTextView);
		alarmKeyword.setText(group.getKeyword());
		
		return alarmGroupView;
	}

}
