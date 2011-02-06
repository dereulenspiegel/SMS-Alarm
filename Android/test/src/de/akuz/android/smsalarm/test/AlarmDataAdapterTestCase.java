package de.akuz.android.smsalarm.test;

import junit.framework.Assert;

import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.content.Context;
import android.test.AndroidTestCase;

public class AlarmDataAdapterTestCase extends AndroidTestCase {
	
	private Context mContext;
	private AlarmDataAdapter testAdapter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.mContext = getContext();
		this.testAdapter = AlarmDataAdapter.getInstance(mContext);
		testAdapter.open();
		testAdapter.clear();
	}

	@Override
	protected void tearDown() throws Exception {
		testAdapter.closeAllChilds();
		testAdapter.close();
		super.tearDown();
	}

	public void testSameInstanceForSameContext() throws Exception{
		AlarmDataAdapter newReference = AlarmDataAdapter.getInstance(mContext);
		Assert.assertEquals(newReference, testAdapter);
	}

	public void testCreatingNewAlarmGroup() throws Exception {
		AlarmGroup group = testAdapter.createNewAlarmGroup("Testgruppe2", "juh_bhp2");
		Assert.assertEquals(testAdapter.getAllAlarmGroups().size(), 1);
	}

}
