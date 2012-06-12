package de.akuz.android.smsalarm.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.akuz.android.smsalarm.R;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import de.akuz.android.smsalarm.dialog.ColorPickerDialog;
import de.akuz.android.smsalarm.dialog.ColorPickerDialog.OnColorChangedListener;

public class AlarmGroupDetailFragment extends BaseFragment implements
		OnClickListener, OnColorChangedListener, OnCheckedChangeListener {

	private AlarmDataAdapter alarmDataAdapter;
	private RingtoneManager ringtoneManager;
	private AlarmGroup currentGroup;
	private Ringtone currentRingtone;

	private EditText nameView;
	private EditText keywordView;
	private ImageView colorView;
	private CheckBox vibrateCheckBox;
	private TextView ringtoneName;
	private ColorDrawable notificationColor;

	private Button chooseColorButton;
	private Button chooseRingtoneButton;

	public final static int REQUEST_CODE_SELECT_RINGTONE = 11;

	private final static int MENU_SAVE = 1;

	private GroupIdChangedListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Details", "OnCreate in Details");
		alarmDataAdapter = new AlarmDataAdapter(getActivity());
		ringtoneManager = new RingtoneManager(getActivity());
		this.setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("Details", "Creating View");
		inflateLayout(R.layout.alarm_group_detail_fragment);

		nameView = findView(R.id.alarmGroupNameField);
		nameView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				currentGroup.setName(s.toString());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		keywordView = findView(R.id.alarmKeywordField);
		keywordView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				currentGroup.setKeyword(s.toString());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		colorView = findView(R.id.colorView);
		vibrateCheckBox = findView(R.id.vibrateCheckBox);
		vibrateCheckBox.setOnCheckedChangeListener(this);
		ringtoneName = findView(R.id.ringtoneName);

		chooseColorButton = findView(R.id.chooseColorButton);
		chooseColorButton.setOnClickListener(this);
		chooseRingtoneButton = findView(R.id.chooseRingtoneButton);
		chooseRingtoneButton.setOnClickListener(this);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("Details", "onResume");
		bindUIToAlarmGroup(currentGroup);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem saveItem = menu.add(Menu.NONE, MENU_SAVE, Menu.NONE,
				R.string.menu_save);
		saveItem.setIcon(android.R.drawable.ic_menu_save);
		saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("Details", "optionsItem selected");
		if (item.getItemId() == MENU_SAVE) {
			Log.d("Details", "Save selected");
			save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void bindUIToAlarmGroup(AlarmGroup group) {

		if (group != null) {
			Log.d("Details", "Binding group to Ui");
			nameView.setText(group.getName());
			keywordView.setText(group.getKeyword());
			notificationColor = new ColorDrawable(group.getLEDColor());
			colorView.setImageDrawable(notificationColor);
			vibrateCheckBox.setChecked(group.vibrate());

			if (group.getRingtoneURI() != null) {
				currentRingtone = RingtoneManager.getRingtone(getActivity(),
						Uri.parse(group.getRingtoneURI()));
				if (currentRingtone != null) {
					ringtoneName.setText(currentRingtone
							.getTitle(getActivity()));
				}
			} else {
				ringtoneName.setText(R.string.no_ringtone_chosen);
			}
		} else {
			Log.d("Details", "Nulling all fields");
			nameView.setText("");
			keywordView.setText("");
			vibrateCheckBox.setChecked(false);
			notificationColor = new ColorDrawable(0);
			colorView.setImageDrawable(notificationColor);
			ringtoneName.setText(R.string.no_ringtone_chosen);
		}
	}

	public void setCurrentGroup(long groupId) {
		if (groupId >= 0) {
			alarmDataAdapter.open();
			currentGroup = alarmDataAdapter.getAlarmGroupById(groupId);
			alarmDataAdapter.close();
			bindUIToAlarmGroup(currentGroup);
		} else {
			currentGroup = new AlarmGroup();
			bindUIToAlarmGroup(currentGroup);
		}
	}

	@Override
	public void onClick(View v) {
		int clickedId = v.getId();
		if (clickedId == chooseRingtoneButton.getId()) {
			chooseRingtone();
		} else if (clickedId == chooseColorButton.getId()) {
			chooseColor();
		}

	}

	private void chooseRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
				getString(R.string.select_alarm_tone));
		if (currentGroup != null && currentGroup.getRingtoneURI() != null) {
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
					Uri.parse(currentGroup.getRingtoneURI()));
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_RINGTONE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_SELECT_RINGTONE
				&& resultCode == Activity.RESULT_OK) {
			Uri uri = data
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null) {
				currentGroup.setRingtoneURI(uri.toString());
			} else {
				currentGroup.setRingtoneURI(null);
			}
			bindUIToAlarmGroup(currentGroup);
		}
	}

	private void chooseColor() {
		ColorPickerDialog dialog = new ColorPickerDialog(getActivity(), this,
				currentGroup != null ? currentGroup.getLEDColor() : 0);
		dialog.show();
	}

	public void save() {
		long oldId = currentGroup.getId();
		alarmDataAdapter.open();
		currentGroup = alarmDataAdapter.saveAlarmGroup(currentGroup);
		alarmDataAdapter.close();
		long newId = currentGroup.getId();
		if (oldId != newId && listener != null) {
			listener.groupIdChanged(oldId, newId);
		}
		Toast.makeText(getActivity(), R.string.message_alarm_group_saved,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void colorChanged(int color) {
		Log.i("Details", "new color: " + color);
		currentGroup.setLEDColor(color);
		bindUIToAlarmGroup(currentGroup);
	}

	public void setListener(GroupIdChangedListener listener) {
		this.listener = listener;
	}

	public interface GroupIdChangedListener {
		public void groupIdChanged(long oldId, long newId);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		currentGroup.setVibrate(isChecked);

	}

}
