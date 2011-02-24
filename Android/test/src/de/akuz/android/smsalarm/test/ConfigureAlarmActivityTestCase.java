package de.akuz.android.smsalarm.test;

import java.util.List;

import junit.framework.Assert;

import com.jayway.android.robotium.solo.Solo;

import de.akuz.android.smsalarm.ConfigureAlarmActivity;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

public class ConfigureAlarmActivityTestCase extends
		ActivityInstrumentationTestCase2<ConfigureAlarmActivity> {
	
	private Solo solo;
	private AlarmDataAdapter alarmAdapter;
	
	private String groupName = "BHP Alarm";
	private String groupKeyword = "bhp_do";
	private String allowedSender = "juh_do";
	
	public ConfigureAlarmActivityTestCase(){
		super("de.akuz.android.smsalarm", ConfigureAlarmActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();	
		
		alarmAdapter = 
			AlarmDataAdapter.getInstance(getInstrumentation().getTargetContext());
		alarmAdapter.open();
		alarmAdapter.clear();

		AlarmGroup group = alarmAdapter.createNewAlarmGroup(groupName, groupKeyword);
		group.setVibrate(true);
		group.addAllowedNumber(allowedSender);
		Intent i = new Intent();
		i.putExtra(AlarmGroup.EXTRA_ALARM_GROUP_ID, group.getId());
		this.setActivityIntent(i);
		
		solo = new Solo(getInstrumentation(),getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try{
			solo.finalize();
		} catch (Throwable e){
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
	
	public void testCreatingNewAlarmGroup() throws Exception {
		disableKeyguard();
		String alarmName = "BHP Alarm DO1";
		String alarmKeyword = "bhp_do1";
		boolean vibrate = true;
		String sender = "juh_do_bhp";
		solo.clearEditText(0);
		solo.enterText(0, alarmName);
		solo.clearEditText(1);
		solo.enterText(1, alarmKeyword);
		solo.clickOnCheckBox(0);
		solo.clickOnButton(1);
		solo.clearEditText(0);
		solo.enterText(0, sender);
		solo.clickOnButton("Speichern");
		List<ListView> listViews = solo.getCurrentListViews();
		Assert.assertEquals(sender, listViews.get(0).getItemAtPosition(1));
		solo.clickOnMenuItem("Speichern");
	}
	
	public void testDialogOnExit() throws Exception {
		disableKeyguard();
		solo.goBack();
		solo.waitForText("Wirklich ohne zu speichern beenden?");
		solo.clickOnButton("Ja");
	}
	
	public void testEditingExistingAlarmGroup() throws Exception {
		disableKeyguard();
		String groupName = "BHP Alarm";
		String groupKeyword = "bhp_do";
		String allowedSender = "juh_do";
		Assert.assertEquals(groupName,solo.getEditText(0).getText().toString());
		Assert.assertEquals(groupKeyword,solo.getEditText(1).getText().toString());
		Assert.assertTrue(solo.isCheckBoxChecked(0));
		Assert.assertTrue(solo.searchText(allowedSender));
	}
	
	public void disableKeyguard(){
		final KeyguardManager  myKeyGuard = 
			(KeyguardManager)getActivity().getSystemService(Context.KEYGUARD_SERVICE);
		final KeyguardLock myLock = myKeyGuard.newKeyguardLock("Test");
		myLock.disableKeyguard();
	}
}
