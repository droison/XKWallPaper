package com.xkwallpaper.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.Paper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DownloadDAO {

	private DBHelper dbOpenHelper;

	public DownloadDAO(Context context) {
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
				values.put("dir", dir);

			db.insert("download", null, values);
		}
	}

	public boolean isExist(String paper_id) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from download where paper_id=? limit 1", new String[] { paper_id });
		boolean result = false;
		if (cursor.moveToFirst()) {
			result = true;
		}
		cursor.close();
		return result;
	}

	public List<DbPaper> getAll() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from download", new String[] {});
		List<DbPaper> papers = new ArrayList<DbPaper>();
		while (cursor.moveToNext()) {
			DbPaper paper = new DbPaper();
			int paper_id = cursor.getInt(1);
			String dir = cursor.getString(9);
			if (isFileExsit(paper_id,dir)) {// 此处比较一下是否文件在文件夹中存在，若存在则add，若不存在delete
				paper.setId(paper_id);
				paper.setTitle(cursor.getString(2));
				paper.setSphoto(cursor.getString(3));
				paper.setMphoto(cursor.getString(4));
				paper.setTime(new Date(cursor.getLong(5)));
				paper.setPraise(cursor.getInt(6));
				paper.setDownload(cursor.getInt(7));
				// 标签好像没啥用，就不取出来了
				paper.setDir(dir);
				papers.add(paper);
			} else {
				delete(paper_id + "");
			}
		}

		cursor.close();
		return papers;
	}

	private boolean isFileExsit(int paper_id, String dir) {
		File file = null;
		if (dir.equals("vid"))
			file = new File(AppConstants.APP_FILE_PATH + "/download", paper_id + ".mp4");
		else
			file = new File(AppConstants.APP_FILE_PATH + "/download", paper_id + ".jpg");
		return file.exists();
	}

	public void delete(String paper_id) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from download where paper_id=?", new String[] { paper_id });
	}

}
