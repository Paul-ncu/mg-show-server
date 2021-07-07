package com.ncu.service;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RedisService {

	void set(String key, String value);
	
	String get(String key);
	
	void setbit(String key, Long offset, boolean value);
	
	Boolean getbit(String key, Long offset);
	
	Long increment(String key);
	
	Long decrement(String key);
	
	Long bitCount(String key);
	
	void delete(String key);
	
	void multiSet(Map<String, String> map);
	
	void hmset(String key, Map<String, Object> map);
	
	List<Object> hmget(String key, Collection<Object> hashKeys); 
	
	String hget(String key, String hashKey);
	
	void hset(String key, String hashKey, String value);
	
	void hexp(String key, Duration timeout);
}
