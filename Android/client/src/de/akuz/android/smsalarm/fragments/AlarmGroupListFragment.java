package de.akuz.android.smsalarm.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.akuz.android.smsalarm.R;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.view.AlarmGroupListItemView;

public class AlarmGroupListFragment extends BaseFragment {

	public interface GroupSelectedListener {
		public void alarmGroupSelected(long groupId);
	}

	private AlarmDataAdapter alarmDataAdapter;
	private AlarmGroupAdapter listAdapter;
	private ListView alarmGroupListView;

	private GroupSelectedListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		alarmDataAdapter = new AlarmDataAdapter(getActivity());
		alarmDataAdapter.open();
		Cursor c = alarmDataAdapter.getAlarmGroupCursor();
		listAdapter = new AlarmGroupAdapter(getActivity(), c);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		inflateLayout(R.layout.alarm_group_list_fragment);

		alarmGroupListView = findView(R.id.alarmGroupList);
		alarmGroupListView.setAdapter(listAdapter);
		alarmGroupListView.setOnItemClickListener(listAdapter);
		alarmGroupListView.setOnItemLongClickListener(listAdapter);
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("List", "onResume");
		new LoadCursorTask().execute();
	}

	@Override
	public void onDestroy() {
		listAdapter.getCursor().close();
		alarmDataAdapter.close();
		super.onDestroy();
	}

	public void setListener(GroupSelectedListener listener) {
		this.listener = listener;
	}

	private class AlarmGroupAdapter extends CursorAdapter implements
			OnItemClickListener, OnItemLongClickListener {

		public AlarmGroupAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		@SuppressWarnings("deprecation")
		public AlarmGroupAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((AlarmGroupListItemView) view).bindAlarmGroup(cursor);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			AlarmGroupListItemView view = new AlarmGroupListItemView(context);
			view.bindAlarmGroup(cursor);
			return view;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			if (listener != null) {
				listener.alarmGroupSelected(id);
			}
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int position, long id) {
			alarmDataAdapter.removeAlarmGroup(id);
			Toast.makeText(getActivity(), R.string.message_alarm_group_deleted,
					Toast.LENGTH_LONG).show();
			new LoadCursorTask().execute();
			return true;
		}
	}

	private class LoadCursorTask extends AsyncTask<Void, Void, Void> {

		private Cursor mCursor = null;

		@Override
		protected Void doInBackground(Void... params) {
			mCursor = alarmDataAdapter.getAlarmGroupCursor();
			mCursor.getCount();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			AlarmGroupAdapter adapter = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				adapter = new AlarmGroupAdapter(getActivity(), mCursor, 0);
			} else {
				adapter = new AlarmGroupAdapter(getActivity(), mCursor);
			}
			listAdapter = adapter;
			alarmGroupListView.setAdapter(adapter);
		}

	}

}
