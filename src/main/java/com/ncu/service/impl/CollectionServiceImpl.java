package com.ncu.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncu.bean.Collection;
import com.ncu.dao.CollectionDao;
import com.ncu.service.CollectionService;

@Service
public class CollectionServiceImpl implements CollectionService {

	@Autowired
	CollectionDao collectionDao;

	@Transactional
	@Override
	public Collection saveCollection(Collection collection) {
		Collection save = collectionDao.save(collection);
		return save;
	}

	@Transactional
	@Override
	public Integer cencelCollection(Integer userId, Long worksId) {
		Integer count = collectionDao.deleteByUserIdAndWorksId(userId, worksId);
		return count;
	}

	@Override
	public Integer isCollection(Integer userId, Long worksId) {
		Integer count = collectionDao.countByUserIdAndWorksId(userId, worksId);
		return count;
	}

	/**
	 * 指定works的收藏数
	 */
	@Override
	public Integer countOfCollection(Long worksId) {
		Integer count = collectionDao.countByWorksId(worksId);
		return count;
	}

	/**
	 * 指定用户的收藏数量
	 */
	@Override
	public Integer countOfMyCollection(Integer userId) {
		Integer count = collectionDao.countByUserId(userId);
		return count;
	}

	/**
	 * 指定用户的收藏
	 */
	@Override
	public List<Collection> findMyCollection(Integer userId) {
		List<Collection> collections = collectionDao.findByUserId(userId);
		return collections;
	}

	/**
	 * 在用户收藏界面删除
	 */
	@Transactional
	@Override
	public Integer deleteCollection(Integer cId, Integer userId) {
		Integer count = collectionDao.deleteByIdAndUserId(cId, userId);
		return count;
	}

}
