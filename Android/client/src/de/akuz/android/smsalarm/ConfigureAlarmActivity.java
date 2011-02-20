package de.akuz.android.smsalarm;

import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ConfigureAlarmActivity extends Activity 
			implements OnClickListener, OnCreateContextMenuListener{
	
	private AlarmDataAdapter alarmAdapter;
	private AlarmGroup currentGroup;
	
	private EditText editKeyword;
	private EditText editName;
	private CheckBox checkBoxVibrate;
	private TextView toneTextView;
	private Button buttonSelectTone;
	private Button buttonAddSender;
	private ListView allowedSenderListView;
	private String ringtoneUri;
	
	private ArrayAdapter<String> allowedSenderAdapter;
	
	private final static int RINGTONE_REQUEST_CODE = 5;
	private final static int MENU_SAVE = 0;
	private final static int MENU_CANCEL = 1;
	private final static int CONTEXT_MENU_REMOVE_SENDER = 2;
	private final static int DIALOG_ENTER_SENDER = 3;
	
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_activity);
		alarmAdapter = AlarmDataAdapter.getInstance(this);
		
		editKeyword = (EditText)findViewById(R.id.editKeyword);
		editName = (EditText)findViewById(R.id.editName);
		checkBoxVibrate = (CheckBox)findViewById(R.id.checkVibrate);
		toneTextView = (TextView)findViewById(R.id.textView3);
		buttonSelectTone = (Button)findViewById(R.id.buttonSelectTone);
		buttonAddSender = (Button)findViewById(R.id.buttonAddSender);
		allowedSenderListView = (ListView)findViewById(R.id.listView1);
		
		allowedSenderAdapter = new ArrayAdapter<String>(this,android.R.id.text1);
		
		allowedSenderListView.setAdapter(allowedSenderAdapter);
		allowedSenderListView.setOnCreateContextMenuListener(this);
		buttonSelectTone.setOnClickListener(this);
		buttonAddSender.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuItem saveItem = menu.add(0, MENU_SAVE, 0, getString(R.string.menu_save));
		saveItem.setIcon(android.R.drawable.ic_menu_add);
		MenuItem cancelItem = 
			menu.add(0, MENU_CANCEL, 0, getString(R.string.menu_cancel));
		cancelItem.setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		alarmAdapter.closeAllChilds();
		alarmAdapter.clear();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(this.getIntent().getLongExtra(AlarmGroup.EXTRA_ALARM_GROUP_ID, -1)>-1){
			currentGroup = 
				alarmAdapter.getAlarmGroupById(
						this.getIntent().getLongExtra(
								AlarmGroup.EXTRA_ALARM_GROUP_ID, -1));
			
			editName.setText(currentGroup.getName());
			editKeyword.setText(currentGroup.getKeyword());
			checkBoxVibrate.setChecked(currentGroup.vibrate());
			ringtoneUri = currentGroup.getRingtoneURI();
			allowedSenderAdapter.clear();
			for(String s : currentGroup.getAllowedNumbers()){
				allowedSenderAdapter.add(s);
			}
			ringtoneUri = currentGroup.getRingtoneURI();
			toneTextView.setText(getToneTitle(ringtoneUri));
		}
	}
	
	private void selectRingtone(){
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
		this.startActivityForResult(intent, RINGTONE_REQUEST_CODE);

	}
	
	private void save(){
		if(currentGroup == null){
			currentGroup = alarmAdapter.createNewAlarmGroup(
					editName.getText().toString(), 
					editKeyword.getText().toString());
		} else {
			currentGroup.setKeyword(editKeyword.getText().toString());
			currentGroup.setName(editName.getText().toString());
			for(String s : currentGroup.getAllowedNumbers()){
				currentGroup.removeAllowedNumber(s);
			}
		}
		for(int i = 0;i<allowedSenderAdapter.getCount();i++){
			currentGroup.addAllowedNumber(allowedSenderAdapter.getItem(i));
		}
		currentGroup.setRingtoneURI(ringtoneUri);
		currentGroup.setVibrate(checkBoxVibrate.isChecked());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == RINGTONE_REQUEST_CODE){
			Uri uri = 
				data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if(uri!=null){
				ringtoneUri = uri.toString();
				toneTextView.setText(getToneTitle(ringtoneUri));
			}
		}
	}
	
	private String getToneTitle(String uri){
		Ringtone myTone = RingtoneManager.getRingtone(this, Uri.parse(uri));
		myTone.getTitle(this);
		return myTone.getTitle(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == buttonSelectTone.getId()){
			selectRingtone();
		} else if(v.getId() == buttonAddSender.getId()){
			showDialog(DIALOG_ENTER_SENDER);
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item.getItemId() == MENU_SAVE){
			save();
			return true;
		} else if(item.getItemId() == MENU_CANCEL){
			finish();
			return true;
		} 
		return super.onMenuItemSelected(featureId, item);
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == CONTEXT_MENU_REMOVE_SENDER){
			AdapterContextMenuInfo menuInfo = 
				(AdapterContextMenuInfo)item.getMenuInfo();
			String sender = allowedSenderAdapter.getItem(menuInfo.position);
			allowedSenderAdapter.remove(sender);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if(v.getId() == allowedSenderListView.getId()){
			menu.setHeaderTitle(R.string.remove_allowed_number);
			menu.add(0, CONTEXT_MENU_REMOVE_SENDER, 0, R.string.remove_allowed_number);
		} else {
			super.onCreateContextMenu(menu, v, menuInfo);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		this.setIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DIALOG_ENTER_SENDER:
			return createEnterSenderDialog();
		default:
			return null;
		}
	}
	
	private Dialog createEnterSenderDialog(){
		Dialog dialog = new Dialog(this);
		dialog.setTitle(R.string.add_allowed_number);
		//TODO: Set view, access ui elements, add listener etc.
		return dialog;
	}

}
