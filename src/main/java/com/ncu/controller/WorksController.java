package com.ncu.controller;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.ncu.bean.Photo;
import com.ncu.bean.User;
import com.ncu.bean.Video;
import com.ncu.bean.Works;

import com.ncu.qiniu.QiniuUtil;
import com.ncu.service.CollectionService;
import com.ncu.service.PhotoService;
import com.ncu.service.RedisService;
import com.ncu.service.UserService;
import com.ncu.service.WorksService;
import com.ncu.utils.JwtUtil;
import com.ncu.utils.PageEntity;
import com.ncu.utils.ResultEntity;


@RestController
public class WorksController {

	Logger logger = LoggerFactory.getLogger(WorksController.class);

	@Autowired
	private WorksService worksService;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private UserService userService;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private RedisService redisService;

	@GetMapping("/uptoken")
	public ResultEntity<Map<String, String>> getUptoken() {
		String uptoken = QiniuUtil.getUptoken();
		Map<String, String> map = new HashMap<>();
		map.put("uptoken", uptoken);
		return ResultEntity.returnWithData(200, "获取uptoken成功", map);
	}

	// 保存带图片的works
	@PostMapping("/my/works/photos")
	public ResultEntity<Object> saveWorksOfPhotos(HttpServletRequest request,
			@RequestBody Map<String, Object> dataMap) {
		logger.info(dataMap.toString());

		@SuppressWarnings("unchecked")
		List<Photo> list = (List<Photo>) dataMap.get("photos");
		ObjectMapper mapper = new ObjectMapper();
		List<Photo> photos = mapper.convertValue(list, new TypeReference<List<Photo>>() {
		});
		String introduction = (String) dataMap.get("introduction");
		Integer id = JwtUtil.getUserIdByToken(request);

		User user = userService.getUserById(id);
		Date date = new Date();
		Works works = new Works();
		works.setUser(user);
		works.setIntroduction(introduction);
		works.setCreateTime(date.getTime());
		works.setPhotosOrVideo(true);
		works.setDisplay(true);
		for (Photo photo : photos) {
			photo.setWorks(works);
		}
//			works.setPhotos(photos);
		// 这里要先保存works再保存photo
		try {
			Works saveWorks = worksService.saveWorks(works);
			Long worksId = saveWorks.getId();
			photoService.savePhoto(photos);
			// 保存指定用户的worksId
			redisService.setbit("countOfWorksOfUserId" + id, worksId, true);
			redisService.delete("pageOfWorks");
			redisService.delete("pageOfWorksOfUser");
			return ResultEntity.returnWithoutData(200, "上传成功");
		} catch (Exception e) {
			e.getStackTrace();
			return ResultEntity.returnWithoutData(400, e.getMessage());
		}
//		return ResultEntity.returnWithoutData(200, "hello");
	}

	// 保存带video的works
	@PostMapping("/my/works/video")
	public ResultEntity<Map<String, String>> saveWorksOfVideo(HttpServletRequest request,
			@RequestBody Map<String, Object> dataMap) {

		Integer id = JwtUtil.getUserIdByToken(request);
		// 类型转换
		Object v = dataMap.get("video");
		ObjectMapper mapper = new ObjectMapper();
		Video video = mapper.convertValue(v, new TypeReference<Video>() {
		});
		Photo photo = video.getPhoto();
		String introduction = (String) dataMap.get("introduction");
		User user = userService.getUserById(id);
		Date date = new Date();
		Works works = new Works();
		works.setCreateTime(date.getTime());
		user.setId(id);
		works.setUser(user);
		// 该works为视频
		works.setPhotosOrVideo(false);
		works.setDisplay(true);
		works.setVideo(video);
		works.setIntroduction(introduction);
		try {
			photoService.savePhoto(photo);
			Works saveWorks = worksService.saveWorks(works);
			Long worksId = saveWorks.getId();
			// 保存指定用户的worksId
			redisService.setbit("countOfWorksOfUserId" + id, worksId, true);
			redisService.delete("pageOfWorks");
			redisService.delete("pageOfWorksOfUser");
			return ResultEntity.returnWithoutData(200, "上传成功");
		} catch (Exception e) {
			return ResultEntity.returnWithoutData(400, e.getMessage());
		}

	}

	/**
	 * 获取works，每页20条
	 * 
	 * @param pageNum
	 * @return
	 */
	@GetMapping("/works")
	public ResultEntity<PageEntity<Works>> getAllWorks(@RequestParam Integer pageNum) {
		String result = redisService.hget("pageOfWorks", "worksOfPageNum:" + pageNum);
//		logger.info("hget => " + result);
		ObjectMapper mapper = new ObjectMapper();
		PageEntity<Works> pageEntity = new PageEntity<Works>();
		if ("null".equals(result)) {
			// 从前端传入页面参数
			logger.info("hello1");
			Page<Works> page = worksService.getAllWorks(pageNum);
			logger.info(page.toString());
			// 当前页的内容
			List<Works> worksList = page.getContent();
			for (Works works : worksList) {
				Long id = works.getId();
				String viewCount = redisService.get("viewOfWorksId:" + id);
				if (viewCount == null) {
					works.setView(0L);
				} else {
					Long view = Long.parseLong(viewCount);
					works.setView(view);
				}
			}
			// 当前页的页码
			int number = page.getNumber();
			// 总页数
			int totalPages = page.getTotalPages();
			// 每页显示的条数
			int size = page.getSize();
			// 是否是第一页
			boolean first = page.isFirst();
			// 是否是最后一页
			boolean last = page.isLast();
			
			
			pageEntity.setFirst(first);
			pageEntity.setLast(last);
			pageEntity.setNumber(number);
			pageEntity.setSize(size);
			pageEntity.setTotalPages(totalPages);
			pageEntity.setContentList(worksList);
			
			String pageStr;
			try {
				pageStr = mapper.writeValueAsString(pageEntity);
				logger.info("mapper => " + pageStr);
				// 将数据存入redis
				redisService.hset("pageOfWorks", "worksOfPageNum:" + pageNum, pageStr);
				Duration days = Duration.ofDays(1);
				redisService.hexp("pageOfWorks", days);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {

			try {
				// 将json字符串中的转义符去掉
				String unescapeJava = StringEscapeUtils.unescapeJava(result);
				// 去除首尾的""
				String resultStr = unescapeJava.substring(1, unescapeJava.length() - 1);
				// json转对象
				pageEntity = mapper.readValue(resultStr, PageEntity.class);

			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ResultEntity.returnWithData(200, "获取works成功", pageEntity);
	}

	/**
	 * 获取个人的works
	 * 
	 * @param request
	 * @return
	 */

	@GetMapping("/my/works")
	public ResultEntity<PageEntity<Works>> getWorksByUserId(HttpServletRequest request, @RequestParam Integer pageNum) {
		Integer id = JwtUtil.getUserIdByToken(request);
		String result = redisService.hget("pageOfWorksOfUser", "worksOfPageNum:" + pageNum + "AndUserId:" + id);
		ObjectMapper mapper = new ObjectMapper();
		PageEntity<Works> pageEntity = new PageEntity<Works>();
		if ("null".equals(result)) { // 没有缓存，从数据库中取值
			Page<Works> allWorks = worksService.findWorksByUserId(id, pageNum);
			pageEntity.setContentList(allWorks.getContent());
			pageEntity.setFirst(allWorks.isFirst());
			pageEntity.setLast(allWorks.isLast());
			pageEntity.setNumber(allWorks.getNumber());
			pageEntity.setSize(allWorks.getSize());
			pageEntity.setTotalPages(allWorks.getTotalPages());
			String allWorksStr;
			try {
				allWorksStr = mapper.writeValueAsString(pageEntity);
				// 设置缓存
				redisService.hset("pageOfWorksOfUser", "worksOfPageNum:" + pageNum + "AndUserId:" + id, allWorksStr);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				String unescapeJava = StringEscapeUtils.unescapeJava(result);
				// 去除首尾的""
				String resultStr = unescapeJava.substring(1, unescapeJava.length() - 1);
				// json转对象
				pageEntity = mapper.readValue(resultStr, PageEntity.class);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ResultEntity.returnWithData(200, "获取个人的works成功", pageEntity);

	}

	/**
	 * 获取个人的works数量
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/my/works/count")
	public ResultEntity<Map<String, Long>> getCountOfWorks(HttpServletRequest request) {
		Integer id = JwtUtil.getUserIdByToken(request);
		Long count = redisService.bitCount("countOfWorksOfUserId" + id);
		Map<String, Long> map = new HashMap<>();
		map.put("count", count);
		return ResultEntity.returnWithData(200, "获取个人作品数量", map);
	}

	/**
	 * 判断该works是不是属于用户
	 */
	@GetMapping("/my/works/is")
	public ResultEntity<String> isMyWorks(HttpServletRequest request, @RequestParam Map<String, String> dataMap) {
		Integer id = JwtUtil.getUserIdByToken(request);
		String string = dataMap.get("worksId");
		Long worksId = Long.parseLong(string);
		Boolean getbit = redisService.getbit("countOfWorksOfUserId" + id, worksId);
		if (getbit) {
			return ResultEntity.returnWithoutData(200, "是自己的works");
		} else {
			return ResultEntity.returnWithoutData(400, "不是自己的works");
		}
	}

	/**
	 * 删除我的works
	 * 
	 * @param request
	 * @param dataMap
	 * @return
	 */
	@DeleteMapping("/my/works")
	public ResultEntity<String> deleteMyWorks(HttpServletRequest request, @RequestBody Map<String, String> dataMap) {
		Integer id = JwtUtil.getUserIdByToken(request);
		String string = dataMap.get("worksId");
		Long worksId = Long.parseLong(string);

		Works works = worksService.getWorksByIdAndUserId(id, worksId);
		// 判断该works是否被收藏
		Integer countOfCollection = collectionService.countOfCollection(worksId);
		if (countOfCollection != 0) { // 被收藏
			works.setDisplay(false);
			try {
				redisService.setbit("countOfWorksOfUserId" + id, worksId, false);
				// 删除redis中的缓存
				redisService.delete("worksOfUserId:" + id);
				worksService.saveWorks(works);
				redisService.delete("pageOfWorks");
				return ResultEntity.returnWithoutData(200, "删除成功,works被收藏");
			} catch (Exception e) {
				return ResultEntity.returnWithoutData(400, "删除失败");
			}

		} else { // 没有被收藏，直接删除

			redisService.setbit("countOfWorksOfUserId" + id, worksId, false);
			// 删除redis中的缓存
			redisService.delete("worksOfUserId:" + id);
			redisService.delete("pageOfWorks");
			Integer deleteMyWorks = worksService.deleteMyWorks(id, worksId);

			if (deleteMyWorks == 1) {
				return ResultEntity.returnWithoutData(200, "删除成功,works未被收藏");
			} else {
				return ResultEntity.returnWithoutData(400, "删除失败");
			}
		}

	}

	@PostMapping("/works/view")
	public ResultEntity<String> view(@RequestBody Map<String, String> dataMap) {
		String wId = dataMap.get("worksId");
		Long worksId = Long.parseLong(wId);
		redisService.increment("viewOfWorksId:" + worksId);
		return ResultEntity.returnWithoutData(200, "浏览成功");
	}

}
