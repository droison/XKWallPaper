package com.xkwallpaper.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SearchDAO {

	private DBHelper dbOpenHelper;

	public SearchDAO(Context context) {
		DBHelper.init(context);
		this.dbOpenHelper = DBHelper.dbHelper();
	}

	// 1代表关键词搜索，2代表标签搜索
	public void save(String key, int type) {
		if (isExist(key)) {
			delete(key);
		}
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("key", key);
		values.put("time", System.currentTimeMillis());
		values.put("type", type);
		db.insert("search", null, values);
	}

	public boolean isExist(String key) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from search where key=? limit 1", new String[] { key });
		boolean result = false;
		if (cursor.moveToFirst()) {
			result = true;
		}
		cursor.close();
		return result;
	}

	public List<DbSearch> getAll() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from search order by id desc", new String[] {});
		List<DbSearch> searchs = new ArrayList<DbSearch>();
		while (cursor.moveToNext()) {
			DbSearch search = new DbSearch();
			search.setId(cursor.getInt(0));
			search.setKey(cursor.getString(1));
			search.setType(cursor.getInt(2));
			search.setTime(cursor.getLong(3));
			searchs.add(search);
		}

		cursor.close();
		return searchs;
	}

	public void delete(String key) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from search where key=?", new String[] { key });
	}

}
