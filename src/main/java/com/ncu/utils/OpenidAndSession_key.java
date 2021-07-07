package com.ncu.utils;

public class OpenidAndSession_key {

	private String openid;
	private String session_key;
	public OpenidAndSession_key() {
		super();
	}
	public OpenidAndSession_key(String openid, String session_key) {
		super();
		this.openid = openid;
		this.session_key = session_key;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getSession_key() {
		return session_key;
	}
	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}
	@Override
	public String toString() {
		return "OpenidAndSession_key [openid=" + openid + ", session_key=" + session_key + "]";
	}
	
	
}
