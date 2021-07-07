package com.ncu.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ncu.bean.Comment;
import com.ncu.utils.ReplyEntity;

public interface CommentDao extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment>{

	List<Comment> findByWorksId(Long worksId);
	
	Page<Comment> findByWorksIdAndParentCommentId(Long worksId, Long pId, Pageable pageable);
	
	Integer countByWorksId(Long worksId);
	
	@Query("Select new com.ncu.utils.ReplyEntity(c, c.parentComment, c.works) From Comment c Where c.replyUser.id = ?1 And c.user.id != ?1 Order By c.createTime Desc")
	Page<ReplyEntity> getReply(Integer userId, Pageable pageable);
}
