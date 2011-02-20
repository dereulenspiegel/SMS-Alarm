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
}
