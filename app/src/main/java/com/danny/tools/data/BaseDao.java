package com.danny.tools.data;
import android.content.*;
import android.database.sqlite.*;
import android.database.*;
import java.util.*;
import com.danny.tools.*;
import java.lang.reflect.*;

public abstract class BaseDao<T>
{
	public final String TABLE_NAME;
	public final String KEY_ID;
	
	protected SQLiteDatabase db;
	protected Context context;
	
	protected BaseDao(String TABLE_NAME, String KEY_ID, Context context) {
		this.TABLE_NAME = TABLE_NAME;
		this.KEY_ID = KEY_ID;
		this.context = context;
		this.db = getDatabase(context);
	}
	
	public void close() {
		db.close();
	}
	
	public void copyDbTo(String path) {
		AndroidFileUtils.copyFileOrDirectory(db.getPath(), path);
	}
	
	public boolean delete(long id) {
		String where = KEY_ID + "=" + id;
		// check if success
		return db.delete(TABLE_NAME, where , null) > 0;
	}
	
	public T get(long id) {
        // 準備回傳結果用的物件
		T item = null;
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
	
	public List<T> getAll() {
		List<T> result = new ArrayList<>();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

		while (cursor.moveToNext()) {
			result.add(getRecord(cursor));
		}

		cursor.close();
		return result;
	}
	
	public int getCount() {
		int count = 0;

		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

		if (cursor.moveToNext())
			count = cursor.getInt(0);

		return count;
	}
	
	public T insert(T data) {
		ContentValues cv = getContentValues(data);
		
		long id = db.insert(TABLE_NAME, null, cv);
		setId(data, id);
		
		return data;
	}
	
	public boolean update(T data) {
		ContentValues cv = getContentValues(data);
		String where = KEY_ID + "=" + getId(data);
		return db.update(TABLE_NAME, cv, where, null) > 0;
	}
	
	protected abstract ContentValues getContentValues(T data);
	
	protected abstract SQLiteDatabase getDatabase(Context context);
	
	protected abstract long getId(T data);

	protected abstract T getRecord(Cursor cursor);
	
	protected abstract void setId(T data, long id);
}
