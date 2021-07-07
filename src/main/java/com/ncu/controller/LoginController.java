package com.ncu.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncu.bean.User;
import com.ncu.service.UserService;
import com.ncu.utils.HttpUtil;
import com.ncu.utils.JwtUtil;
import com.ncu.utils.OpenidAndSession_key;
import com.ncu.utils.ResultEntity;

@RestController
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	private final String APPID = "your appid";
	private final String APPSECRET = "your appsecret";

	/**
	 * 接收微信客户端发送过来的code，获取用户唯一标识openid
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/user/login")
	public ResultEntity<Map<String, String>> login(@RequestBody Map<String, String> dataMap) throws Exception {
		String code = dataMap.get("code");
		String nickName = dataMap.get("nickName");
		String avatarUrl = dataMap.get("avatarUrl");
		String urlStr = "https://api.weixin.qq.com/sns/jscode2session";
		Map<String, String> params = new HashMap<>();
		params.put("appid", APPID);
		params.put("secret", APPSECRET);
		params.put("js_code", code);
		params.put("grant_type", "authorization_code");
		ObjectMapper mapper = new ObjectMapper();
		// 向微信小程序接口申请openid和session_key
		String openidAndSession_key = HttpUtil.doGet(urlStr, params);
		try {
			// 将JSON字符串转换为对象
			OpenidAndSession_key result = mapper.readValue(openidAndSession_key, OpenidAndSession_key.class);
			// 获取用户openid
			String openid = result.getOpenid();
			// 从数据库中查询是否有该用户，如果数据库中没有该用户
			User userByOpenid = userService.getUserByOpenid(openid);
			// 用户id，需要将id放入token中
			Integer id;
			// 用于存放token中的playload
			Map<String, Integer> map = new HashMap<>();
			// 将用户信息存入数据库
			if (userByOpenid == null) { // 新用户登录
				User user = new User();
				user.setOpenid(openid);
				user.setNickName(nickName);
				// 默认username === nickName
				user.setUsername(nickName);
				user.setAvatarUrl(avatarUrl);
				User savedUser = userService.save(user);
				// 获取用户的id
				id = savedUser.getId();
				// 将用户id放到token中
				map.put("id", id);
			} else { // 如果数据库中存在该用户，获取用户id，重新生成token
				id = userByOpenid.getId();
				map.put("id", id);
			}
			String token = JwtUtil.getToken(map);
			Map<String, String> resultMap = new HashMap<>();
			resultMap.put("token", token);
			return ResultEntity.returnWithData(200, "获取token成功", resultMap);
		} catch (Exception e) {
			return ResultEntity.returnWithoutData(400, e.getMessage());
		}

	}
}
