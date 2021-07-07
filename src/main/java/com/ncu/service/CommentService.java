package com.ncu.service;



import org.springframework.data.domain.Page;

import com.ncu.bean.Comment;
import com.ncu.utils.PageEntity;
import com.ncu.utils.ReplyEntity;

public interface CommentService {

	Comment saveComment(Comment comment);
	
	PageEntity<Comment> getCommentsListByWorksId(Long worksId, Integer pageNum);
	
	Integer getCountByWorksId(Long worksId);
	
	Comment getReplyComment(Long id);
	
	Page<ReplyEntity> getMyComment(Integer userId, Integer pageNum);

}
