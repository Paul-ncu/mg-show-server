package com.ncu.interceptor;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncu.utils.JwtUtil;

public class JwtInterceptor implements HandlerInterceptor{
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		 HashMap<String, Object> map = new HashMap<>();
	        //获取请求头中的令牌
	        String token = request.getHeader("Authorization");
	        try {
	            JwtUtil.verify(token);
	            return true;
	        } catch (SignatureVerificationException e) {
	            e.printStackTrace();
	            map.put("msg", "无效签名");
	        } catch (TokenExpiredException e) {
	            e.printStackTrace();
	            map.put("msg", "token过期！");
	        } catch (AlgorithmMismatchException e) {
	            e.printStackTrace();
	            map.put("msg", "token算法不一致！");
	        } catch (Exception e) {
	            e.printStackTrace();
	            map.put("msg", "token无效！！！");
	        }
	        map.put("state", false);//设置状态
	        //将map 转为json Jackson
	        String s = new ObjectMapper().writeValueAsString(map);
	        response.setContentType("application/json;charset=UTF-8");
	        response.getWriter().println(s);
	        return false;
	}
	
}
