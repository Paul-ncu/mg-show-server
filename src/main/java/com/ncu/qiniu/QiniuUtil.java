package com.ncu.qiniu;

import com.qiniu.util.Auth;

public class QiniuUtil {
	
	public static String getUptoken() {
		String accessKey = "your accessKey";
		String secretKey = "your secretkey";
		String bucket = "zzj-works";
		// String key = "file key";
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket);
		return upToken;
	}
}
