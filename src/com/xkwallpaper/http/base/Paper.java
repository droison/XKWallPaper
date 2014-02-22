package com.xkwallpaper.http.base;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Paper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private Date time;
	private int praise;
	private int download;
	private List<String> tags;
	private String sphoto;
	private String mphoto;
	private String photo_1, photo_2, photo_3, photo_4;
	private String pphoto;
	private String video;
	private int style;
	private boolean pay;
	private String price;

	public boolean isPay() {
		return pay;
	}

	public void setPay(boolean pay) {
		this.pay = pay;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getPphoto() {
		return pphoto;
	}

	public void setPphoto(String pphoto) {
		this.pphoto = pphoto;
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

	public String getSphoto() {
		return sphoto;
	}

	public void setSphoto(String sphoto) {
		this.sphoto = sphoto;
	}

	public String getMphoto() {
		return mphoto;
	}

	public void setMphoto(String mphoto) {
		this.mphoto = mphoto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
