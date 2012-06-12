package de.akuz.android.smsalarm.view;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.akuz.android.smsalarm.R;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;

public class AlarmGroupListItemView extends LinearLayout {

	private Context mContext;
	private View layout;
	private TextView nameView;
	private LinearLayout colorLayout;

	private long group;

	public AlarmGroupListItemView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public AlarmGroupListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public AlarmGroupListItemView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	private void init() {
		layout = LayoutInflater.from(mContext).inflate(
				R.layout.alarm_group_list_view, this);
		nameView = (TextView) layout.findViewById(R.id.nameTextView);
		nameView.setText("");

		colorLayout = (LinearLayout) layout.findViewById(R.id.colorLayout);
	}

	public void bindAlarmGroup(Cursor c) {
		int alarmGroupIdId = c.getColumnIndex(AlarmDataAdapter.ALARM_ID);
		int alarmGroupName = c.getColumnIndex(AlarmDataAdapter.ALARM_NAME);
		int alarmColor = c.getColumnIndex(AlarmDataAdapter.ALARM_LED);

		this.nameView.setText(c.getString(alarmGroupName));
		colorLayout.setBackgroundColor(c.getInt(alarmColor));
		group = c.getLong(alarmGroupIdId);
	}

	public long getAlarmGroupId() {
		return this.group;
	}

}
