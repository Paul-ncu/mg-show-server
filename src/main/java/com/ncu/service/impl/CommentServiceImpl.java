package com.ncu.service.impl;

import java.util.ArrayList;

import java.util.List;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ncu.bean.Comment;

import com.ncu.dao.CommentDao;
import com.ncu.service.CommentService;
import com.ncu.utils.PageEntity;
import com.ncu.utils.ReplyEntity;

@Service
public class CommentServiceImpl implements CommentService{

	@Autowired
	CommentDao commentDao;
	
	
	@Transactional
	@Override
	public Comment saveComment(Comment comment) {
		Comment save = commentDao.save(comment);
		return save;
	}

	/**
	 * 获取父节点下的所有子节点
	 */
	@Override
	public PageEntity<Comment> getCommentsListByWorksId(Long worksId, Integer pageNum) {
		// 获取指定works中所有父评论节点
		Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
		Pageable pageable = PageRequest.of(pageNum, 10, sort);
		Page<Comment> page = commentDao.findByWorksIdAndParentCommentId(worksId, 1L, pageable);
		List<Comment> parentComment = page.getContent();
		for (Comment comment : parentComment) {
			getRely(comment);
			// 获取父评论id
			Long parentId = comment.getId();
			List<Comment> replyComment = comment.getReplyComment();
			if (replyComment != null) {
				// 对二级评论遍历
				for (Comment comment2 : replyComment) {
					Long id = comment2.getParentComment().getId();
					// 如果二级评论的父评论为一级评论，把reply设置为null
					if (id == parentId) {
						comment2.setReplyUser(null);
					}
				}
			}
			
		}
		PageEntity<Comment> pageEntity = new PageEntity<Comment>();
		pageEntity.setContentList(parentComment);
		pageEntity.setFirst(page.isFirst());
		pageEntity.setLast(page.isLast());
		pageEntity.setNumber(page.getNumber());
		pageEntity.setSize(page.getSize());
		pageEntity.setTotalPages(page.getTotalPages());
		return pageEntity;
	}
	
	// 用于暂时存放一个父评论的所有子评论
	private List<Comment> temp = new ArrayList<>();
	
	/**
	 * 将一个节点下面的所有节点放到tempList中
	 * @param parentComment
	 */
	private void recursion(Comment parentComment) {
		temp.add(parentComment);
		List<Comment> replyComment = parentComment.getReplyComment();
		if (replyComment == null) { // 如果一个节点没有子评论，
			return;
		}
		for (Comment comment : replyComment) {
			recursion(comment);
		}
	}
	
	private void getRely(Comment parentComment) {
		List<Comment> replyComment = parentComment.getReplyComment();
		if (replyComment != null) {
			for (Comment comment : replyComment) {
				recursion(comment);
			}
			parentComment.setReplyComment(temp);
			
			temp = new ArrayList<>();
		}
	}

	@Override
	public Integer getCountByWorksId(Long worksId) {
		Integer countByWorksId = commentDao.countByWorksId(worksId);
		return countByWorksId;
	}

	@Override
	public Comment getReplyComment(Long id) {
		Comment comment = commentDao.getOne(id);
		getRely(comment);
		// 获取父评论id
		Long parentId = comment.getId();
		List<Comment> replyComment = comment.getReplyComment();
		if (replyComment != null) {
			// 对二级评论遍历
			for (Comment comment2 : replyComment) {
				Long rid = comment2.getParentComment().getId();
				// 如果二级评论的父评论为一级评论，把reply设置为null
				if (rid == parentId) {
					comment2.setReplyUser(null);
				}
			}
		}
		return comment;
	}

	@Override
	public Page<ReplyEntity> getMyComment(Integer userId, Integer pageNum) {

		Pageable pageable = PageRequest.of(pageNum, 15);
		Page<ReplyEntity> reply = commentDao.getReply(userId, pageable);
		return reply;
	}

	

}
