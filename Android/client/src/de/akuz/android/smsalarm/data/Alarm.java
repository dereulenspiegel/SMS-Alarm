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
	
	public static final Parcelable.Creator<Alarm> CREATOR = new Creator<Alarm>(){

		@Override
		public Alarm createFromParcel(final Parcel source) {
			return new Alarm(source);
		}

		@Override
		public Alarm[] newArray(final int size) {
			return new Alarm[size];
		}
		
	};
	
	private Alarm(final Parcel in){
		readFromParcel(in);
	}
	
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

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public String getDescription() {
		return description;
	}

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

	@Override
	public void writeToParcel(final Parcel parcel, final int flags) {
		parcel.writeString(this.sender);
		parcel.writeString(this.message);
		parcel.writeString(this.description);
		parcel.writeInt(ledColor);
		parcel.writeString(ringtoneUri);
		parcel.writeInt(vibrate?1:0);
	}

	public int getLedColor() {
		return ledColor;
	}

	public String getRingtoneUri() {
		return ringtoneUri;
	}

	public boolean isVibrate() {
		return vibrate;
	}

}
