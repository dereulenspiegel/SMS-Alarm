package de.akuz.android.smsalarm.util.test;

import de.akuz.android.smsalarm.util.TextUtils;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TextUtilsTestCase extends TestCase {
	
	public void testForNonEmptyString() throws Exception {
		String testString = "Non empty String";
		Assert.assertEquals(true, TextUtils.isNonEmptyString(testString));
	}
	
	public void testForEmptyString() throws Exception {
		String testString = "";
		Assert.assertEquals(false, TextUtils.isNonEmptyString(testString));
	}
	
	public void testForNull() throws Exception {
		String testString = null;
		Assert.assertEquals(false, TextUtils.isNonEmptyString(testString));
	}
	
	public void testForSingleSpace() throws Exception {
		String testString = " ";
		Assert.assertEquals(false, TextUtils.isNonEmptyString(testString));
	}

}
