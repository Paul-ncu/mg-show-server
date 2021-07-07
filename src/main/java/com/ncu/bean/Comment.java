package com.ncu.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "z_comment")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String content;
	
	
	private Long createTime;
	
	// 发表评论的用户
	@OneToOne
	private User user;
	
	// 被回复的用户
	@OneToOne
	private User replyUser;
	
	@JsonIgnore
	@ManyToOne
	private Works works;

	@OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE)
	private List<Comment> replyComment = new ArrayList<>();
	
	@JsonIgnore
	@ManyToOne
	private Comment parentComment;
	
	public Comment() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getReplyUser() {
		return replyUser;
	}

	public void setReplyUser(User replyUser) {
		this.replyUser = replyUser;
	}

	public Works getWorks() {
		return works;
	}

	public void setWorks(Works works) {
		this.works = works;
	}

	public List<Comment> getReplyComment() {
		return replyComment;
	}

	public void setReplyComment(List<Comment> replyComment) {
		this.replyComment = replyComment;
	}

	public Comment getParentComment() {
		return parentComment;
	}

	public void setParentComment(Comment parentComment) {
		this.parentComment = parentComment;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", content=" + content + ", createTime=" + createTime + "]";
	}


	
}
