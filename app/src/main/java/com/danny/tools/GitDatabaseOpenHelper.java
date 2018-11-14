package com.danny.tools;
import android.database.sqlite.*;
import android.content.*;
import com.danny.tools.data.repository.*;
import com.danny.tools.data.person.*;
import com.danny.tools.data.auth.*;

public class GitDatabaseOpenHelper extends SQLiteOpenHelper
{
	
	public static final String DB_NAME = "git.db";
	public static int version = 1;
	public static SQLiteDatabase database;
	
	public GitDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public static SQLiteDatabase getDatabase(Context context) {
		if (database == null || !database.isOpen())
			database = new GitDatabaseOpenHelper(context, DB_NAME, null, version).getWritableDatabase();
		return database;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// create table
		db.execSQL(RepositoryRecordDao.CREATE_TABLE);
		db.execSQL(PersonDao.CREATE_TABLE);
		db.execSQL(AuthRecordDao.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int p2, int p3)
	{
		// delete original table
		db.execSQL("DROP TABLE IF EXISTS " + RepositoryRecordDao.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + PersonDao.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + AuthRecordDao.TABLE_NAME);
		
		// create new one
		onCreate(db);
	}
}
