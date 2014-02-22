package com.xkwallpaper.http.base;

import java.io.Serializable;
import java.util.Date;

public class CommentResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4745965615989453186L;
	private boolean result;
	private Temp comment;
	private int comment_num;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Temp getComment() {
		return comment;
	}

	public void setComment(Temp comment) {
		this.comment = comment;
	}

	public int getComment_num() {
		return comment_num;
	}

	public void setComment_num(int comment_num) {
		this.comment_num = comment_num;
	}

	class Temp implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2554145337274805587L;
		private String content;
		private Date created_at;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Date getCreated_at() {
			return created_at;
		}

		public void setCreated_at(Date created_at) {
			this.created_at = created_at;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getPaper_id() {
			return paper_id;
		}

		public void setPaper_id(int paper_id) {
			this.paper_id = paper_id;
		}

		public int getParent_id() {
			return parent_id;
		}

		public void setParent_id(int parent_id) {
			this.parent_id = parent_id;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Date getUpdate_at() {
			return update_at;
		}

		public void setUpdate_at(Date update_at) {
			this.update_at = update_at;
		}

		public int getUser_id() {
			return user_id;
		}

		public void setUser_id(int user_id) {
			this.user_id = user_id;
		}

		private int id;
		private int paper_id;
		private int parent_id;
		private String status;
		private String title;
		private Date update_at;
		private int user_id;
	}
}
