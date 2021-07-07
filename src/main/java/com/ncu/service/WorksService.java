package com.ncu.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.ncu.bean.Works;

public interface WorksService {
	
	Works saveWorks(Works works);
	
	Page<Works> getAllWorks(Integer pageNum);
	
	Page<Works> findWorksByUserId(Integer id, Integer pageNum);
	
	Works getWorksById(Long id);
	
	Integer getCountOfMyWorks(Integer userId);
	
	Integer isMyWorks(Integer userId, Long worksId);
	
	Integer deleteMyWorks(Integer userId, Long worksId);
	
	Works getWorksByIdAndUserId(Integer userId, Long worksId);
	
	Long getViewOfWorks(Long worksId);
}
