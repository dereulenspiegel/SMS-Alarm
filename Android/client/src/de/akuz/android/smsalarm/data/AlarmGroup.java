package de.akuz.android.smsalarm.data;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.akuz.android.smsalarm.util.Log;
import de.akuz.android.smsalarm.util.NumberUtils;

/**
 * This class represents an AlarmGroup. AlarmGroups define sender numbers and
 * keywords which are allowed to send an alarm to this device. Also AlarmGroups
 * define which action should be taken if an alarm occurs.
 * 
 * @author Till Klocke
 * 
 */
public class AlarmGroup {

	private final static String TAG = "AlarmGroup";

	public final static String EXTRA_ALARM_GROUP_ID = "de.akuz.android.smsalarm.alarmgroupid";

	private final static String EMPTY = "";

	private long id;

	private List<String> allowedSender;
	private String keyword;
	private int ledColor;
	private String ringtoneUri;
	private boolean vibrate;
	private String name;
	private AlarmResponseConfiguration responseConfiguration;
	private boolean canRespond;

	public AlarmGroup() {
		this.id = -1;
	}

	public void setAllowedNumbers(List<String> allowedNumbers) {
		this.allowedSender = allowedNumbers;
	}

	/**
	 * Returns a user defined name of this alarm group
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a list of all allowed sender numbers for this group
	 * 
	 * @return List<String>
	 */
	public List<String> getAllowedNumbers() {
		return allowedSender;
	}

	/**
	 * Returns the unique id of this AlarmGroup. This id is the same as the
	 * database id
	 * 
	 * @return long
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Sets the descriptional name for this alarm group
	 * 
	 * @param name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Returns the URI to use as URI for the ringtone which be used as alarm
	 * 
	 * @return an URI
	 */
	public String getRingtoneURI() {
		return ringtoneUri;
	}

	/**
	 * Sets the uri to the alarm ringtone of this alarm group. If you give null
	 * or an empty uri string a default ringtone uri will be set
	 * 
	 * @param uri
	 */
	public void setRingtoneURI(final String uri) {
		this.ringtoneUri = uri;
	}

	/**
	 * Returns true if the phone should vibrate during the alarm
	 * 
	 * @return
	 */
	public boolean vibrate() {
		return vibrate;
	}

	/**
	 * Set wether this alarms based on this AlarmGroup should let the mobile
	 * vibrate
	 * 
	 * @param vibrate
	 */
	public void setVibrate(final boolean vibrate) {
		this.vibrate = vibrate;
	}

	/**
	 * Returns an integer representing the color in which LED should blink in
	 * case of an alarm. See the android reference for more details about this
	 * integer.
	 * 
	 * @return
	 */
	public int getLEDColor() {
		return ledColor;
	}

	/**
	 * Sets the color for the LED notification. Please see the android developer
	 * docs on how the hardware tries to match the color;
	 * 
	 * @param ledColor
	 */
	public void setLEDColor(final int ledColor) {
		this.ledColor = ledColor;
	}

	/**
	 * Returns the keyword with which an alarm SMS has to start
	 * 
	 * @return a String representing the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * Sets a new alarm keyword for this alarm group
	 * 
	 * @param keyword
	 */
	public void setKeyword(final String keyword) {
		this.keyword = keyword;
	}

	/**
	 * Add a number to the list of allowed senders. Since the sender number in
	 * SMS can contain much more than just numbers, any String can be put here
	 * 
	 * @param number
	 *            the new allowed sender of alarm sms for this AlarmGroup
	 */
	public void addAllowedNumber(final String number) {
		this.allowedSender.add(number);
	}

	/**
	 * Deletes a number (or sender) from the list of allowed numbers
	 * 
	 * @param number
	 *            the sender to be removed
	 */
	public void removeAllowedNumber(final String number) {
		this.allowedSender.remove(number);
	}

	/**
	 * Verify that the sender of the alarm SMS is an allowed sender. At first we
	 * try to match the sender against all allowed numbers with String.equals.
	 * If this fails we use all allowed numbers as regex and try to match
	 * against them.
	 * 
	 * @param sender
	 * @return true if we found a match
	 */
	public boolean verifySender(String sender) {
		List<String> allowedNumbers = getAllowedNumbers();
		String tempSender = sender;
		if (NumberUtils.isValidMobileNumber(sender)) {
			Log.info(TAG, "We are veryfying a valid mobile number: " + sender);
			tempSender = NumberUtils.convertNumberToInternationalFormat(sender);
		}
		for (String s : allowedNumbers) {
			if (s.equals(tempSender)) {
				Log.info(TAG, "We found the bare number directly: "
						+ tempSender + " s: " + s);
				return true;
			}
		}
		Log.debug(TAG, "No number matched directly, trying regex");
		return hasNumberAsPattern(allowedNumbers, sender);
	}

	/**
	 * This method iterates through all allowed numbers, uses them as regex and
	 * matches the sender against them.
	 * 
	 * @param numbers
	 * @param sender
	 * @return true if we found a match
	 */
	private boolean hasNumberAsPattern(List<String> numbers, String sender) {
		for (String s : numbers) {
			try {
				if (Pattern.matches(s, sender)) {
					Log.debug(TAG, "Number " + sender + " matched with regex: "
							+ s);
					return true;
				}
			} catch (PatternSyntaxException e) {
				Log.warning(TAG, "The pattern " + s + " wasn't a valid regex!",
						e);
			}
		}
		Log.debug(TAG, "Number " + sender + " didn't matched with any regex");
		return false;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AlarmResponseConfiguration getResponseConfiguration() {
		return responseConfiguration;
	}

	public void setResponseConfiguration(
			AlarmResponseConfiguration responseConfiguration) {
		this.responseConfiguration = responseConfiguration;
	}

	public boolean isCanRespond() {
		return canRespond;
	}

	public void setCanRespond(boolean canRespond) {
		this.canRespond = canRespond;
	}

}
