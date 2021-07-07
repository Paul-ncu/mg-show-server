package com.ncu.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ncu.bean.User;
import com.ncu.service.UserService;
import com.ncu.utils.JwtUtil;
import com.ncu.utils.ResultEntity;


@RestController
public class UserController {

	Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	
	@PutMapping("/my/wechatInfo")
	public ResultEntity<String> updateWechatInfo(HttpServletRequest request, @RequestBody Map<String, String> dataMap) {
		//  获取请求头中的id
		Integer id = JwtUtil.getUserIdByToken(request);
		
		// 从请求参数中拿出nickName和avatarUrl
		String nickName = dataMap.get("nickName");
		String avatarUrl = dataMap.get("avatarUrl");
		
		// 从数据库中获得用户信息
		User user = userService.getUserById(id);
		user.setAvatarUrl(avatarUrl);
		user.setNickName(nickName);
		// 如果保存失败会抛出异常
		try {
			userService.save(user);
			return ResultEntity.returnWithoutData(200, "修改个人信息成功");
		} catch (Exception e) {
			return ResultEntity.returnWithoutData(400, "修改个人信息失败");
		}
		
	}
	
	@PutMapping("/my/username")
	public ResultEntity<String> updateUsername(HttpServletRequest request, @RequestBody Map<String, String> dataMap) {
		// 获取用户信息
		Integer id = JwtUtil.getUserIdByToken(request);
		User user = userService.getUserById(id);
		
		// 更新用户username
		String username = dataMap.get("username");
		user.setUsername(username);
	
		// 如果保存失败会抛出异常
		try {
			userService.save(user);
			return ResultEntity.returnWithoutData(200, "修改username成功");
		} catch (Exception e) {
			return ResultEntity.returnWithoutData(400, "修改username失败");
		}
	}
	
//	/*
//	 * 提交works的简介
//	 */
//	@PutMapping("/my/introduction")
//	public ResultEntity<String> updateIntroduction(HttpServletRequest request, @RequestBody Map<String, String> dataMap) {
//		
//		Integer id = JwtUtil.getUserIdByToken(request);
//		User user = userService.getUserById(id);
//		
//		
//		String introduction = dataMap.get("introduction");
//		user.setIntroduction(introduction);
//	
//		// 如果保存失败会抛出异常
//		try {
//			userService.save(user);
//			return ResultEntity.returnWithoutData(200, "修改introduction成功");
//		} catch (Exception e) {
//			return ResultEntity.returnWithoutData(400, "修改introduction失败");
//		}
//	}
	
	@GetMapping("/my/userInfo")
	public ResultEntity<Map<String, User>> getUserInfo(HttpServletRequest request) {
		Integer id = JwtUtil.getUserIdByToken(request);
		User user = userService.getUserById(id);
		Map<String, User> map = new HashMap<>();
		map.put("user", user);
		
		if (user != null) {
			return ResultEntity.returnWithData(200, "获取个人信息成功", map);
		} else {
			return ResultEntity.returnWithoutData(400, "获取个人信息失败");
		}
	}
}
