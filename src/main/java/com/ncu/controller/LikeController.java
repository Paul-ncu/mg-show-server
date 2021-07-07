package com.ncu.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ncu.bean.Like;
import com.ncu.bean.User;
import com.ncu.bean.Works;
import com.ncu.service.LikeService;
import com.ncu.service.RedisService;
import com.ncu.utils.JwtUtil;
import com.ncu.utils.ResultEntity;

@RestController
public class LikeController {

	@Autowired
	private LikeService likeService;
	
	@Autowired
	private RedisService redisService;
	
	Logger logger = LoggerFactory.getLogger(LikeController.class);
	
	/**
	 * 确认点赞
	 * @param request
	 * @param dataMap
	 * @return
	 */
	@PostMapping("/my/like/confirm")
	public ResultEntity<String> confirmLike(HttpServletRequest request, @RequestBody Map<String, String> dataMap) {
		//  获取请求头中的id
		try {
			Integer id = JwtUtil.getUserIdByToken(request);
			Long idLong = id.longValue();
			// 点赞works的id
			String wId = dataMap.get("worksId");
			Long worksId = Long.parseLong(wId);
			// 将works的点赞缓存到rdis中
			redisService.setbit("likeOfWorksId:" + worksId, idLong, true);
			Like like = new Like();
			User user = new User();
			user.setId(id);
			Works works = new Works();
			works.setId(worksId);
			Date date = new Date();
			Long createTime = date.getTime();
			like.setUser(user);
			like.setWorks(works);
			like.setCreateTime(createTime);
			likeService.confirmLike(like);
			
			return ResultEntity.returnWithoutData(200, "点赞成功");
		} catch (Exception e) {
			return ResultEntity.returnWithoutData(400, e.getMessage());
		}
	}
	
	/**
	 * 用户取消点赞
	 * @param request
	 * @param dataMap
	 * @return
	 */
	@DeleteMapping("/my/like/cancel")
	public ResultEntity<Map<String, Integer>> cancelLike(HttpServletRequest request, @RequestBody Map<String, Long> dataMap) {
		logger.info(dataMap.toString());
		Integer id = JwtUtil.getUserIdByToken(request);
		Long idLong = id.longValue();
		// 点赞works的id
		Long worksId = dataMap.get("worksId");
		Integer count = likeService.cancelLike(id, worksId);
		redisService.setbit("likeOfWorksId:" + worksId, idLong, false);
		Map<String, Integer> map = new HashMap<>();
		map.put("count", count);
		if (count == 1) {
			return ResultEntity.returnWithoutData(200, "取消点赞成功");
		} else {
			return ResultEntity.returnWithoutData(400, "取消点赞失败");
		}
		
	}
	
	/**
	 * 查看用户是否对该works点赞
	 * @param request
	 * @param dataMap
	 * @return
	 */
	@GetMapping("/my/like")
	public ResultEntity<String> isLike(HttpServletRequest request, @RequestParam Map<String, String> dataMap) {
		Integer id = JwtUtil.getUserIdByToken(request);
		Long idLong = id.longValue();
		// 点赞works的id
		String worksIdStr = dataMap.get("worksId");
		Long worksId = Long.parseLong(worksIdStr);
		Boolean getbit = redisService.getbit("likeOfWorksId:" + worksId, idLong);
		if (getbit) {
			return ResultEntity.returnWithoutData(200, "获取是否点赞");
		} else {
			return ResultEntity.returnWithoutData(400, "获取是否点赞失败");
		}
	}
	
	
	/**
	 * 获取单个works的点赞数
	 * @param dataMap
	 * @return
	 */
	@GetMapping("/like/count/works")
	public ResultEntity<Map<String, Long>> getCountOfLike(@RequestParam Map<String, String> dataMap) {
		String worksIdStr = dataMap.get("worksId");
		Long worksId = Long.parseLong(worksIdStr);
		Long count = redisService.bitCount("likeOfWorksId:" + worksId);
		Map<String, Long> map = new HashMap<>();
		map.put("count", count);
		return ResultEntity.returnWithData(200, "获取点赞数量", map);
	}
	
	/**
	 * 获取个人的所有点赞数
	 * @param request
	 * @return
	 */
	@GetMapping("/my/like/count")
	public ResultEntity<Map<String, Integer>> getCountOflikeOfUser(HttpServletRequest request) {
		Integer id = JwtUtil.getUserIdByToken(request);
		Integer count = likeService.getCountByUserId(id);
		Map<String, Integer> map = new HashMap<>();
		map.put("count", count);
		return ResultEntity.returnWithData(200, "获取个人的总点赞数", map);
	}
}
