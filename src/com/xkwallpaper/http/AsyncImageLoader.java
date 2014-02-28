package com.xkwallpaper.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpStatus;

import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.HttpResponseEntity;
import com.xkwallpaper.thread.ThreadExecutor;
import com.xkwallpaper.util.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * author song
 * 
 * 类说明，图片异步加载
 * */
public class AsyncImageLoader {

	private static final String TAG = "AsyncImageLoader";

	public AsyncImageLoader() {
	}

	// dir="pic","lock","video" type="thumb""info"
	public void loadDrawable(final Context context, final String imageUrl, final ImageCallback imageCallback, String dir, String name) {
		final Handler handler = new ImageHandler(imageUrl, imageCallback);

		ThreadExecutor.execute(new ImageThread(imageUrl, context, handler, dir, name));

	}

	public Bitmap loadImageFromUrl(Context context, String imageUrl, String dir, String name) {
		Bitmap bm = null;
		
		String oldName = "";
		String newName = "";

		if (imageUrl != null && imageUrl.length() != 0) {
			newName = name;
			oldName = newName + ".temp";
		} else {
			return null;
		}

		File oldfile = new File(AppConstants.APP_FILE_PATH + "/" + dir, oldName);
		File newfile = new File(AppConstants.APP_FILE_PATH + "/" + dir, newName);

		File filePath = new File(AppConstants.APP_FILE_PATH + "/" + dir);
		if (!filePath.isDirectory()) {
			filePath.mkdirs();
		}

		if (!newfile.exists() && !newfile.isDirectory()) {
			try {

				FileOutputStream fos = new FileOutputStream(oldfile);
				HttpResponseEntity hre = HTTP.get(imageUrl);

				byte[] is = null;
				if (hre != null) {
					if (hre.getHttpResponseCode() == HttpStatus.SC_OK) {
						is = hre.getB();
						for (int i = 0; i < is.length; i++) {
							fos.write(is[i]);
						}
					}
				}
				fos.close();
				bm = ImageUtil.readBitmapAutoSize(oldfile.getAbsolutePath(), 0, 0);
				oldfile.renameTo(newfile);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		} else {
			bm = ImageUtil.readBitmapAutoSize(newfile.getAbsolutePath(), 0, 0);
		}
		return bm;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap bm, String imageUrl);
	}

	/**
	 * image使用的handler
	 * 
	 * @author chaisong
	 */
	static class ImageHandler extends Handler {
		private String imageUrl;

		private ImageCallback imageCallback;

		public ImageHandler(String imageUrl, ImageCallback imageCallback) {
			this.imageUrl = imageUrl;
			this.imageCallback = imageCallback;
		}

		public void handleMessage(Message message) {
			imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
		}
	}

	class ImageThread extends Thread {
		private String imageUrl;

		private Context context;

		private Handler handler;
		private String dir;
		private String name;

		public ImageThread(String imageUrl, Context context, Handler handler, String dir, String name) {
			this.imageUrl = imageUrl;
			this.context = context;
			this.handler = handler;
			this.dir = dir;
			this.name = name;
		}

		@Override
		public void run() {
			Bitmap bm = null;
			if (dir.equals("icon"))
				bm = loadIconImage(imageUrl, name);
			else
				bm = loadImageFromUrl(context, imageUrl, dir, name);
			Message message = handler.obtainMessage(0, bm);
			handler.sendMessage(message);
		}
	}

	public Bitmap loadIconImage(String imageUrl, String name) {

		Bitmap bm = null;
		String oldName = "";

		if (imageUrl != null && imageUrl.length() != 0) {
			oldName = name + ".temp";
		} else {
			return null;
		}

		File oldfile = new File(AppConstants.APP_FILE_PATH + "/icon", oldName);

		File filePath = new File(AppConstants.APP_FILE_PATH + "/icon");
		if (!filePath.isDirectory()) {
			filePath.mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(oldfile);
			HttpResponseEntity hre = HTTP.get(imageUrl);
			byte[] is = null;
			if (hre != null) {
				if (hre.getHttpResponseCode() == HttpStatus.SC_OK) {
					is = hre.getB();
					for (int i = 0; i < is.length; i++) {
						fos.write(is[i]);
					}
				}
			}
			fos.close();
			bm = BitmapFactory.decodeFile(oldfile.toString());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
		return bm;
	}

}
