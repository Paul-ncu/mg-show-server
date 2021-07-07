package com.ncu.dao;





import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ncu.bean.Works;

public interface WorksDao extends JpaRepository<Works, Long>, JpaSpecificationExecutor<Works>{

	Integer countByUserId(Integer id);
	
	Integer countByIdAndUserId(Long id, Integer userId);
	
	Integer deleteByIdAndUserId(Long id, Integer userId);
	
	Works findByIdAndUserId(Long id, Integer userId);
}
