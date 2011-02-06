package de.akuz.android.smsalarm;

import java.util.Date;

import de.akuz.android.smsalarm.data.Alarm;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class AlarmListAdapter extends ArrayAdapter<Alarm> {

	public AlarmListAdapter(Context context) {
		super(context, 0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Alarm alarm = this.getItem(position);
		AlarmItem itemView;
		if(convertView !=null){
			itemView = (AlarmItem)convertView;
			itemView.setDate(new Date());
			itemView.setMessage(alarm.getMessage());
			itemView.setDescription(alarm.getDescription());
		} else {
			itemView = 
				new AlarmItem(parent.getContext(),alarm.getMessage(),alarm.getDescription());
		}
		return itemView;
	}

}
