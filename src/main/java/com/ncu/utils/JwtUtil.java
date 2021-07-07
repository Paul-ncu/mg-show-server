package com.ncu.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {
	// 设置签名
	public static final String SIGN = "token!zzj#$%xh#$%1997@";

	/**
	 * 生成token的方法
	 * 
	 * @param map payload中携带的参数
	 * @return
	 */
	public static String getToken(Map<String, Integer> map) {
		// 设置过期时间
		Calendar instance = Calendar.getInstance();
		// 设置30天过期
		instance.add(Calendar.DATE, 30);
		Date time = instance.getTime();
		Builder builder = JWT.create();
		// payload自包容携带数据
		map.forEach((k, v) -> {
			builder.withClaim(k, v);
		});
		// 生成token
		String token = builder.withExpiresAt(time).sign(Algorithm.HMAC256(SIGN));
		return token;

	}

	/**
	 * 验证token，算法是否匹配，是否过期，签名是否正确
	 * 
	 * @param token
	 */
	public static void verify(String token) {

		JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);

	}
	/**
	 * 获取token中包含的信息
	 * @param token
	 * @return
	 */
	public static DecodedJWT getTokenInfo(String token) {
		
		DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
		return verify;
	}
	
	public static Integer getUserIdByToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		DecodedJWT tokenInfo = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
		Integer id = tokenInfo.getClaim("id").asInt();
		return id;
	}
}
