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


@Entity
@Table(name ="z_works")
public class Works{
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private User user;
	
	private Long createTime;
	
	private String introduction;
	
	private boolean display;
	
	private Long view;

	@JsonIgnore
	@OneToMany(mappedBy = "works", cascade = CascadeType.ALL)
	private List<Like> likes = new ArrayList<>();
	
	@OneToMany(mappedBy = "works", cascade = CascadeType.ALL)
	private List<Photo> photos = new ArrayList<Photo>();

	@OneToOne(cascade = CascadeType.ALL)
	private Video video;
	// 表示该works是照片还是视频，true为照片，false为视频
	private boolean photosOrVideo;
	
	@JsonIgnore
	@OneToMany(mappedBy = "works", cascade = CascadeType.ALL)
	private List<Comment> comments = new ArrayList<Comment>(); 
	
	public Works() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public Long getView() {
		return view;
	}

	public void setView(Long view) {
		this.view = view;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
	

	

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public boolean isPhotosOrVideo() {
		return photosOrVideo;
	}

	public void setPhotosOrVideo(boolean photosOrVideo) {
		this.photosOrVideo = photosOrVideo;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public List<Like> getLikes() {
		return likes;
	}

	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Works [id=" + id + ", introduction=" + introduction + "]";
	}

	
}
