package com.ncu.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncu.bean.Collection;
import com.ncu.bean.User;
import com.ncu.bean.Works;
import com.ncu.service.CollectionService;
import com.ncu.service.RedisService;
import com.ncu.utils.JwtUtil;
import com.ncu.utils.ResultEntity;


@RestController
public class CollectionController {

	Logger logger = LoggerFactory.getLogger(CollectionController.class);
	
	@Autowired
	private CollectionService collectionService;

	@Autowired
	private RedisService redisService;
	
	/**
	 * 收藏works
	 * @param request
	 * @param dataMap
	 * @return
	 */
	@PostMapping("/my/collection/save")
	public ResultEntity<String> saveCollection(HttpServletRequest request, @RequestBody Map<String, Object> dataMap) {
		
		Integer id = JwtUtil.getUserIdByToken(request);
		Long idLong = id.longValue();
		User user = new User();
		user.setId(id);
		Integer w = (Integer) dataMap.get("worksId");
		Long worksId = Integer.toUnsignedLong(w);
		
		Works works = new Works();
		works.setId(worksId);
		Date date = new Date();
		Long createTime = date.getTime();
		Collection collection = new Collection();
		collection.setWorks(works);
		collection.setUser(user);
		collection.setCreateTime(createTime);
		try {
			// 在redis中缓存每个用户是否收藏，使用bitmap进行记录
			redisService.setbit("collectionOfWorksId:" + worksId, idLong, true);
			// 记录用户的收藏的works的总数
			redisService.increment("countOfCollectionOfUserId:" + id);
			// 当用更新收藏时，删除之前的缓存
			redisService.delete("collectionOfUserId:" + id);
			collectionService.saveCollection(collection);
			return ResultEntity.returnWithoutData(200, "收藏成功");
		} catch(Exception e) {
			return ResultEntity.returnWithoutData(400, "收藏失败");
		}
	}
	
	/**
	 * 取消收藏
	 * @param request
	 * @param dataMap
	 * @return
	 */
	@DeleteMapping("/my/collection/cancel")
	public ResultEntity<String> cancelCollection(HttpServletRequest request, @RequestBody Map<String, Object> dataMap) {
		Integer id = JwtUtil.getUserIdByToken(request);
		Long idLong = id.longValue();
		Integer w = (Integer) dataMap.get("worksId");
		Long worksId = Integer.toUnsignedLong(w);
		redisService.decrement("countOfCollectionOfUserId:" + id);
		// 对redis就行更新
		redisService.setbit("collectionOfWorkdId:" + worksId, idLong, false);
		// 删除缓存，用户收藏的works
		redisService.delete("collectionOfUserId:" + id);
		Integer count = collectionService.cencelCollection(id, worksId);
		if (count == 1) {
			return ResultEntity.returnWithoutData(200, "取消收藏成功");
		} else {
			return ResultEntity.returnWithoutData(400, "取消收藏失败");
		}
	}
	
	/**
	 * 查看用户是否收藏了该works
	 * @param request
	 * @param dataMap
	 * @return
	 */
	@GetMapping("/my/collection")
	public ResultEntity<Map<String, Integer>> isCollection(HttpServletRequest request, @RequestParam Map<String, String> dataMap) {
		Integer id = JwtUtil.getUserIdByToken(request);
		Long idLong = id.longValue();
		String wId = dataMap.get("worksId");
		Long worksId = Long.parseLong(wId);
		Boolean getbit = redisService.getbit("collectionOfWorksId:" + worksId, idLong);
		
		if (getbit) {
			return ResultEntity.returnWithoutData(200, "用户已收藏");
		} else {
			return ResultEntity.returnWithoutData(400, "用户未收藏");
		}
		
	}
	
	/**
	 * 获取指定works被收藏的数量
	 * @param dataMap
	 * @return
	 */
	@GetMapping("/collection/count/works")
	public ResultEntity<Map<String, Long>> countOfCollection(@RequestParam Map<String, String> dataMap) {
		String wId = dataMap.get("worksId");
		Long worksId = Long.parseLong(wId);
		Long count = redisService.bitCount("collectionOfWorksId:" + worksId);
		Map<String, Long> map = new HashMap<>();
		map.put("count", count);
		return ResultEntity.returnWithData(200, "获取works的收藏数", map);
	}
	
	/**
	 * 获取我的收藏的数量
	 * @param request
	 * @return
	 */
	@GetMapping("/my/collection/count")
	public ResultEntity<Map<String, Integer>> countOfMyCollection(HttpServletRequest request) {
		Integer id = JwtUtil.getUserIdByToken(request);
		String count = redisService.get("countOfCollectionOfUserId:" + id);
		logger.info(count);
		Map<String, Integer> map = new HashMap<>();

		map.put("count", count == null ? 0 : Integer.valueOf(count));
		return ResultEntity.returnWithData(200, "获取的我所有收藏数量", map);
	}
	
	/**
	 * 获取个人的所有collction
	 * @param request
	 * @return
	 */
	@GetMapping("/my/collection/all")
	public ResultEntity<Map<String, List<Collection>>> getMyCollection(HttpServletRequest request) {
		Integer id = JwtUtil.getUserIdByToken(request);
		// 将用户的收藏缓存到redis
		String resultStr = redisService.get("collectionOfUserId:" + id);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, List<Collection>> map = new HashMap<>();
		List<Collection> collections;
		if (resultStr == null) { // 用户未缓存，从数据库中获取数据
			collections = collectionService.findMyCollection(id);
			String collectiosStr;
			try {
				collectiosStr = mapper.writeValueAsString(collections);
				redisService.set("collectionOfUserId:" + id, collectiosStr);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			map.put("collections", collections);
			
		} else { // 从缓存中获取数据
			try {
				collections = mapper.readValue(resultStr, new TypeReference<List<Collection>>() {});
				map.put("collections", collections);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ResultEntity.returnWithData(200, "获得所有的收藏", map);
		
	}
	
	/**
	 * 在个人主页中删除收藏
	 * @param request
	 * @param dataMap
	 * @return
	 */
	@DeleteMapping("/my/collection/delete")
	public ResultEntity<String> deleteCollection(HttpServletRequest request, @RequestBody Map<String, Integer> dataMap) {
		Integer id = JwtUtil.getUserIdByToken(request);
		Integer cId = dataMap.get("cId");
		// 删除缓存
		redisService.delete("collectionOfUserId:" + id);
		// 个人收藏总数减一
		redisService.decrement("countOfCollectionOfUserId:" + id);
		Integer count = collectionService.deleteCollection(cId, id);
		if (count == 1) {
			return ResultEntity.returnWithoutData(200, "删除收藏成功");
		} else {
			return ResultEntity.returnWithoutData(400, "删除失败");
		}
	}
}
