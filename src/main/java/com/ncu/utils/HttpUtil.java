package com.ncu.utils;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.http.HttpStatus;
/**
 * 用于发起请求的工具类
 * @author xiaohao
 *
 */
public class HttpUtil {

	/**
	 * 
	 * @param urlPath 请求地址
	 * @param params 请求参数，用map的形式，key为参数名，value为参数值
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlPath, Map<String, String> params) throws Exception {
		
		StringBuffer sb = new StringBuffer(urlPath);
		if (params != null && !params.isEmpty()) {
			sb.append("?");
			Set<Entry<String,String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				String key = entry.getKey();
				String value = "";
				if (entry.getValue() != null) {
					value = entry.getValue();
					value = URLEncoder.encode(value, "UTF-8");
				}
				sb.append(key).append("=").append(value).append("&");
			}
			// 删除最后一个"&"
			sb.deleteCharAt(sb.length() - 1);
		}
		
		URL url = new URL(sb.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置连接超时
		conn.setConnectTimeout(5000);
		// 设置请求方法
		conn.setRequestMethod("GET");
		// 获取响应状态吗
		int responseCode = conn.getResponseCode();
		if (responseCode == HttpStatus.OK.value()) {
			// 获取连接响应的输入流
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuffer sbs = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sbs.append(line);
			}
			return sbs.toString();
		}
		return null;
	}
}
