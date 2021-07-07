package com.ncu.service;

import java.util.List;

import com.ncu.bean.Photo;

public interface PhotoService {

	List<Photo> savePhoto(Iterable<Photo> entities);
	
	Photo savePhoto(Photo photo);
}
