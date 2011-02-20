package de.akuz.android.smsalarm;

import java.util.List;

import de.akuz.android.smsalarm.data.AlarmGroup;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AlarmGroupListAdapter extends ArrayAdapter<AlarmGroup> {

	public AlarmGroupListAdapter(final Context context, final int textViewResourceId,
			final List<AlarmGroup> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(final int position, final View convertView, 
			final ViewGroup parent) {
		final AlarmGroup group = getItem(position);
		View alarmGroupView;
		if(convertView != null){
			alarmGroupView = convertView;
		} else {
			alarmGroupView = 
				LayoutInflater.from(
						parent.getContext()).inflate(R.layout.alarm_group_view, null);
		}
		
		final TextView alarmName = 
			(TextView)alarmGroupView.findViewById(R.id.AlarmGroupNameTextView);
		alarmName.setText(group.getName());
		final TextView alarmKeyword = 
			(TextView)alarmGroupView.findViewById(R.id.alarmKeywordTextView);
		alarmKeyword.setText(group.getKeyword());
		
		final ListView allowedNumberListView = 
			(ListView)alarmGroupView.findViewById(R.id.AllowedNumbersListView);
		final ArrayAdapter<String> listAdapter = 
			new ArrayAdapter<String>(parent.getContext(),android.R.layout.simple_list_item_1);
		allowedNumberListView.setAdapter(listAdapter);
		listAdapter.clear();
		for(String s : group.getAllowedNumbers()){
			listAdapter.add(s);
		}
		
		return alarmGroupView;
	}

}
