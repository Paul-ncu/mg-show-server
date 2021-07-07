package com.ncu.service.impl;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;



import com.ncu.bean.Works;

import com.ncu.dao.WorksDao;
import com.ncu.service.WorksService;

@Service
public class WorksServiceImpl implements WorksService {

	@Autowired
	private WorksDao worksDao;

	Logger logger = LoggerFactory.getLogger(WorksServiceImpl.class);

	@Override
	@Transactional
	public Works saveWorks(Works works) {
		Works save = worksDao.save(works);
		return save;
	}

	@Override
	public Page<Works> findWorksByUserId(Integer id, Integer pageNum) {

		Specification<Works> spec = new Specification<Works>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Works> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				Path<Object> user = root.get("user");
				Path<Object> userId = user.get("id");
				Path<Object> display = root.get("display");
				Predicate equal1 = criteriaBuilder.equal(userId, id);
				Predicate equal2 = criteriaBuilder.equal(display, true);
				Predicate predicate = criteriaBuilder.and(equal1, equal2);
				return predicate;
			}

		};
		Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
		PageRequest page = PageRequest.of(pageNum, 10, sort);		
		Page<Works> findAll = worksDao.findAll(spec, page);
		return findAll;
	}

	@Override
	public Page<Works> getAllWorks(Integer pageNum) {

		Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
		Pageable pageable = PageRequest.of(pageNum, 10, sort);
		Specification<Works> spec = new Specification<Works>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Works> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				Path<Object> display = root.get("display");
				Predicate predicate = criteriaBuilder.equal(display, true);
				return predicate;
			}
		};

		Page<Works> findAll = worksDao.findAll(spec, pageable);

		return findAll;

	}

	@Override
	public Works getWorksById(Long id) {
		Works works = worksDao.getOne(id);
		return works;
	}

	@Override
	public Integer getCountOfMyWorks(Integer userId) {
		Integer count = worksDao.countByUserId(userId);
		return count;
	}

	@Override
	public Integer isMyWorks(Integer userId, Long worksId) {
		Integer count = worksDao.countByIdAndUserId(worksId, userId);
		return count;
	}

	@Transactional
	@Override
	public Integer deleteMyWorks(Integer userId, Long worksId) {
		Integer count = worksDao.deleteByIdAndUserId(worksId, userId);
		return count;
	}

	@Override
	public Works getWorksByIdAndUserId(Integer userId, Long worksId) {
		Works works = worksDao.findByIdAndUserId(worksId, userId);
		return works;
	}

	@Override
	public Long getViewOfWorks(Long worksId) {
		// TODO Auto-generated method stub
		return null;
	}

}
