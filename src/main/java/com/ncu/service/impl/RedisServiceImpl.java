package com.ncu.service.impl;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncu.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService{
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	/**
	 * set方法
	 */
	@Override
	public void set(String key, String value) {
		stringRedisTemplate.opsForValue().set(key, value);
	}
	
	

	/**
	 * get方法
	 */
	@Override
	public String get(String key) {
		String value = stringRedisTemplate.opsForValue().get(key);
		return value;
	}

	/**
	 * 设置指定偏移量上的值
	 */
	@Override
	public void setbit(String key, Long offset, boolean value) {
		stringRedisTemplate.opsForValue().setBit(key, offset, value);
	}

	/**
	 * 获取指定偏移量上的值
	 */
	@Override
	public Boolean getbit(String key, Long offset) {
		Boolean bit = stringRedisTemplate.opsForValue().getBit(key, offset);
		return bit;
	}

	/**
	 * 自增，步长为1
	 */
	@Override
	public Long increment(String key) {
		Long increment = stringRedisTemplate.opsForValue().increment(key);
		return increment;
	}

	/**
	 * 封装的bitCount函数
	 */
	@Override
	public Long bitCount(String key) {
		Long result = stringRedisTemplate.execute(new RedisCallback<Long>() {

			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				Long bitCount = connection.bitCount(key.getBytes());
				return bitCount;
			}
			
		});
		return result;
	}

	/**
	 * 递减方法
	 */
	@Override
	public Long decrement(String key) {
		Long decrement = stringRedisTemplate.opsForValue().decrement(key);
		return decrement;
	}

	/**
	 * 删除key
	 */
	@Override
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}

	/**
	 * 批量set
	 */
	@Override
	public void multiSet(Map<String, String> map) {
		stringRedisTemplate.opsForValue().multiSet(map);
	}


	/**
	 * 批量hset
	 */
	@Override
	public void hmset(String key, Map<String, Object> map) {
		stringRedisTemplate.opsForHash().putAll(key, map);
	}


	/**
	 * 批量hget
	 */
	@Override
	public List<Object> hmget(String key, Collection<Object> hashKeys) {
		List<Object> multiGet = stringRedisTemplate.opsForHash().multiGet(key, hashKeys);
		return multiGet;
	}


	/**
	 * hget
	 */
	@Override
	public String hget(String key, String hashKey) {
		Object object = stringRedisTemplate.opsForHash().get(key, hashKey);
		ObjectMapper mapper = new ObjectMapper();
		String result = "null";
		try {
			result = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * hset
	 */
	@Override
	public void hset(String key, String hashKey, String value) {
		stringRedisTemplate.opsForHash().put(key, hashKey, value);
	}


	/**
	 * hash设置过期时间
	 */
	@Override
	public void hexp(String key, Duration timeout) {
		stringRedisTemplate.boundHashOps(key).expire(timeout);
		
	}

}
