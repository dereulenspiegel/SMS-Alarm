package de.akuz.android.smsalarm;

import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This custom View can be used to show alarms on the screen.
 * @author Till Klocke
 *
 */
public class AlarmItem extends LinearLayout {
	
	private TextView date;
	private TextView description;
	private TextView message;
	
	private Context mContext;

	public AlarmItem(final Context context, final String message, 
			final String description) {
		super(context);
		this.mContext = context;
		final View view = 
			LayoutInflater.from(context).inflate(R.layout.alarm_item, null);
		this.addView(view);
		this.date = (TextView)view.findViewById(R.id.TextViewDate);
		this.description = (TextView)view.findViewById(R.id.TextViewDescription);
		this.message = (TextView)view.findViewById(R.id.TextViewMessage);
		this.date.setText(DateFormat.getDateFormat(mContext).format(date));
		this.message.setText(message);
		this.description.setText(description);
	}

	public void setDate(final Date date) {
		this.date.setText(DateFormat.getDateFormat(mContext).format(date));
	}

	public void setDescription(final String description) {
		this.description.setText(description);
	}

	public void setMessage(final String message) {
		this.message.setText(message);
	}

}
