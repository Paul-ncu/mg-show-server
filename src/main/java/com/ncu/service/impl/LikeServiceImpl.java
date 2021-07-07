package com.ncu.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncu.bean.Like;
import com.ncu.dao.LikeDao;
import com.ncu.service.LikeService;

@Service
public class LikeServiceImpl implements LikeService {
	
	@Autowired
	LikeDao likeDao;

	@Transactional
	@Override
	public Like confirmLike(Like like) {
		Like save = likeDao.save(like);
		return save;
	}

	@Transactional
	@Override
	public Integer cancelLike(Integer userId, Long worksId) {
		Integer count = likeDao.deleteByUserIdAndWorksId(userId, worksId);
		return count;
	}

	@Override
	public Integer isLike(Integer userId, Long worksId) {
		Integer count = likeDao.countByUserIdAndWorksId(userId, worksId);
		return count;
	}

	@Override
	public Integer getCountByWorksId(Long worksId) {
		Integer count = likeDao.countByWorksId(worksId);
		return count;
	}

	@Override
	public Integer getCountByUserId(Integer userId) {
		Integer count = likeDao.countByUserId(userId);
		return count;
	}
	
	
	
}
