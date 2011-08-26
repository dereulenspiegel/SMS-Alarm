package de.akuz.android.smsalarm.util;

public class TextUtils {
	
	public static boolean isNonEmptyString(final String in){
		if(in == null){
			return false;
		}
		for(char c : in.toCharArray()){
			if(!Character.isWhitespace(c)){
				return true;
			}
		}
		return false;
	}
	
	public static String getKeyword(String body){
		return body.substring(0, body.indexOf(' '));
	}
	
	public static String getBody(String body){
		return body.substring(body.indexOf(' ')+1);
	}

}
