package de.akuz.android.smsalarm.fragments;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.akuz.android.smsalarm.R;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;

public class ManageAllowedNumbersFragment extends BaseFragment implements
		OnClickListener, OnItemLongClickListener {

	private AlarmDataAdapter alarmDataAdapter;
	private ListView numberList;
	private Button addNumberButton;
	private EditText addNumberEditText;
	private ListAdapter listAdapter;

	private AlarmGroup currentAlarmGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		alarmDataAdapter = new AlarmDataAdapter(getActivity());
		listAdapter = new ListAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		inflateLayout(R.layout.manage_allowed_numbers);
		numberList = findView(R.id.allowedNumberList);
		numberList.setAdapter(listAdapter);
		numberList.setOnItemLongClickListener(this);
		addNumberButton = findView(R.id.addNumberButton);
		addNumberButton.setEnabled(false);
		addNumberButton.setOnClickListener(this);
		addNumberEditText = findView(R.id.addNumberTextView);
		return root;
	}

	public void setAlarmGroup(long id) {
		alarmDataAdapter.open();
		currentAlarmGroup = alarmDataAdapter.getAlarmGroupById(id);
		alarmDataAdapter.close();
		if (currentAlarmGroup != null) {
			addNumberButton.setEnabled(true);
			listAdapter.clear();
			listAdapter.addAll(currentAlarmGroup.getAllowedNumbers());
		}
	}

	@Override
	public void onClick(View v) {
		String number = addNumberEditText.getText().toString();
		listAdapter.add(number);
		currentAlarmGroup.addAllowedNumber(number);
		alarmDataAdapter.open();
		alarmDataAdapter.saveAlarmGroup(currentAlarmGroup);
		alarmDataAdapter.close();
		addNumberEditText.setText("");
	}

	private static class ListAdapter extends BaseAdapter {

		private List<String> numbers;

		public ListAdapter(List<String> in) {
			this();
			numbers.addAll(in);
		}

		public ListAdapter() {
			numbers = new LinkedList<String>();
		}

		@Override
		public int getCount() {
			if (numbers == null) {
				return 0;
			}
			return numbers.size();
		}

		@Override
		public Object getItem(int arg0) {
			return numbers.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = (TextView) convertView;
			if (textView == null) {
				textView = new TextView(parent.getContext());
			}
			textView.setText((String) getItem(position));
			return textView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		public void add(String s) {
			numbers.add(s);
			notifyDataSetChanged();
		}

		public void remove(String s) {
			numbers.remove(s);
			notifyDataSetChanged();
		}

		public List<String> getList() {
			return numbers;
		}

		public List<String> getListCopy() {
			return new LinkedList<String>(numbers);
		}

		public void addAll(Collection<String> in) {
			numbers.addAll(in);
			notifyDataSetChanged();
		}

		public void clear() {
			numbers.clear();
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		String number = (String) listAdapter.getItem(arg2);
		listAdapter.remove(number);
		currentAlarmGroup.removeAllowedNumber(number);
		alarmDataAdapter.open();
		alarmDataAdapter.saveAlarmGroup(currentAlarmGroup);
		alarmDataAdapter.close();
		Toast.makeText(getActivity(), R.string.message_sender_deleted,
				Toast.LENGTH_LONG).show();
		return true;
	}

}
