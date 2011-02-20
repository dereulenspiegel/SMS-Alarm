package de.akuz.android.smsalarm.test;

import com.jayway.android.robotium.solo.Solo;

import de.akuz.android.smsalarm.ConfigureAlarmActivity;
import android.test.ActivityInstrumentationTestCase2;

public class ConfigureAlarmActivityTestCase extends
		ActivityInstrumentationTestCase2<ConfigureAlarmActivity> {
	
	private Solo solo;
	
	public ConfigureAlarmActivityTestCase(){
		super("de.akuz.android.smsalarm", ConfigureAlarmActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
		String alarmName = "BHP Alarm";
		String alarmKeyword = "bhp_do1";
		boolean vibrate = true;
		String sender = "juh_do";
		solo.enterText(0, alarmName);
		solo.enterText(0, alarmKeyword);
		solo.clickOnCheckBox(0);
		solo.clickOnButton(1);
		solo.enterText(0, sender);
		solo.clickOnButton("Speichern");
		solo.clickOnMenuItem("Speichern");
	}
}
