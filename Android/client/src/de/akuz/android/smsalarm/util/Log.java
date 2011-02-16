package de.akuz.android.smsalarm.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Config;

public class Log {
	
	private static String logPath="/sdcard/.jabbroid/log.txt";
	private final static String TAG="Akuz Logger";
	
	private static boolean logToSD=false;
	
	public static void setLogToSD(final boolean toSD){
		logToSD=toSD;
	}
	
	public static void setLogPath(final String path){
		logPath = path;
	}
	
	public static String getLogPath(){
		return logPath;
	}
	
	public static void writeLog(final String tag, final String message, 
			final Throwable t){
		final Date date = new Date();
		final SimpleDateFormat dateFormat = 
			new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		final String dateString = dateFormat.format(date);
		final StringBuilder builder = new StringBuilder();
		builder.append(dateString);
		builder.append(':');
		builder.append(' ');
		builder.append(tag.trim());
		builder.append('\t');
		builder.append(message.trim());
		builder.append('\n');
		
		if(t!=null){
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			t.printStackTrace(printWriter);
			builder.append(result.toString());
			try {
				result.flush();
				result.close();
			} catch (IOException e) {
				Log.error(TAG,"Error while printing the stacktrace",e);
			}
			builder.append('\n');
		}
		
		BufferedWriter bos = null;
		try {
			bos = new BufferedWriter(new FileWriter(logPath));
			bos.write(builder.toString());
		} catch (IOException e) {
			Log.error(TAG,"Error while writing logging",e);
		} finally {
			if(bos!=null){
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					Log.error(TAG,"Error while closing log",e);
				}
			}
		}
	}
	
	public static void writeLog(final String tag, final String message){
		writeLog(tag,message,null);
	}
	
	public static void debug(final String tag, final String message){
		if(Config.LOGD){
			android.util.Log.d(tag, message);
			if(logToSD){
				writeLog(tag,message);
			}
		}
	}
	
	public static void debug(final String tag, final String message, 
			final Throwable t){
		if(Config.LOGD){
			android.util.Log.d(tag, message, t);
			if(logToSD){
				writeLog(tag,message,t);
			}
		}
	}
	
	public static void verbose(final String tag, final String message){
		if(Config.LOGV){
			android.util.Log.v(tag, message);
			if(logToSD){
				writeLog(tag,message);
			}
		}
	}
	
	public static void verbose(final String tag, final String message, 
			final Throwable t){
		if(Config.LOGV){
			android.util.Log.v(tag, message, t);
			if(logToSD){
				writeLog(tag,message,t);
			}
		}
	}
	
	public static void info(final String tag, final String message){
			android.util.Log.i(tag, message);
			if(logToSD){
				writeLog(tag,message);
			}
	}
	
	public static void info(final String tag, final String message, final Throwable t){
			android.util.Log.i(tag, message, t);
			if(logToSD){
				writeLog(tag,message,t);
			}
	}
	
	public static void error(final String tag, final String message){
		android.util.Log.e(tag, message);
		if(logToSD){
			writeLog(tag,message);
		}
	}
	
	public static void error(final String tag, final String message, 
			final Throwable t){
		android.util.Log.e(tag, message, t);
		if(logToSD){
			writeLog(tag,message,t);
		}
	}
	
	public static void warning(final String tag, final String message){
		android.util.Log.w(tag, message);
		if(logToSD){
			writeLog(tag,message);
		}
	}
	
	public static void warning(final String tag, final String message, 
			final Throwable t){
		android.util.Log.w(tag, message, t);
		if(logToSD){
			writeLog(tag,message,t);
		}
	}
}
