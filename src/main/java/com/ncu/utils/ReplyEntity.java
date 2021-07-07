package com.ncu.utils;


import com.ncu.bean.Comment;
import com.ncu.bean.Works;

public class ReplyEntity {

	private Comment comment;
	
	private Comment parentComment;
	
	private Works works;

	public ReplyEntity() {
		super();
	}

	public ReplyEntity(Comment comment, Comment parentComment, Works works) {
		super();
		this.comment = comment;
		this.parentComment = parentComment;
		this.works = works;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Comment getParentComment() {
		return parentComment;
	}

	public void setParentComment(Comment parentComment) {
		this.parentComment = parentComment;
	}

	public Works getWorks() {
		return works;
	}

	public void setWorks(Works works) {
		this.works = works;
	}

	@Override
	public String toString() {
		return "ReplyEntity [comment=" + comment + ", parentComment=" + parentComment + ", works=" + works + "]";
	}
	
	
}
