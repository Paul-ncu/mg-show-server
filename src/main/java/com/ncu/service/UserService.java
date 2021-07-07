package com.ncu.service;

import com.ncu.bean.User;

public interface UserService {
	
	User save(User user);
	
	User getUserByOpenid(String openid);
	
	User getUserById(Integer id);
	
}
