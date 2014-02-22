package com.xkwallpaper.db;

import com.xkwallpaper.http.base.LoginOrRegResult;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountDAO {

	private DBHelper dbOpenHelper;

	public AccountDAO(Context context) {
		DBHelper.init(context);
		this.dbOpenHelper = DBHelper.dbHelper();
	}

	public void save(DbAccount account) {
		delete();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", 2014);
		values.put("username", account.getUsername() == null ? "" : account.getUsername());
		values.put("token", account.getToken() == null ? "" : account.getToken());
		values.put("face", account.getFace() == null ? "" : account.getFace());
		values.put("phone", account.getPhone() == null ? "" : account.getPhone());
		values.put("bind_weibo", account.isBind_weibo() ? 1 : 0);
		values.put("bind_qq", account.isBind_qq() ? 1 : 0);
		db.insert("account", null, values);
	}

	public void save(LoginOrRegResult.Login loginResult) {
		delete();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", 2014);
		values.put("username", loginResult.getUsername() == null ? "" : loginResult.getUsername());
		values.put("token", loginResult.getPrivate_token() == null ? "" : loginResult.getPrivate_token());
		values.put("face", loginResult.getFace() == null ? "" : loginResult.getFace());
		values.put("phone", loginResult.getPhone() == null ? "" : loginResult.getPhone());
		values.put("bind_weibo", loginResult.isWeibo() ? 1 : 0);
		values.put("bind_qq", loginResult.isQq() ? 1 : 0);
		db.insert("account", null, values);
	}

	// 判断用户是否存在 为空则不存在
	public DbAccount getAccount() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from account limit 1", new String[] {});
		DbAccount dbAccount = null;
		if (cursor.moveToFirst()) {
			dbAccount = new DbAccount();
			dbAccount.setId(cursor.getInt(0));
			dbAccount.setUsername(cursor.getString(1));
			dbAccount.setToken(cursor.getString(2));
			dbAccount.setFace(cursor.getString(3));
			dbAccount.setPhone(cursor.getString(4));
			dbAccount.setBind_weibo(cursor.getInt(5) == 1);
			dbAccount.setBind_qq(cursor.getInt(6) == 1);
		}
		cursor.close();
		return dbAccount;
	}

	public void delete() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from account where id=?", new String[] { "2014" });
	}

}
