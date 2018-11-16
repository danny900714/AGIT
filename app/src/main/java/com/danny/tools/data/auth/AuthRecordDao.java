package com.danny.tools.data.auth;
import com.danny.tools.data.*;
import android.database.sqlite.*;
import android.content.*;
import android.database.*;
import com.danny.tools.*;

public class AuthRecordDao extends BaseDao<AuthRecord>
{
	// table name
	public static final String TABLE_NAME= "auth";

	// key
	public static final String KEY_ID = "_id";
	
	// columns
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_REMOTE = "remote";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_IS_IGNORE = "ignore";
	
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
	KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	COLUMN_NAME + " TEXT NOT NULL, " +
	COLUMN_REMOTE + " TEXT NOT NULL, " +
	COLUMN_USERNAME + " TEXT, " +
	COLUMN_PASSWORD + " TEXT, " +
	COLUMN_IS_IGNORE + " INTEGER DEFAULT 0)";
	
	public AuthRecordDao(Context context) {
		super(TABLE_NAME, KEY_ID, context);
	}
	
	public AuthRecord get(String repositoryName, String remoteName) {
		AuthRecord record = null;
		
		String where = COLUMN_NAME + " = '" + repositoryName + "' AND " + COLUMN_REMOTE + " = '" + remoteName + "'";
		
		Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);
		if (result.moveToFirst())
			record = getRecord(result);
		
		result.close();
		
		return record;
	}

	@Override
	protected ContentValues getContentValues(AuthRecord data) {
		ContentValues cv = new ContentValues();
		
		cv.put(COLUMN_NAME, data.name);
		cv.put(COLUMN_REMOTE, data.remoteName);
		cv.put(COLUMN_USERNAME, data.userName);
		cv.put(COLUMN_PASSWORD, data.password);
		cv.put(COLUMN_IS_IGNORE, data.isIgnored);
		
		return cv;
	}

	@Override
	protected SQLiteDatabase getDatabase(Context context) {
		return GitDatabaseOpenHelper.getDatabase(context);
	}

	@Override
	protected long getId(AuthRecord data) {
		return data.id;
	}

	@Override
	protected AuthRecord getRecord(Cursor cursor) {
		if (cursor == null)
			return null;
		
		AuthRecord record = new AuthRecord();
		
		record.id = cursor.getLong(0);
		record.name = cursor.getString(1);
		record.remoteName = cursor.getString(2);
		record.userName = cursor.getString(3);
		record.password = cursor.getString(4);
		record.isIgnored = cursor.getInt(5) > 0;
		
		return record;
	}

	@Override
	protected void setId(AuthRecord data, long id) {
		data.id = id;
	}
	
}
