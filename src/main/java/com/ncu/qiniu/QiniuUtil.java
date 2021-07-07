package com.ncu.qiniu;

import com.qiniu.util.Auth;

public class QiniuUtil {
	
	public static String getUptoken() {
		String accessKey = "62PRI4BzVmqSqNsT_oEcbxCweKb_x0dKnLG0y5pw";
		String secretKey = "YX30xhjzufAICvzXRDPSZ6n03W1YbEL1Ius3dLLd";
		String bucket = "zzj-works";
		// String key = "file key";
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket);
		return upToken;
	}
}
