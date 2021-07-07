package com.ncu.utils;

public class ResultEntity<T> {
	
	private Integer code;
	private String message;
	private T data;
	
	public ResultEntity(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public ResultEntity(Integer code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static <T> ResultEntity<T> returnWithoutData(Integer code, String message) {
		ResultEntity<T> resultEntity = new ResultEntity<T>(code, message);
		return resultEntity;
	}
	
	public static <T> ResultEntity<T> returnWithData(Integer code, String message, T data){
		ResultEntity<T> resultEntity = new ResultEntity<T>(code, message, data);
		return resultEntity;
	}

}
