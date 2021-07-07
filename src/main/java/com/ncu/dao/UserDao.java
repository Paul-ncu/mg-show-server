package com.ncu.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.ncu.bean.User;

public interface UserDao extends JpaRepository<User, Integer>{

	User findByOpenid(String openid);
	
}
