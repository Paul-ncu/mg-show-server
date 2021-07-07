package com.ncu.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.ncu.bean.Like;

public interface LikeDao extends JpaRepository<Like, Long>{
	
	Integer countByUserIdAndWorksId(Integer userId, Long worksId);
	
	Integer deleteByUserIdAndWorksId(Integer userId, Long worksId);
	
	Integer countByWorksId(Long worksId);
	
	Integer countByUserId(Integer userId);
}
