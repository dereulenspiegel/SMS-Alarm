package de.akuz.android.smsalarm.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is a simple class to describe occuring alarms. This class implements the Parcelable
 * interface so it can be passed in Intents.
 * @author Till Klocke
 *
 */
public class Alarm implements Parcelable {
	
	public final static String PARCELABLE_KEYWORD="AlarmObjects";
	
	/**
	 * The sender from where the alarm originated
	 */
	private String sender;
	/**
	 * The message of the alarm
	 */
	private String message;
	/**
	 * A description of the alarm i.e. the name of the AlarmGroup
	 */
	private String description;
	/**
	 * The color in which the led should flash
	 */
	private int ledColor;
	/**
	 * An Uri specifying a ringtone
	 */
	private String ringtoneUri;
	/**
	 * The the device should vibrate
	 */
	private boolean vibrate;
	
	/**
	 * A needed field for Parcelables
	 */
	public static final Parcelable.Creator<Alarm> CREATOR = new Creator<Alarm>(){

		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Alarm createFromParcel(final Parcel source) {
			return new Alarm(source);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Alarm[] newArray(final int size) {
			return new Alarm[size];
		}
		
	};
	
	/**
	 * Private constructor used to recreate an Alarm object from a Parcel
	 * @param in
	 */
	private Alarm(final Parcel in){
		readFromParcel(in);
	}
	
	/**
	 * Simple constructor with necessary arguments
	 * @param sender
	 * @param message
	 * @param description
	 * @param ledColor
	 * @param ringtoneUri
	 * @param vibrate
	 */
	public Alarm(final String sender, final String message, 
			final String description, final int ledColor, 
			final String ringtoneUri, final boolean vibrate){
		this.sender = sender;
		this.message = message;
		this.description = description;
		this.ledColor = ledColor;
		this.ringtoneUri = ringtoneUri;
		this.vibrate = vibrate;
	}
	
	/**
	 * Constructor for convienience
	 * @param group
	 * @param sender
	 * @param message
	 */
	public Alarm(final AlarmGroup group, final String sender, final String message){
		this(sender, message,group.getName(),group.getLEDColor(),
				group.getRingtoneURI(), group.vibrate());
	}

	/**
	 * Returns a String representing the sender of the message
	 * @return
	 */
	public String getSender() {
		return sender;
	}
	
	/**
	 * Returns the received message without the keyword
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Returns the Description (name) of the AlarmGroup through which this Alarm
	 * was generated
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	
	/**
	 * Helper methos for constructing an instance from a Parcel
	 * @param in
	 */
	private void readFromParcel(final Parcel in){
		this.sender = in.readString();
		this.message = in.readString();
		this.description = in.readString();
		this.ledColor = in.readInt();
		this.ringtoneUri = in.readString();
		this.vibrate = in.readInt()==1;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeToParcel(final Parcel parcel, final int flags) {
		parcel.writeString(this.sender);
		parcel.writeString(this.message);
		parcel.writeString(this.description);
		parcel.writeInt(ledColor);
		parcel.writeString(ringtoneUri);
		parcel.writeInt(vibrate?1:0);
	}
	
	/**
	 * Returns the color in which the LED should flash
	 * @return
	 */
	public int getLedColor() {
		return ledColor;
	}
	
	/**
	 * Return the ringtone URI for this alarm
	 * @return
	 */
	public String getRingtoneUri() {
		return ringtoneUri;
	}
	
	/**
	 * Wether the mobile should vibrate
	 * @return
	 */
	public boolean isVibrate() {
		return vibrate;
	}

}
