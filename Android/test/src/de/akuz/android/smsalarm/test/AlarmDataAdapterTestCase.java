package de.akuz.android.smsalarm.test;

import junit.framework.Assert;

import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.content.Context;
import android.test.AndroidTestCase;

public class AlarmDataAdapterTestCase extends AndroidTestCase {
	
	private Context mContext;
	private AlarmDataAdapter testAdapter;
	
	private final static String TEST_NAME="Testgruppe";
	private final static String TEST_KEYWORD="bhp1";
	private final static String TEST_SENDER="juh_do";

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
		AlarmGroup group = testAdapter.createNewAlarmGroup(TEST_NAME, TEST_KEYWORD);
		group.addAllowedNumber(TEST_SENDER);
		Assert.assertEquals(testAdapter.getAllAlarmGroups().size(), 1);
	}
	
	public void testRetrieveGroupByNumberAndKeyword() throws Exception {
		AlarmGroup group = 
			testAdapter.getAlarmGroupByNumberAndKeyword(TEST_SENDER, TEST_KEYWORD);
		Assert.assertNotNull(group);
		Assert.assertEquals(TEST_KEYWORD, group.getKeyword());
		Assert.assertEquals(TEST_NAME, group.getName());
	}

}
