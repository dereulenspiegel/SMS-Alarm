package de.akuz.android.smsalarm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small utility class to handle phone numbers and probably in the future other numbers
 * @author Till Klocke
 *
 */
public class NumberUtils {
	
	/**
	 * A regular expression describing a valid mobile phone number
	 */
	private final static String MOBILE_NUMBER_PATTERN =
		"/^(01|\\+491|00491)(5|6|7)([0-9])([0-9]{7})$/";
	
	/**
	 * This method checks is the given phone number is in a valid format accordings to
	 * a given regular expression. The number must be a valid german mobile phone number
	 * either in local or in international format.
	 * @param number the given phone number as a string
	 * @return true if the phone number is valid
	 */
	public static boolean isValidMobileNumber(final String number){
		final Pattern mobileNumberPattern = Pattern.compile(MOBILE_NUMBER_PATTERN);
		final Matcher matcher = mobileNumberPattern.matcher(number);
		return matcher.matches();
	}
	
	/**
	 * Checks if the given number is in an international format. Throws a 
	 * NumberFormatException if the given string doesn't represent a valid mobile
	 * phone number at all.
	 * @param number the given phone number
	 * @return true if number is in international format
	 * @throws NumberFormatException
	 */
	public static boolean isInternationalNumber(final String number) 
				throws NumberFormatException{
		if(!isValidMobileNumber(number)){
			throw new NumberFormatException();
		}
		if(number.startsWith("+49") || number.startsWith("0049")){
			return true;
		}
		return false;
	}
	
	/**
	 * This method converts a given phone number into international format, starting with
	 * a plus.
	 * @param number the given phone number
	 * @return the converted phone number
	 * @throws NumberFormatException
	 */
	public static String convertNumberToInternationalFormat(final String number) 
		throws NumberFormatException {
		if(!isValidMobileNumber(number)){
			throw new NumberFormatException();
		}
		if(isInternationalNumber(number)){
			if(number.charAt(0)=='+'){
				return number;
			} else {
				number.replaceFirst("00", "+");
				return number;
			}
		} else {
			number.replaceFirst("0", "+49");
			return number;
		}
		
	}
}
