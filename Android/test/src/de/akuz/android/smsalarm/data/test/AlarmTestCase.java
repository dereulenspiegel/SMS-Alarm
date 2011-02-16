package de.akuz.android.smsalarm.data.test;

import junit.framework.Assert;
import de.akuz.android.smsalarm.data.Alarm;
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
		testAlarm.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		Alarm parcelAlarm = parcel.readParcelable(null);
		Assert.assertEquals(testAlarm, parcelAlarm);
	}

}
