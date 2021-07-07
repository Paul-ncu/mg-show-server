package com.ncu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import com.ncu.bean.User;
import com.ncu.dao.CommentDao;
import com.ncu.qiniu.QiniuUtil;
import com.ncu.service.CommentService;
import com.ncu.service.RedisService;
import com.ncu.service.UserService;
import com.ncu.service.WorksService;
import com.ncu.utils.ReplyEntity;

@SpringBootTest
class MyGirlApplicationTests {
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	UserService userService;
	
	@Autowired
	WorksService worksService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	CommentService commentService;
	
	
	@Test
	void contextLoads() throws SQLException {
		Connection conn = dataSource.getConnection();
		System.out.println(conn);
	}
	
	@Test
	public void getUserByopenId() {
		User userByOpenid = userService.getUserByOpenid("o_kWc5WiSKSpjKsesjDGo5Iv-a7");
		System.out.println(userByOpenid);
	}
	
	@Test
	public void getUptoken() {
		String uptoken = QiniuUtil.getUptoken();
		System.out.println(uptoken);
	}
	
	@Test
	public void delete() {
		Integer count = worksService.deleteMyWorks(1, 9L);
		System.out.println(count);
	}
	
	@Test
	public void connectToRedis() {
		Boolean flag = false;
		if (flag) {
			System.out.println("hello world");
		} else {
			System.out.println("false");
		}
	}
	
	@Test
	public void increment() {
		Long increment = redisService.increment("test");
		System.out.println(increment);
	}
	
	@Test
	public void bit() {
		redisService.setbit("xiaohao", 0L, false);
		
		Long bitCount = redisService.bitCount("xiaohao");
		System.out.println(bitCount);
	}
	
	@Test
	public void get() {
		String count = redisService.get("countOfCollectionOfUserId:" + 1);
		System.out.println(count);
	}
	
	@Test
	public void hset() {
		redisService.hset("hest", "hello", "11");
	}
	
	@Test
	public void getReply() {
		Page<ReplyEntity> myComment = commentService.getMyComment(1, 0);
		int totalPages = myComment.getTotalPages();
		System.out.println(totalPages);
		List<ReplyEntity> content = myComment.getContent();
		for (ReplyEntity replyEntity : content) {
			System.out.println(replyEntity);
		}
		System.out.println(myComment.toString());
	}
}
