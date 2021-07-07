package com.ncu.service;

import com.ncu.bean.Like;

public interface LikeService {

	Like confirmLike(Like like);
	
	Integer cancelLike(Integer userId, Long worksId);
	
	Integer isLike(Integer userId, Long worksId);
	
	Integer getCountByWorksId(Long worksId);
	
	Integer getCountByUserId(Integer userId);
}
