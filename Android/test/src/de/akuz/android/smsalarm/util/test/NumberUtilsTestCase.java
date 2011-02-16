package de.akuz.android.smsalarm.util.test;

import de.akuz.android.smsalarm.util.NumberUtils;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NumberUtilsTestCase extends TestCase{
	
	private String internationalFormat;
	private String internationalFormat2;
	private String localFormat;
	private String notANumber;
	
	public void testIsValidMobileNumber() throws Exception {
		Assert.assertTrue(NumberUtils.isValidMobileNumber(internationalFormat));
		Assert.assertTrue(NumberUtils.isValidMobileNumber(internationalFormat2));
		Assert.assertTrue(NumberUtils.isValidMobileNumber(localFormat));
		Assert.assertFalse(NumberUtils.isValidMobileNumber(notANumber));
	}
	
	public void testIsInternationalNumber() throws Exception {
		Assert.assertTrue(NumberUtils.isInternationalNumber(internationalFormat));
		Assert.assertTrue(NumberUtils.isInternationalNumber(internationalFormat2));
		Assert.assertFalse(NumberUtils.isInternationalNumber(localFormat));
		boolean exceptionThrown = false;
		try{
			NumberUtils.isInternationalNumber(notANumber);
		} catch(NumberFormatException e){
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
	}
	
	public void testConvertNumberToInternationalFormat() throws Exception {
		Assert.assertEquals(internationalFormat, 
				NumberUtils.convertNumberToInternationalFormat(localFormat));
		Assert.assertEquals(internationalFormat, 
				NumberUtils.convertNumberToInternationalFormat(internationalFormat2));
		boolean exceptionThrown = false;
		try{
			NumberUtils.convertNumberToInternationalFormat(notANumber);
		} catch (NumberFormatException e){
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		internationalFormat = "+491791791798";
		internationalFormat2 = "00491791791798";
		localFormat = "01791791798";
		notANumber = "NotaNumber";
	}

}
