package com.xkwallpaper.http;

import java.io.File;

import android.util.Log;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.Paper;

public class SaveVideo {

	public String saveVideoToLocal(Paper paper) {
		String url = "";
		if (paper.getVideo() != null && paper.getVideo().length() != 0) {
			url = paper.getVideo();
		} else {
			return null;
		}

		File oldfile = new File(AppConstants.APP_FILE_PATH + "/download", paper.getId() + ".temp");
		File newfile = new File(AppConstants.APP_FILE_PATH + "/download", paper.getId() + ".mp4");
		boolean isSuccess = false;
		File filePath = new File(AppConstants.APP_FILE_PATH + "/download");
		if (!filePath.isDirectory()) {
			filePath.mkdirs();
		}
		if (newfile.exists()) {
			isSuccess = true;
		} else {
			try {
				isSuccess = HTTP.download(url, oldfile);
			} catch (Exception e) {
				Log.e("SaveVideo", "下载视频", e);
			}

		}

		if (isSuccess) {
			oldfile.renameTo(newfile);
			return newfile.getAbsolutePath();
		} else
			return null;
	}

}