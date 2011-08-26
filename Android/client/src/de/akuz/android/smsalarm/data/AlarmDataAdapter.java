package de.akuz.android.smsalarm.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.akuz.android.smsalarm.util.Log;
import de.akuz.android.smsalarm.util.NumberUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class stores definitions for alarm groups.
 * @author Till Klocke
 *
 */
public class AlarmDataAdapter {
	
	private final static String TAG = "AlarmDataAdapter";
	
	private final static String DB_NAME="alarms";
	private final static int DB_VERSION=1;
	
	/**
	 * Table and column names for the table which stores the alarmgroups
	 */	
	final static String ALARM_TABLE_NAME="alarmgroups";
	final static String ALARM_ID="_id";
	final static String ALARM_NAME="name";
	final static String ALARM_RINGTONE="ringtone";
	final static String ALARM_VIBRATE="vibrate";
	final static String ALARM_LED="led";	
	final static String ALARM_KEYWORD="keyword";
	final static String ALARM_CREATE_STATEMENT=
		"create table "+ALARM_TABLE_NAME+" ("+
		ALARM_ID+" integer primary key autoincrement,"+
		ALARM_NAME+" text not null,"+
		ALARM_RINGTONE+" text,"+
		ALARM_VIBRATE+" boolean,"+
		ALARM_KEYWORD+" text unique not null,"+
		ALARM_LED+" integer);";
	
	/**
	 * Table and column names for the tables which stores the allowed numbers
	 */
	final static String NUMBER_TABLE_NAME="numbers";
	final static String NUMBER_ID="_id";
	final static String NUMBER_ALARM_ID="alarm_id";
	final static String NUMBER_NUMBER_STRING="number";
	final static String NUMBER_CREATE_STATEMENT=
		"create table "+NUMBER_TABLE_NAME+" ("+
		NUMBER_ID+" integer primary key autoincrement,"+
		NUMBER_ALARM_ID+" integer not null,"+
		NUMBER_NUMBER_STRING+" text not null);";
	
	/**
	 * DBHelper class
	 * @author Till Klocke
	 *
	 */
	private static class DBHelper extends SQLiteOpenHelper{
			
		public DBHelper(final Context context){
			super(context,DB_NAME,null,DB_VERSION);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onCreate(final SQLiteDatabase db) {
			Log.debug(TAG,"Creating table with statement:"+ALARM_CREATE_STATEMENT);
			db.execSQL(ALARM_CREATE_STATEMENT);
			db.execSQL(NUMBER_CREATE_STATEMENT);
			Log.debug(TAG,"Database created");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onUpgrade(final SQLiteDatabase db, 
				final int oldVersion, final int newVersion) {
			//TODO: implement more intelligent upgrade mechanism
			Log.debug(TAG,"Updating database, deleting old content...");
			db.execSQL("DROP TABLE IF EXISTS "+ALARM_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+NUMBER_TABLE_NAME);
			onCreate(db);
		}
	}
	
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Context mContext;
	
	/**
	 * A Map which contains all AlarmGroup objects create by this instance of
	 * AlarmDataAdapter to keep track of all objects and to be used for caching
	 */
	private HashMap<Long,AlarmGroup> alarmGroupObjects =
		new HashMap<Long,AlarmGroup>();
	
	/**
	 * Keeps track of all instances since we want only one instance per
	 * context
	 */
	private static HashMap<Context,AlarmDataAdapter> instances =
		new HashMap<Context,AlarmDataAdapter>();
	
	/**
	 * private constructor so only the getInstance method can create new instances
	 * @param context
	 */
	private AlarmDataAdapter(final Context context){
		dbHelper = new DBHelper(context);
		this.mContext = context;
	}
	
	/**
	 * This method returns an appropriate instance of AlarmDataAdpater for the given
	 * context
	 * @param context given Context
	 * @return a instance of AlarmDataAdapter
	 */
	public static AlarmDataAdapter getInstance(final Context context){
		if(instances.containsKey(context)){
			return instances.get(context);
		} else {
			final AlarmDataAdapter temp = new AlarmDataAdapter(context);
			instances.put(context, temp);
			return temp;
		}
	}
	
	/**
	 * Opens the Database in writeable mode
	 */
	public void open(){
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * This closes the database, but only if there aren't any AlarmGroup objects still
	 * open. It also removes this instance from the instances map.
	 */
	public void close(){
		if(alarmGroupObjects.size()==0){
			dbHelper.close();
			//Remove this instance from instances map
			instances.remove(mContext);
		}
		//TODO: probably throw an exception or so
	}
	
	/**
	 * returns the used SQLiteDatabase object so AlarmGroup objects can access it
	 * @return
	 */
	SQLiteDatabase getDatabase(){
		return db;
	}
	
	/**
	 * This method closes all generated AlarmGroup objects. So should be used with caution.
	 */
	public void closeAllChilds(){
		for(AlarmGroup a : alarmGroupObjects.values()){
			a.close();
		}
	}
	
	/**
	 * This method returns all alarm groups which are responsible for a certain number
	 * @param number The number of the sender
	 * @return an unmodifiable List of AlarmGroups
	 */
	public List<AlarmGroup> getAlarmGroupsByNumber(final String number){
		String temp = number.trim();
		Log.debug(TAG,"Querying for "+number);
		if(NumberUtils.isValidMobileNumber(number)){
			Log.debug(TAG, "Sender seems to be a valid, mobile number, comverting to international format");
			temp = NumberUtils.convertNumberToInternationalFormat(temp);
		}
		//Use query this distinct = ture
		Log.debug(TAG,"Querying for sender "+temp);
		final Cursor mCursor = db.query(true,
				NUMBER_TABLE_NAME, 
				new String[]{NUMBER_ALARM_ID}, 
				NUMBER_NUMBER_STRING+"='"+temp+"'", 
				null, 
				null, 
				null, 
				null,
				null);
		Log.debug(TAG, "Cursor has "+mCursor.getCount()+" results");
		final int numberAlarmId = mCursor.getColumnIndex(NUMBER_ALARM_ID);
		final List<AlarmGroup> tempList = getGroupsFromCursor(mCursor,numberAlarmId);
		mCursor.close();
		Log.debug(TAG, "Returning unmodifiable List");
		return Collections.unmodifiableList(tempList);
	}
	
	public AlarmGroup getAlarmGroupByKeyword(final String keyword){
		final Cursor mCursor = db.query(ALARM_TABLE_NAME, 
				new String[]{ALARM_ID}, 
				ALARM_KEYWORD+"=?", new String[]{keyword}, null, null, null);
		final int numberAlarmId = mCursor.getColumnIndex(ALARM_ID);
		final List<AlarmGroup> tempList = getGroupsFromCursor(mCursor,numberAlarmId);
		mCursor.close();
		Log.debug(TAG, "We got "+tempList.size()+" groups from keyword");
		if(tempList.size() > 0){
			return tempList.get(0);
		}
		return null;
	}
	
	private List<AlarmGroup> getGroupsFromCursor(final Cursor mCursor, 
			final int idColumn){
		final int numberAlarmId = idColumn;
		final List<AlarmGroup> tempList = new ArrayList<AlarmGroup>();
		mCursor.moveToFirst();
		if(mCursor.getCount()>0){
			do {
				final long id = mCursor.getLong(numberAlarmId);
				if(alarmGroupObjects.containsKey(id)){
					tempList.add(alarmGroupObjects.get(id));
				} else {
					final AlarmGroup tempGroup = 
						new AlarmGroup(this,mCursor.getInt(numberAlarmId));
					tempList.add(tempGroup);
					alarmGroupObjects.put(id, tempGroup);
				}
			} while(mCursor.moveToNext());
		}
		return tempList;
	}
	
	public AlarmGroup getAlarmGroupById(final long id){
		AlarmGroup retVal = null;
		if(alarmGroupObjects.containsKey(id)){
			return alarmGroupObjects.get(id);
		}
		final Cursor mCursor = db.query(ALARM_TABLE_NAME, 
				new String[]{ALARM_ID}, 
				ALARM_ID+"=?", 
				new String[]{String.valueOf(id)}, 
				null, 
				null, 
				null);
		if(mCursor.getCount()>0){
			retVal = new AlarmGroup(this,id);
			alarmGroupObjects.put(id, retVal);
		}
		mCursor.close();
		return retVal;
	}
	
	/**
	 * Return all AlarmGroups.
	 * @return
	 */
	public List<AlarmGroup> getAllAlarmGroups(){
		Log.debug(TAG, "A List with all AlarmGroups is requested");
		final Cursor mCursor = db.query(
				ALARM_TABLE_NAME, 
				new String[]{ALARM_ID}, null, 
				null, null, null, null);
		Log.debug(TAG, "The cursor has "+mCursor.getCount()+" rows");
		
		final int alarmId = mCursor.getColumnIndex(ALARM_ID);
		final List<AlarmGroup> tempList = getGroupsFromCursor(mCursor,alarmId);
		mCursor.close();
		return Collections.unmodifiableList(tempList);
	}
	
	/**
	 * Returns the first (and hopefully only) AlarmGroup object matching a certain number
	 * and a certain keyword.
	 * @param number
	 * @param keyword
	 * @return
	 */
	public AlarmGroup getAlarmGroupByNumberAndKeyword(final String number, 
			final String keyword){
		Log.debug(TAG,"Getting all AlarmGroups for sender "+number);
		final List<AlarmGroup> alarmGroups = getAlarmGroupsByNumber(number);
		Log.debug(TAG,"Got "+alarmGroups.size()+" groups");
		for(AlarmGroup g : alarmGroups){
			if(g.getKeyword().equals(keyword)){
				Log.debug(TAG, "Returning an AlarmGroup");
				return g;
			}
		}
		Log.warning(TAG, "Didn't found any matching AlarmGroup");
		return null;
	}
	
	/**
	 * This method allows child AlarmGroup objects to remove their self from the 
	 * alarmGroupObjects map
	 * @param id The id of the AlarmGroup
	 */
	void closeAlarmGroup(final long id){
		alarmGroupObjects.remove(id);
	}
	
	/**
	 * Creates a new AlarmGroup with the given name and keyword
	 * @param name A desciptional name for the new AlarmGroup
	 * @param keyword The Keyword for this AlarmGroup
	 * @return the created AlarmGroup
	 */
	public AlarmGroup createNewAlarmGroup(final String name, 
			final String keyword) throws SQLException{
		Log.debug(TAG, "Creating new AlarmGroup with name "+name);
		final ContentValues values = new ContentValues();
		values.put(ALARM_KEYWORD, keyword);
		values.put(ALARM_NAME, name);

		final long id = db.insertOrThrow(ALARM_TABLE_NAME, null, values);
		Log.debug(TAG, "The new AlarmGroup has the ID "+id);
		final AlarmGroup group = new AlarmGroup(this,id);
		alarmGroupObjects.put(id, group);
		return group;
	}
	
	/**
	 * Deletes an AlarmGroup with the given id
	 * @param id the id of the AlarmGroup which sould be deleted
	 */
	public void removeAlarmGroup(final long id){
		final String[] args = {String.valueOf(id)};
		alarmGroupObjects.remove(id);
		db.delete(ALARM_TABLE_NAME, ALARM_ID+"=?", args);
		db.delete(NUMBER_TABLE_NAME, NUMBER_ALARM_ID+"=?", args);
	}
	
	/**
	 * Deletes the whole database. Normally just for tests, so use with caution!
	 */
	public void clear(){
		db.delete(ALARM_TABLE_NAME, null, null);
		db.delete(NUMBER_TABLE_NAME, null, null);
	}

}
