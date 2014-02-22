package com.xkwallpaper.util;

import com.alibaba.fastjson.JSON;

public class JsonUtil {

	public static Object jsonToObject(String json, Class cla) {
		Object ob = JSON.parseObject(json, cla);
		return ob;
	}
}
