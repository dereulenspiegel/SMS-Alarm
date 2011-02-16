package de.akuz.android.smsalarm.data.test;

import junit.framework.Assert;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.test.AndroidTestCase;

public class AlarmGroupTestCase extends AndroidTestCase {
	
	private AlarmDataAdapter alarmAdapter;
	
	private final static String TEST_NAME = "bhp_do1";
	private final static String TEST_KEYWORD = "bhp_alarm";
	
	private AlarmGroup group;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		alarmAdapter = AlarmDataAdapter.getInstance(getContext());
		alarmAdapter.open();
		alarmAdapter.clear();
		group = alarmAdapter.createNewAlarmGroup(TEST_NAME, TEST_KEYWORD);
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

		group.setRingtoneURI(testUri);
		Assert.assertEquals(testUri, group.getRingtoneURI());
		group.setRingtoneURI(null);
		Assert.assertTrue(group.getRingtoneURI()!=null);
	}
	
	public void testModifyingKeyword() throws Exception {
		String newKeyword = "SEG_Alarm";
		Assert.assertEquals(TEST_KEYWORD, group.getKeyword());
		group.setKeyword(newKeyword);
		Assert.assertEquals(newKeyword, group.getKeyword());
	}
	
	public void testModifyingName() throws Exception {
		String newName = "SEG Betreuung";
		Assert.assertEquals(TEST_NAME, group.getName());
		group.setName(newName);
		Assert.assertEquals(newName, group.getName());
	}
	
	public void testModifyingLEDColor() throws Exception {
		int oldColor = group.getLEDColor();
		int newColor = 0x2323;
		group.setLEDColor(newColor);
		Assert.assertEquals(newColor, group.getLEDColor());
	}

}
