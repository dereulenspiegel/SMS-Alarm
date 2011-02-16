package de.akuz.android.smsalarm.data.test;

import junit.framework.Assert;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.test.AndroidTestCase;

public class AlarmGroupTestCase extends AndroidTestCase {
	
	private AlarmDataAdapter alarmAdapter;
	
	private final static String TEST_NAME = "bhp_do1";
	private final static String TEST_KEYWORD = "bhp_alarm";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		alarmAdapter = AlarmDataAdapter.getInstance(getContext());
		alarmAdapter.open();
	}

	@Override
	protected void tearDown() throws Exception {
		alarmAdapter.closeAllChilds();
		alarmAdapter.close();
		super.tearDown();
	}
	
	public void testAddingAndRemovingAllowedNumbers() throws Exception {
		String testSender1 = "juh_do";
		String testSender2 = "01791791798";
		AlarmGroup group = alarmAdapter.createNewAlarmGroup(TEST_NAME, TEST_KEYWORD);
		group.addAllowedNumber(testSender1);
		group.addAllowedNumber(testSender2);
		Assert.assertEquals(2, group.getAllowedNumbers().size());
		Assert.assertTrue(group.getAllowedNumbers().contains(testSender1));
		Assert.assertTrue(group.getAllowedNumbers().contains(testSender2));
		group.removeAllowedNumber(testSender1);
		group.removeAllowedNumber(testSender2);
		Assert.assertEquals(0, group.getAllowedNumbers().size());
		Assert.assertFalse(group.getAllowedNumbers().contains(testSender1));
		Assert.assertFalse(group.getAllowedNumbers().contains(testSender2));
	}
	
	public void testSettingAndRemovingRingtoneURI() throws Exception {
		String testUri = "test://uri";
		AlarmGroup group = alarmAdapter.createNewAlarmGroup(TEST_NAME, TEST_KEYWORD);
		group.setRingtoneURI(testUri);
		Assert.assertEquals(testUri, group.getRingtoneURI());
		group.setRingtoneURI(null);
		Assert.assertEquals(null, group.getRingtoneURI());
	}

}