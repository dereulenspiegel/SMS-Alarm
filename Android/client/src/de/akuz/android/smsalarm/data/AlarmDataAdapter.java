package de.akuz.android.smsalarm.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class stores definitions for alarm groups.
 * 
 * @author Till Klocke
 * 
 */
public class AlarmDataAdapter {

	private final static String TAG = "AlarmDataAdapter";

	private final static String DB_NAME = "alarmGroups.sql";
	private final static int DB_VERSION = 3;

	/**
	 * Table for response configurations
	 */
	public final static String RESPONSE_TABLE_NAME = "response_config";
	public final static String RESPONSE_ID = "_id";
	public final static String RESPONSE_ALARM_ID = "alarm_group_id";
	public final static String RESPONSE_TYPE = "response_type";
	public final static String RESPONSE_NUMBER = "response_number";
	public final static String RESPONSE_TO_RECEIVED_NUMBER = "response_to_received_number";
	public final static String RESPONSE_CREATE_STATEMENT = "create table "
			+ RESPONSE_TABLE_NAME + " (" + RESPONSE_ID
			+ " integer primary key autoincrement, " + RESPONSE_NUMBER
			+ " text, " + RESPONSE_TO_RECEIVED_NUMBER + " integer not null, "
			+ RESPONSE_ALARM_ID + " integer not null, " + RESPONSE_TYPE
			+ " text not null);";

	/**
	 * Table for response choices
	 */
	public final static String RESPONSE_CHOICE_TABLE_NAME = "response_choices";
	public final static String RESPONSE_CHOICE_ID = "_id";
	public final static String RESPONSE_CHOICE_MEANING = "choice_meaning";
	public final static String RESPONSE_CHOICE_PREFIX = "message_prefix";
	public final static String RESPONSE_CHOICE_CONFIG_ID = "config_id";
	public final static String RESPONCE_CHOICE_CREATE_STATEMENT = "create table "
			+ RESPONSE_CHOICE_TABLE_NAME
			+ " ( "
			+ RESPONSE_CHOICE_ID
			+ " integer primary key autoincrement, "
			+ RESPONSE_CHOICE_CONFIG_ID
			+ " integer not null, "
			+ RESPONSE_CHOICE_MEANING
			+ " text not null, "
			+ RESPONSE_CHOICE_PREFIX + " text not null);";

	/**
	 * Table and column names for the table which stores the alarmgroups
	 */
	public final static String ALARM_TABLE_NAME = "alarmgroups";
	public final static String ALARM_ID = "_id";
	public final static String ALARM_NAME = "name";
	public final static String ALARM_RINGTONE = "ringtone";
	public final static String ALARM_VIBRATE = "vibrate";
	public final static String ALARM_LED = "led";
	public final static String ALARM_KEYWORD = "alarmword";
	final static String ALARM_CREATE_STATEMENT = "create table "
			+ ALARM_TABLE_NAME + " (" + ALARM_ID
			+ " integer primary key autoincrement," + ALARM_NAME
			+ " text not null," + ALARM_RINGTONE + " text," + ALARM_VIBRATE
			+ " integer not null," + ALARM_KEYWORD + " text," + ALARM_LED
			+ " integer);";

	/**
	 * Table and column names for the tables which stores the allowed numbers
	 */
	final static String NUMBER_TABLE_NAME = "numbers";
	final static String NUMBER_ID = "_id";
	final static String NUMBER_ALARM_ID = "alarm_id";
	final static String NUMBER_NUMBER_STRING = "number";
	final static String NUMBER_CREATE_STATEMENT = "create table "
			+ NUMBER_TABLE_NAME + " (" + NUMBER_ID
			+ " integer primary key autoincrement," + NUMBER_ALARM_ID
			+ " integer not null," + NUMBER_NUMBER_STRING + " text not null);";

	/**
	 * DBHelper class
	 * 
	 * @author Till Klocke
	 * 
	 */
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(final Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onCreate(final SQLiteDatabase db) {
			Log.d(TAG, "Creating table with statement:"
					+ ALARM_CREATE_STATEMENT);
			db.execSQL(ALARM_CREATE_STATEMENT);
			db.execSQL(NUMBER_CREATE_STATEMENT);
			db.execSQL(RESPONSE_CREATE_STATEMENT);
			db.execSQL(RESPONCE_CHOICE_CREATE_STATEMENT);
			Log.d(TAG, "Database created");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			// TODO: implement more intelligent upgrade mechanism
			Log.d(TAG, "Updating database, deleting old content...");
			db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + NUMBER_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + RESPONSE_CHOICE_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + RESPONSE_TABLE_NAME);
			onCreate(db);
		}
	}

	private DBHelper dbHelper;
	private SQLiteDatabase db;

	private String[] allAlarmColumns = { ALARM_ID, ALARM_KEYWORD, ALARM_LED,
			ALARM_NAME, ALARM_RINGTONE, ALARM_VIBRATE };

	/**
	 * private constructor so only the getInstance method can create new
	 * instances
	 * 
	 * @param context
	 */
	public AlarmDataAdapter(final Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * Opens the Database in writeable mode
	 */
	public void open() {
		dbHelper.close();
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * This closes the database, but only if there aren't any AlarmGroup objects
	 * still open. It also removes this instance from the instances map.
	 */
	public void close() {
		dbHelper.close();
	}

	public AlarmGroup getAlarmGroupByKeyword(final String keyword) {
		final Cursor mCursor = db.query(ALARM_TABLE_NAME, null, ALARM_KEYWORD
				+ "=?", new String[] { keyword }, null, null, null);
		AlarmGroup group = null;
		if (mCursor.getCount() > 0 && mCursor.moveToFirst()) {
			group = bindCursorToAlarmGroup(mCursor);
		}
		mCursor.close();
		return group;
	}

	public AlarmGroup getAlarmGroupById(final long id) {
		AlarmGroup retVal = null;

		final Cursor mCursor = db.query(ALARM_TABLE_NAME, allAlarmColumns,
				ALARM_ID + "=?", new String[] { String.valueOf(id) }, null,
				null, null);
		if (mCursor.getCount() > 0 && mCursor.moveToFirst()) {

			retVal = bindCursorToAlarmGroup(mCursor);
		}
		mCursor.close();
		return retVal;
	}

	private AlarmResponseConfiguration getAlarmResponseConfigurationByAlarmGroupId(
			final long id) {
		AlarmResponseConfiguration config = null;
		final Cursor mCursor = db.query(RESPONSE_TABLE_NAME, null,
				RESPONSE_ALARM_ID + "=?", new String[] { String.valueOf(id) },
				null, null, null);
		if (mCursor.moveToFirst()) {
			config = bindCursorToAlarmResponseConfiguration(mCursor);
		}
		return config;
	}

	private List<String> getAllowedNumbersForGroupId(long id) {
		final Cursor mCursor = db.query(NUMBER_TABLE_NAME, new String[] {
				NUMBER_ALARM_ID, NUMBER_NUMBER_STRING, NUMBER_ID },
				NUMBER_ALARM_ID + "=?", new String[] { String.valueOf(id) },
				null, null, null);
		if (mCursor.getCount() > 0 && mCursor.moveToFirst()) {
			List<String> retVal = new ArrayList<String>(mCursor.getCount());
			int numberId = mCursor.getColumnIndex(NUMBER_NUMBER_STRING);
			do {
				retVal.add(mCursor.getString(numberId));
			} while (mCursor.moveToNext());
			mCursor.close();
			return retVal;
		} else {
			mCursor.close();
			return new ArrayList<String>(0);
		}
	}

	private AlarmGroup bindCursorToAlarmGroup(Cursor mCursor) {
		AlarmGroup retVal = new AlarmGroup();
		int keyWordId = mCursor.getColumnIndex(ALARM_KEYWORD);
		int ledColorId = mCursor.getColumnIndex(ALARM_LED);
		int nameId = mCursor.getColumnIndex(ALARM_NAME);
		int ringtoneUriId = mCursor.getColumnIndex(ALARM_RINGTONE);
		int vibrateId = mCursor.getColumnIndex(ALARM_VIBRATE);
		int idId = mCursor.getColumnIndex(ALARM_ID);

		retVal.setId(mCursor.getLong(idId));
		retVal.setKeyword(mCursor.getString(keyWordId));
		retVal.setLEDColor(mCursor.getInt(ledColorId));
		retVal.setName(mCursor.getString(nameId));
		retVal.setRingtoneURI(mCursor.getString(ringtoneUriId));
		retVal.setVibrate(mCursor.getInt(vibrateId) > 0);
		retVal.setAllowedNumbers(getAllowedNumbersForGroupId(retVal.getId()));
		retVal.setResponseConfiguration(getAlarmResponseConfigurationByAlarmGroupId(retVal
				.getId()));
		return retVal;
	}

	private AlarmResponseConfiguration bindCursorToAlarmResponseConfiguration(
			Cursor mCursor) {
		AlarmResponseConfiguration config = new AlarmResponseConfiguration();

		int idId = mCursor.getColumnIndex(RESPONSE_ID);
		int numberId = mCursor.getColumnIndex(RESPONSE_NUMBER);
		int toReceivedId = mCursor.getColumnIndex(RESPONSE_TO_RECEIVED_NUMBER);
		int typeId = mCursor.getColumnIndex(RESPONSE_TYPE);
		int parentIdId = mCursor.getColumnIndex(RESPONSE_ALARM_ID);

		config.setId(mCursor.getLong(idId));
		config.setRespondtoReceivedNumber(mCursor.getInt(toReceivedId) > 0);
		config.setResponseNumber(mCursor.getString(numberId));
		config.setResponseType(AlarmResponseTypes.valueOf(mCursor
				.getString(typeId)));
		config.setParentId(mCursor.getLong(parentIdId));

		return config;
	}

	private ContentValues bindAlarmResponseConfigurationToContentValues(
			AlarmResponseConfiguration config) {
		ContentValues values = new ContentValues();

		values.put(RESPONSE_ID, config.getId());
		values.put(RESPONSE_NUMBER, config.getResponseNumber());
		values.put(RESPONSE_TO_RECEIVED_NUMBER,
				config.isRespondtoReceivedNumber() ? 1 : 0);
		values.put(RESPONSE_TYPE, config.getResponseType().toString());
		values.put(RESPONSE_ALARM_ID, config.getParentId());

		return values;
	}

	/**
	 * Return all AlarmGroups.
	 * 
	 * @return
	 */
	public List<AlarmGroup> getAllAlarmGroups() {
		Log.d(TAG, "A List with all AlarmGroups is requested");
		final Cursor mCursor = db.query(ALARM_TABLE_NAME, allAlarmColumns,
				null, null, null, null, null);
		List<AlarmGroup> groupList = null;
		Log.d(TAG, "The cursor has " + mCursor.getCount() + " rows");
		if (mCursor.getCount() > 0 && mCursor.moveToFirst()) {
			groupList = new ArrayList<AlarmGroup>(mCursor.getCount());
			do {
				groupList.add(bindCursorToAlarmGroup(mCursor));
			} while (mCursor.moveToNext());
		} else {
			groupList = new ArrayList<AlarmGroup>(0);
		}
		return Collections.unmodifiableList(groupList);
	}

	public Cursor getAlarmGroupCursor() {
		return db.query(ALARM_TABLE_NAME, null, null, null, null, null, null);
	}

	/**
	 * Creates a new AlarmGroup with the given name and keyword
	 * 
	 * @param name
	 *            A desciptional name for the new AlarmGroup
	 * @param keyword
	 *            The Keyword for this AlarmGroup
	 * @return the created AlarmGroup
	 */
	public AlarmGroup createNewAlarmGroup(AlarmGroup group) throws SQLException {
		Log.d(TAG, "Creating new AlarmGroup with name " + group.getName());
		final ContentValues values = bindAlarmGroupToContentValues(group);

		final long id = db.insertOrThrow(ALARM_TABLE_NAME, null, values);
		Log.d(TAG, "The new AlarmGroup has the ID " + id);

		return getAlarmGroupById(id);
	}

	/**
	 * Deletes an AlarmGroup with the given id
	 * 
	 * @param id
	 *            the id of the AlarmGroup which sould be deleted
	 */
	public void removeAlarmGroup(final long id) {
		final String[] args = { String.valueOf(id) };
		db.delete(ALARM_TABLE_NAME, ALARM_ID + "=?", args);
		db.delete(NUMBER_TABLE_NAME, NUMBER_ALARM_ID + "=?", args);
	}

	/**
	 * Deletes the whole database. Normally just for tests, so use with caution!
	 */
	public void clear() {
		db.delete(ALARM_TABLE_NAME, null, null);
		db.delete(NUMBER_TABLE_NAME, null, null);
	}

	public AlarmGroup saveAlarmGroup(AlarmGroup group) {
		if (group.getId() < 0) {
			AlarmGroup newGroup = createNewAlarmGroup(group);
			updateAllowedNumbers(newGroup);
			insertAlarmResponseConfiguration(newGroup);
			return newGroup;
		} else {
			ContentValues values = bindAlarmGroupToContentValues(group);
			db.update(ALARM_TABLE_NAME, values, ALARM_ID + "=?",
					new String[] { String.valueOf(group.getId()) });
			updateAllowedNumbers(group);
			updateAlarmResponseConfiguration(group);
			return group;
		}

	}

	private void updateAvailableResponses(AlarmResponseConfiguration config) {
		List<AlarmResponse> responses = config.getAvailableResponses();

		db.delete(RESPONSE_CHOICE_TABLE_NAME, RESPONSE_CHOICE_CONFIG_ID + "=?",
				new String[] { String.valueOf(config.getId()) });
		if (responses != null) {
			for (AlarmResponse a : responses) {
				ContentValues values = new ContentValues();
				values.put(RESPONSE_CHOICE_CONFIG_ID, config.getId());
				values.put(RESPONSE_CHOICE_MEANING, a.getMeaning());
				values.put(RESPONSE_CHOICE_PREFIX, a.getMessagePrefix());
				db.insert(RESPONSE_CHOICE_TABLE_NAME, null, values);
			}
		}
	}

	private void insertAlarmResponseConfiguration(AlarmGroup group) {
		AlarmResponseConfiguration config = group.getResponseConfiguration();
		config.setParentId(group.getId());
		ContentValues values = bindAlarmResponseConfigurationToContentValues(config);
		long id = db.insert(RESPONSE_TABLE_NAME, null, values);
		config.setId(id);
		updateAvailableResponses(config);
	}

	private void updateAlarmResponseConfiguration(AlarmGroup group) {
		ContentValues values = bindAlarmResponseConfigurationToContentValues(group
				.getResponseConfiguration());
		db.update(RESPONSE_TABLE_NAME, values, RESPONSE_ALARM_ID + "=?",
				new String[] { String.valueOf(group.getId()) });
		updateAvailableResponses(group.getResponseConfiguration());
	}

	private ContentValues bindAlarmGroupToContentValues(AlarmGroup group) {
		ContentValues values = new ContentValues();
		values.put(ALARM_KEYWORD, group.getKeyword());
		values.put(ALARM_LED, group.getLEDColor());
		values.put(ALARM_NAME, group.getName());
		values.put(ALARM_RINGTONE, group.getRingtoneURI());
		values.put(ALARM_VIBRATE, group.vibrate() ? 1 : 0);
		return values;
	}

	private void updateAllowedNumbers(AlarmGroup group) {
		db.delete(NUMBER_TABLE_NAME, NUMBER_ALARM_ID + "=?",
				new String[] { String.valueOf(group.getId()) });
		if (group.getAllowedNumbers() != null) {
			for (String s : group.getAllowedNumbers()) {
				insertAllowedNumber(group.getId(), s);
			}
		}
	}

	private void insertAllowedNumber(long groupId, String allowedNumber) {
		ContentValues values = new ContentValues();
		values.put(NUMBER_ALARM_ID, groupId);
		values.put(NUMBER_NUMBER_STRING, allowedNumber);
		db.insert(NUMBER_TABLE_NAME, null, values);
	}

}
