package com.xkwallpaper.http.base;

public class DownloadResult {
	private boolean result;
	private int download_num;
	private String photo_1;
	private String photo_2;
	private String photo_3;
	private String photo_4;
	private String video;

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public int getDownload_num() {
		return download_num;
	}

	public void setDownload_num(int download_num) {
		this.download_num = download_num;
	}

	public String getPhoto_1() {
		return photo_1;
	}

	public void setPhoto_1(String photo_1) {
		this.photo_1 = photo_1;
	}

	public String getPhoto_2() {
		return photo_2;
	}

	public void setPhoto_2(String photo_2) {
		this.photo_2 = photo_2;
	}

	public String getPhoto_3() {
		return photo_3;
	}

	public void setPhoto_3(String photo_3) {
		this.photo_3 = photo_3;
	}

	public String getPhoto_4() {
		return photo_4;
	}

	public void setPhoto_4(String photo_4) {
		this.photo_4 = photo_4;
	}

}
