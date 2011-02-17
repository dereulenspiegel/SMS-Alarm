package de.akuz.android.smsalarm.data.test;

import junit.framework.Assert;
import de.akuz.android.smsalarm.data.Alarm;
import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.os.Parcel;
import android.test.AndroidTestCase;

public class AlarmTestCase extends AndroidTestCase {
	
	private Alarm testAlarm;
	
	private final static String TEST_SENDER = "juh_do";
	private final static String TEST_MESSAGE = "Alarm Einsatz";
	private final static String TEST_NAME = "BHP_DO1";
	private final static int LED_COLOR = 1;
	private final static String RINGTONE_URI = "ringtoneUri";
	private final static boolean VIBRATE = true;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testAlarm = new Alarm(TEST_SENDER,TEST_MESSAGE,TEST_NAME,LED_COLOR,
				RINGTONE_URI,VIBRATE);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}
	
	public void testGetters() throws Exception {
		Assert.assertEquals(TEST_SENDER, testAlarm.getSender());
		Assert.assertEquals(TEST_MESSAGE, testAlarm.getMessage());
		Assert.assertEquals(TEST_NAME, testAlarm.getDescription());
		Assert.assertEquals(LED_COLOR, testAlarm.getLedColor());
		Assert.assertEquals(RINGTONE_URI, testAlarm.getRingtoneUri());
		Assert.assertEquals(VIBRATE, testAlarm.isVibrate());
	}
	
	public void testParcelableMarshalling() throws Exception {
		Parcel parcel = Parcel.obtain();
		Assert.assertNotNull(testAlarm);
		testAlarm.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		//Alarm parcelAlarm = parcel.readParcelable(this.getClass().getClassLoader());
		Alarm parcelAlarm = Alarm.CREATOR.createFromParcel(parcel);
		Assert.assertEquals(parcelAlarm.getSender(), testAlarm.getSender());
		Assert.assertEquals(parcelAlarm.getMessage(), testAlarm.getMessage());
		Assert.assertEquals(parcelAlarm.getDescription(), testAlarm.getDescription());
		Assert.assertEquals(parcelAlarm.getLedColor(), testAlarm.getLedColor());
		Assert.assertEquals(parcelAlarm.getRingtoneUri(), testAlarm.getRingtoneUri());
		Assert.assertEquals(parcelAlarm.isVibrate(), testAlarm.isVibrate());
	}
	
	public void testCreatingAlarmFromAlarmGroup() throws Exception {
		AlarmDataAdapter testAdapter = AlarmDataAdapter.getInstance(getContext());
		String testKeyword = "bhp_do1";
		Assert.assertNotNull(testAdapter);
		testAdapter.open();
		testAdapter.clear();
		AlarmGroup testGroup = testAdapter.createNewAlarmGroup(TEST_NAME, testKeyword);
		testGroup.addAllowedNumber(TEST_SENDER);
		testGroup.setLEDColor(LED_COLOR);
		testGroup.setRingtoneURI(RINGTONE_URI);
		testGroup.setVibrate(VIBRATE);
		
		Alarm testAlarm = new Alarm(testGroup, TEST_SENDER, TEST_MESSAGE);
		Assert.assertEquals(TEST_SENDER, testAlarm.getSender());
		Assert.assertEquals(TEST_MESSAGE, testAlarm.getMessage());
		Assert.assertEquals(TEST_NAME, testAlarm.getDescription());
		Assert.assertEquals(LED_COLOR, testAlarm.getLedColor());
		Assert.assertEquals(RINGTONE_URI, testAlarm.getRingtoneUri());
		Assert.assertEquals(VIBRATE, testAlarm.isVibrate());
		testAdapter.closeAllChilds();
		testAdapter.close();
	}

}
