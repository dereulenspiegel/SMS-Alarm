package de.akuz.android.smsalarm.util.test;

import de.akuz.android.smsalarm.util.TextUtils;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TextUtilsTestCase extends TestCase {
	
	 private static final String alarmSMSBody = "ALARM SEG-Einsatz";
	
	public void testForNonEmptyString() throws Exception {
		String testString = "Non empty String";
		Assert.assertTrue(TextUtils.isNonEmptyString(testString));
	}
	
	public void testForEmptyString() throws Exception {
		String testString = "";
		Assert.assertFalse(TextUtils.isNonEmptyString(testString));
	}
	
	public void testForNull() throws Exception {
		String testString = null;
		Assert.assertFalse(TextUtils.isNonEmptyString(testString));
	}
	
	public void testForSingleSpace() throws Exception {
		String testString = " ";
		Assert.assertFalse(TextUtils.isNonEmptyString(testString));
	}
	
	public void testGetKeyword() throws Exception {
		String keyword = "ALARM";
		
		Assert.assertEquals(keyword, TextUtils.getKeyword(alarmSMSBody));
	}
	
	public void testGetBody() throws Exception {
		String body = "SEG-Einsatz";
		
		Assert.assertEquals(body, TextUtils.getBody(alarmSMSBody));
	}

}
