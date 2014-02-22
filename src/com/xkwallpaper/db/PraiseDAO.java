package com.xkwallpaper.db;

import com.xkwallpaper.http.base.Paper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PraiseDAO {

	private DBHelper dbOpenHelper;

	public PraiseDAO(Context context) {
		DBHelper.init(context);
		this.dbOpenHelper = DBHelper.dbHelper();
	}

	// dir:pic壁纸 lock锁屏 vid视频
	public void save(Paper paper, String dir) {
		if (!isExist(paper.getId() + "")) {
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("paper_id", paper.getId());
			if (paper.getTitle() != null) {
				values.put("title", paper.getTitle());
			}
			if (paper.getSphoto() != null) {
				values.put("sphoto", paper.getSphoto());
			}
			if (paper.getMphoto() != null) {
				values.put("mphoto", paper.getMphoto());
			}
			if (paper.getTime() != null) {
				values.put("time", paper.getTime().getTime());
			}
			values.put("praise", paper.getPraise());
			values.put("download", paper.getDownload());
			if (paper.getTags() != null) {
				values.put("tags", paper.getTags().toString());
			}
			if (paper.getTitle() != null) {
				values.put("dir", dir);
			}

			db.insert("praise", null, values);
		}
	}
	
	// style  对应 dir:1 pic壁纸 2 lock锁屏 3 vid视频
		public void save(Paper paper) {
			if (!isExist(paper.getId() + "")) {
				SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put("paper_id", paper.getId());
				if (paper.getTitle() != null) {
					values.put("title", paper.getTitle());
				}
				if (paper.getSphoto() != null) {
					values.put("sphoto", paper.getSphoto());
				}
				if (paper.getMphoto() != null) {
					values.put("mphoto", paper.getMphoto());
				}
				if (paper.getTime() != null) {
					values.put("time", paper.getTime().getTime());
				}
				values.put("praise", paper.getPraise());
				values.put("download", paper.getDownload());
				if (paper.getTags() != null) {
					values.put("tags", paper.getTags().toString());
				}
				String dir = "";
				if(paper.getStyle() == 1){
					dir = "pic";
				}else if(paper.getStyle() == 2){
					dir = "lock";
				}else if(paper.getStyle() == 3){
					dir = "vid";
				}
				values.put("dir", dir);

				db.insert("praise", null, values);
			}
		}

	public boolean isExist(String paper_id) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from praise where paper_id=? limit 1", new String[] { paper_id });
		boolean result = false;
		if (cursor.moveToFirst()) {
			result = true;
		}
		cursor.close();
		return result;
	}

}
