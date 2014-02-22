package com.xkwallpaper.http.base;

import java.util.List;

public class CommentList {

	private List<Comment> comments;
	private int comment_num;

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public int getComment_num() {
		return comment_num;
	}

	public void setComment_num(int comment_num) {
		this.comment_num = comment_num;
	}

}
