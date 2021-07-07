package com.ncu.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncu.bean.Photo;
import com.ncu.dao.PhotoDao;
import com.ncu.service.PhotoService;

@Service
public class PhotoServiceImpl implements PhotoService {

	@Autowired
	private PhotoDao photoDao;

	
	@Override
	@Transactional
	public List<Photo> savePhoto(Iterable<Photo> entities) {
		List<Photo> photos = photoDao.saveAll(entities);
		return photos;
	}

	@Transactional
	@Override
	public Photo savePhoto(Photo photo) {
		Photo save = photoDao.save(photo);
		return save;
	}
	


}
