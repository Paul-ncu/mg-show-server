package com.ncu.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ncu.bean.Comment;
import com.ncu.bean.User;
import com.ncu.bean.Works;
import com.ncu.service.CommentService;
import com.ncu.service.RedisService;
import com.ncu.utils.JwtUtil;
import com.ncu.utils.PageEntity;
import com.ncu.utils.ReplyEntity;
import com.ncu.utils.ResultEntity;

@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private RedisService redisService;
	
	@PostMapping("/my/comment")
	public ResultEntity<String> submitComment(HttpServletRequest request, @RequestBody Map<String, String> dataMap) {
		// 用户id
		Integer id = JwtUtil.getUserIdByToken(request);
		// 评论内容
		String content = dataMap.get("content");
		// 父评论id
		String pId = dataMap.get("parentId");
		Long parentId = Long.parseLong(pId);
		Date date = new Date();
		// 评论创建时间
		Long time = date.getTime();
		
		// worksId
		String wId = dataMap.get("worksId");
		Long worksId = Long.parseLong(wId);
		Works works = new Works();
		works.setId(worksId);
		
		User user = new User();
		user.setId(id);
		
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setCreateTime(time);
		comment.setUser(user);
		comment.setWorks(works);
		String userId = dataMap.get("userId");
		Integer uId = Integer.valueOf(userId);
		User replyUser = new User();
		replyUser.setId(uId);
		comment.setReplyUser(replyUser);
		Comment parentComment = new Comment();
		if (parentId != 0L) {
			// 被回复评论的userid
			parentComment.setId(parentId);
		} else {
			parentComment.setId(1L);
		}
		comment.setParentComment(parentComment);
		
		try {
			commentService.saveComment(comment);

			if (uId != id) { // 在缓存中提示用有新评论未读
				redisService.increment("newsOfUserId:" + uId);
			}
			return ResultEntity.returnWithoutData(200, "发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.returnWithoutData(400, "发送失败");
		}
	}
	// 获取一评论
	@GetMapping("/comment")
	public ResultEntity<PageEntity<Comment>> getComments(@RequestParam String worksId, @RequestParam Integer pageNum) {
		Long wId = Long.parseLong(worksId);
		PageEntity<Comment> pageEntity = commentService.getCommentsListByWorksId(wId, pageNum);
		return ResultEntity.returnWithData(200, "获取评论成功", pageEntity);
	}
	
	// 获取二级评论
	@GetMapping("/comment/reply")
	public ResultEntity<Map<String, Comment>> getReplyComments(@RequestParam String parentId) {
		Long pId = Long.parseLong(parentId);
		Comment replyComment = commentService.getReplyComment(pId);
		Map<String, Comment> map = new HashMap<>();
		map.put("comment", replyComment);
		return ResultEntity.returnWithData(200, "获取回复成功", map);
	}
	
	@GetMapping("/comment/count")
	public ResultEntity<Map<String, Integer>> countOfComment(@RequestParam String worksId) {
		Long wId = Long.parseLong(worksId);
		Integer countByWorksId = commentService.getCountByWorksId(wId);
		Map<String, Integer> map = new HashMap<>();
		map.put("count", countByWorksId);
		return ResultEntity.returnWithData(200, "获取评论数", map);
	}
	
	@GetMapping("/my/comment/reply")
	public ResultEntity<PageEntity<ReplyEntity>> getMyReply(HttpServletRequest request, @RequestParam Integer pageNum) {
		Integer id = JwtUtil.getUserIdByToken(request);
		PageEntity<ReplyEntity> pageEntity = new PageEntity<ReplyEntity>();

		Page<ReplyEntity> myComment = commentService.getMyComment(id, pageNum);
		pageEntity.setContentList(myComment.getContent());
		pageEntity.setFirst(myComment.isFirst());
		pageEntity.setLast(myComment.isLast());
		pageEntity.setNumber(myComment.getNumber());
		pageEntity.setSize(myComment.getSize());
		pageEntity.setTotalPages(myComment.getTotalPages());

		return ResultEntity.returnWithData(200, "获取评论回复", pageEntity);
	}
	
	@GetMapping("/my/comment/news")
	public ResultEntity<Map<String, Integer>> haveNews(HttpServletRequest request) {
		Integer id = JwtUtil.getUserIdByToken(request);
		String haveNews = redisService.get("newsOfUserId:" + id);
		Map<String, Integer> map = new HashMap<>();
		if (haveNews == null) {
			map.put("count", 0);
		} else {
			Integer valueOf = Integer.valueOf(haveNews);
			map.put("count", valueOf);
		}
		return ResultEntity.returnWithData(200, "新消息提示", map);
	}
	
	@DeleteMapping("/my/comment/news")
	public ResultEntity<String> cancelNews(HttpServletRequest request) {
		Integer id = JwtUtil.getUserIdByToken(request);
		redisService.delete("newsOfUserId:" + id);
		return ResultEntity.returnWithoutData(200, "清空消息提示");
	}
}
