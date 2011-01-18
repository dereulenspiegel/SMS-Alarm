package de.akuz.android.smsalarm.util;

public class TextUtils {
	
	public static boolean isNonEmptyString(String in){
		if(in == null || in.trim().length() == 0){
			return false;
		}
		return true;
	}

}
