package com.danny.tools.data.repository;
import android.database.sqlite.*;
import android.content.*;
import com.danny.tools.*;
import java.util.*;
import android.database.*;
import java.io.*;
import java.nio.channels.*;

public class RepositoryRecordDao
{
	// table name
	public static final String TABLE_NAME= "repository";
	
	// key
	public static final String KEY_ID = "_id";
	
	// columns
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_PATH = "path";
	
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
			KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_NAME + " TEXT NOT NULL, " +
			COLUMN_PATH + " TEXT NOT NULL)";
	
	// db
	private SQLiteDatabase db;
	private Context context;
	
	public RepositoryRecordDao(Context context) {
		this.context = context;
		db = GitDatabaseOpenHelper.getDatabase(context);
	}
	
	public void close() {
		db.close();
	}
	
	public RepositoryRecord insert(RepositoryRecord record) {
		ContentValues cv = new ContentValues();
		
		cv.put(COLUMN_NAME, record.name);
		cv.put(COLUMN_PATH, record.path);
		
		record.id = db.insert(TABLE_NAME, null, cv);
		
		return record;
	}
	
	public boolean update(RepositoryRecord record) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_NAME, record.name);
		cv.put(COLUMN_PATH, record.path);
		
		// where is key
		String where = KEY_ID + "=" + record.id;
		
		// update and check if success
		return db.update(TABLE_NAME, cv, where, null) > 0;
	}
	
	// delete defined id
	public boolean delete(long id){
		String where = KEY_ID + "=" + id;
		// check if success
		return db.delete(TABLE_NAME, where , null) > 0;
    }
	
	public List<RepositoryRecord> getAll() {
		List<RepositoryRecord> result = new ArrayList<>();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

		while (cursor.moveToNext()) {
			result.add(getRecord(cursor));
		}

		cursor.close();
		return result;
    }
	
	public List<RepositoryRecord> getAllInverse() {
		List<RepositoryRecord> result = new ArrayList<>();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, KEY_ID + " DESC");

		while (cursor.moveToNext()) {
			result.add(getRecord(cursor));
		}

		cursor.close();
		return result;
    }
	
	public RepositoryRecord get(long id) {
        // 準備回傳結果用的物件
		RepositoryRecord item = null;
        // 使用編號為查詢條件
		String where = KEY_ID + "=" + id;
        // 執行查詢
		Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        // 如果有查詢結果
		if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
			item = getRecord(result);
		}

        // 關閉Cursor物件
		result.close();
        // 回傳結果
		return item;
    }
	
	public int getCount() {
		int count = 0;
		
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
		
		if (cursor.moveToNext())
			count = cursor.getInt(0);
		
		return count;
	}
	
	public void copyDbTo(String path) {
		AndroidFileUtils.copyFileOrDirectory(db.getPath(), path);
	}
	
	public RepositoryRecord getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        RepositoryRecord result = new RepositoryRecord();

        result.id = cursor.getLong(0);
        result.name = cursor.getString(1);
        result.path = cursor.getString(2);
        
        return result;
    }
}
