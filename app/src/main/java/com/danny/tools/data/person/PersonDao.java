package com.danny.tools.data.person;
import android.database.sqlite.*;
import android.content.*;
import com.danny.tools.*;
import java.util.*;
import android.database.*;
import com.danny.tools.data.*;
import java.lang.reflect.*;

public class PersonDao extends BaseDao<Person>
{
	// table name
	public static final String TABLE_NAME= "person";

	// key
	public static final String KEY_ID = "_id";

	// columns
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_PROFILE_PATH = "profile_path";

	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
	KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	COLUMN_NAME + " TEXT NOT NULL, " +
	COLUMN_EMAIL + " TEXT NOT NULL, " +
	COLUMN_PROFILE_PATH + " TEXT )";
	
	public PersonDao(Context context) {
		super(TABLE_NAME, KEY_ID, context);
	}

	@Override
	protected SQLiteDatabase getDatabase(Context context) {
		return GitDatabaseOpenHelper.getDatabase(this.context);
	}

	@Override
	protected ContentValues getContentValues(Person data) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_NAME, data.name);
		cv.put(COLUMN_EMAIL, data.email);
		cv.put(COLUMN_PROFILE_PATH, data.profilePath);
		
		return cv;
	}

	@Override
	protected long getId(Person data) {
		return data.id;
	}

	@Override
	public void setId(Person data, long id) {
		data.id = id;
	}
	
	@Override
	public Person getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Person result = new Person();

        result.id = cursor.getLong(0);
        result.name = cursor.getString(1);
        result.email = cursor.getString(2);
		result.profilePath = cursor.getString(3);

        return result;
    }
}
