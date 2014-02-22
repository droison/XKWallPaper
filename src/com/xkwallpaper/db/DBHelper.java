package com.xkwallpaper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static DBHelper dbHelper;

	private int openedConnections = 0;

	public synchronized SQLiteDatabase getReadableDatabase() {
		openedConnections++;
		return super.getReadableDatabase();
	}

	public synchronized SQLiteDatabase getWritableDatabase() {
		openedConnections++;
		return super.getWritableDatabase();
	}

	public synchronized void close() {
		openedConnections--;
		if (openedConnections == 0) {
			super.close();
		}
	}

	public static DBHelper dbHelper() {
		return dbHelper;
	}

	public static void init(Context context) {
		if (dbHelper == null) {
			dbHelper = new DBHelper(context);
		}
	}

	private DBHelper(Context context) {
		super(context, "xingku.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String collectHistory = "create table collect" +
				"(id integer primary key," +
				"paper_id integer,"+
				"title varchar(255)," +
				"sphoto varchar(255)," +
				"mphoto varchar(255)," +
				"time long," +
				"praise integer," + 
				"download integer," +  
				"tags varchar(20)," +
				"dir varchar(20)" +
				");";
		String downloadHistory = "create table download" +
				"(id integer primary key," +
				"paper_id integer,"+
				"title varchar(255)," +
				"sphoto varchar(255)," +
				"mphoto varchar(255)," +
				"time long," +
				"praise integer," + 
				"download integer," +  
				"tags varchar(20)," +
				"dir varchar(20)" +
				");";
		String praiseHistory = "create table praise" +
				"(id integer primary key," +
				"paper_id integer,"+
				"title varchar(255)," +
				"sphoto varchar(255)," +
				"mphoto varchar(255)," +
				"time long," +
				"praise integer," + 
				"download integer," +  
				"tags varchar(20)," +
				"dir varchar(20)" +
				");";
		String searchHistory = "create table search" +
				"(id integer primary key," +
				"key varchar(255)," +
				"type integer," + //1代表基于搜索框的关键词搜索，2代表基于标签的标签搜索）
				"time long" +
				");";
		String account = "create table account"
				+ "(id integer primary key,"
				+ "username varchar(255),"
				+ "token varchar(255),"
				+ "face varchar(255),"
				+ "phone varchar(20),"
				+ "bind_weibo integer,"  //1表示已经绑定，0表示未绑定
				+ "bind_qq integer);";
		String pay = "create table pay"
				+ "(id integer primary key,"
				+ "token varchar(255),"
				+ "paper_id integer);";
		db.execSQL(collectHistory);
		db.execSQL(downloadHistory);
		db.execSQL(praiseHistory);
		db.execSQL(searchHistory);
		db.execSQL(account);
		db.execSQL(pay);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF praise");
		db.execSQL("DROP TABLE IF search");
		db.execSQL("DROP TABLE IF download");
		db.execSQL("DROP TABLE IF collect");
		db.execSQL("DROP TABLE IF account");
		db.execSQL("DROP TABLE IF pay");
		onCreate(db);
	}

}
