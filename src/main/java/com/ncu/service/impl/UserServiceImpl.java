package com.ncu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncu.bean.User;
import com.ncu.dao.UserDao;
import com.ncu.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	
	@Override
	public User save(User user) {
		User save = userDao.save(user);
		return save;
	}

	@Override
	public User getUserByOpenid(String openid) {
		User user = userDao.findByOpenid(openid);
		return user;
	}

	@Override
	public User getUserById(Integer id) {
		User user = userDao.getOne(id);
		return user;
	}



}
