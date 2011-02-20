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

/**
 * This Activity is for editing existing AlarmGroups and adding new ones.
 * All information is only saved when the user selects the "Save" menu item. Otherwise
 * all changes are discarded.
 * @author Till Klocke
 *
 */
public class ConfigureAlarmActivity extends Activity 
			implements OnClickListener, OnCreateContextMenuListener{
	
	private AlarmDataAdapter alarmAdapter;
	private AlarmGroup currentGroup;
	
	/**
	 * All UI-Elements with which we interact
	 */
	private EditText editKeyword;
	private EditText editName;
	private CheckBox checkBoxVibrate;
	private TextView toneTextView;
	private Button buttonSelectTone;
	private Button buttonAddSender;
	private ListView allowedSenderListView;
	private String ringtoneUri;
	
	private ArrayAdapter<String> allowedSenderAdapter;
	
	/**
	 * static ints for identifying MenuItems, dialogs and so on
	 */
	private final static int RINGTONE_REQUEST_CODE = 5;
	private final static int MENU_SAVE = 0;
	private final static int MENU_CANCEL = 1;
	private final static int CONTEXT_MENU_REMOVE_SENDER = 2;
	private final static int DIALOG_ENTER_SENDER = 3;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_activity);
		alarmAdapter = AlarmDataAdapter.getInstance(this);
		alarmAdapter.open();
		
		editKeyword = (EditText)findViewById(R.id.editKeyword);
		editName = (EditText)findViewById(R.id.editName);
		checkBoxVibrate = (CheckBox)findViewById(R.id.checkVibrate);
		toneTextView = (TextView)findViewById(R.id.textView3);
		buttonSelectTone = (Button)findViewById(R.id.buttonSelectTone);
		buttonAddSender = (Button)findViewById(R.id.buttonAddSender);
		allowedSenderListView = (ListView)findViewById(R.id.listView1);
		
		allowedSenderAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		
		allowedSenderListView.setAdapter(allowedSenderAdapter);
		allowedSenderListView.setOnCreateContextMenuListener(this);
		buttonSelectTone.setOnClickListener(this);
		buttonAddSender.setOnClickListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuItem saveItem = menu.add(0, MENU_SAVE, 0, getString(R.string.menu_save));
		saveItem.setIcon(android.R.drawable.ic_menu_add);
		final MenuItem cancelItem = 
			menu.add(0, MENU_CANCEL, 0, getString(R.string.menu_cancel));
		cancelItem.setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		alarmAdapter.closeAllChilds();
		alarmAdapter.close();
		super.onDestroy();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(this.getIntent().getLongExtra(AlarmGroup.EXTRA_ALARM_GROUP_ID, -1)>-1){
			//if we get an id of an existing AlarmGroup, load the AlarmGroup
			//and populate the info to the UI Elements
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
	
	/**
	 * This method starts the Ringtone Picker on the System
	 */
	private void selectRingtone(){
		final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, 
				RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
		this.startActivityForResult(intent, RINGTONE_REQUEST_CODE);

	}
	
	/**
	 * Here we write all information in the database
	 */
	private void save(){
		if(currentGroup == null){
			currentGroup = alarmAdapter.createNewAlarmGroup(
					editName.getText().toString(), 
					editKeyword.getText().toString());
		} else {
			currentGroup.setKeyword(editKeyword.getText().toString());
			currentGroup.setName(editName.getText().toString());
			//Clear all allowed numbers so we can just add all number from
			//the list adapter later
			for(String s : currentGroup.getAllowedNumbers()){
				currentGroup.removeAllowedNumber(s);
			}
		}
		for(int i = 0;i<allowedSenderAdapter.getCount();i++){
			currentGroup.addAllowedNumber(allowedSenderAdapter.getItem(i));
		}
		currentGroup.setRingtoneURI(ringtoneUri);
		currentGroup.setVibrate(checkBoxVibrate.isChecked());
		finish();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, 
			final Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == RINGTONE_REQUEST_CODE){
			final Uri uri = 
				data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if(uri!=null){
				ringtoneUri = uri.toString();
				toneTextView.setText(getToneTitle(ringtoneUri));
			}
		}
	}
	
	/**
	 * Get a human readable name for a ringtone
	 * @param uri The Uri of the Ringtone as String representation
	 * @return the human readable name as String
	 */
	private String getToneTitle(final String uri){
		if(uri!=null){
			final Ringtone myTone = RingtoneManager.getRingtone(this, Uri.parse(uri));
			myTone.getTitle(this);
			return myTone.getTitle(this);
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		if(v.getId() == buttonSelectTone.getId()){
			selectRingtone();
		} else if(v.getId() == buttonAddSender.getId()){
			showDialog(DIALOG_ENTER_SENDER);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if(item.getItemId() == MENU_SAVE){
			save();
			return true;
		} else if(item.getItemId() == MENU_CANCEL){
			finish();
			return true;
		} 
		return super.onMenuItemSelected(featureId, item);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		if(item.getItemId() == CONTEXT_MENU_REMOVE_SENDER){
			final AdapterContextMenuInfo menuInfo = 
				(AdapterContextMenuInfo)item.getMenuInfo();
			final String sender = allowedSenderAdapter.getItem(menuInfo.position);
			allowedSenderAdapter.remove(sender);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v,
			final ContextMenuInfo menuInfo) {
		if(v.getId() == allowedSenderListView.getId()){
			menu.setHeaderTitle(R.string.remove_allowed_number);
			menu.add(0, CONTEXT_MENU_REMOVE_SENDER, 0, R.string.remove_allowed_number);
		} else {
			super.onCreateContextMenu(menu, v, menuInfo);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onNewIntent(final Intent intent) {
		this.setIntent(intent);
		super.onNewIntent(intent);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Dialog onCreateDialog(final int id) {
		switch(id){
		case DIALOG_ENTER_SENDER:
			return createEnterSenderDialog();
		default:
			return null;
		}
	}
	
	/**
	 * Create a dialog for entering a new allowed sender
	 * @return a dialog object
	 */
	private Dialog createEnterSenderDialog(){
		final Dialog dialog = new Dialog(this);
		dialog.setTitle(R.string.add_allowed_number);
		dialog.setContentView(R.layout.add_sender_dialog);
		final EditText editSender = (EditText)dialog.findViewById(R.id.editSender);
		final Button saveButton = (Button)dialog.findViewById(R.id.buttonSave);
		final Button cancelButton = (Button)dialog.findViewById(R.id.buttonCancel);
		OnClickListener dialogOnClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(v.getId() == saveButton.getId()){
					allowedSenderAdapter.add(editSender.getText().toString());
					dialog.dismiss();
				} else if(v.getId() == cancelButton.getId()){
					dialog.dismiss();
				}
			}
		};
		saveButton.setOnClickListener(dialogOnClickListener);
		cancelButton.setOnClickListener(dialogOnClickListener);
		return dialog;
	}

}
