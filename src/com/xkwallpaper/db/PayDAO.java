package com.xkwallpaper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PayDAO {

	private DBHelper dbOpenHelper;

	public PayDAO(Context context) {
		DBHelper.init(context);
		this.dbOpenHelper = DBHelper.dbHelper();
	}

	// dir:pic壁纸 lock锁屏 vid视频
	public void save(String token, int paper_id) {
		if (!isExist(token,paper_id)) {
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("paper_id", paper_id);
			values.put("token", token);

			db.insert("pay", null, values);
		}
	}

	public boolean isExist(String token, int paper_id) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from pay where paper_id=? and token=? limit 1", new String[] { String.valueOf(paper_id),token });
		boolean result = false;
		if (cursor.moveToFirst()) {
			result = true;
		}
		cursor.close();
		return result;
	}

	public void delete(String token, int paper_id) {

	}

}
