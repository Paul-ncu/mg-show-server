package com.ncu.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ncu.bean.Collection;

public interface CollectionDao extends JpaRepository<Collection, Integer>{

	Integer deleteByUserIdAndWorksId(Integer userId, Long worksId);
	
	Integer countByUserIdAndWorksId(Integer userId, Long worksId);
	
	Integer countByWorksId(Long worksId);
	
	Integer countByUserId(Integer userId);
	
	List<Collection> findByUserId(Integer userId);
	
	Integer deleteByIdAndUserId(Integer cId, Integer userId);
}
