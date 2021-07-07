package com.ncu.service;

import java.util.List;

import com.ncu.bean.Collection;

public interface CollectionService {

	Collection saveCollection(Collection collection);
	
	Integer cencelCollection(Integer userId, Long worksId);
	
	Integer isCollection(Integer userId, Long worksId);
	
	Integer countOfCollection(Long worksId);
	
	Integer countOfMyCollection(Integer userId);
	
	List<Collection> findMyCollection(Integer userId);
	
	Integer deleteCollection(Integer cId, Integer userId);
}
