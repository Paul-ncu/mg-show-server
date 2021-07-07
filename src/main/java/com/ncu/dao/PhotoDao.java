package com.ncu.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ncu.bean.Photo;

public interface PhotoDao extends JpaRepository<Photo, Long>{

}
