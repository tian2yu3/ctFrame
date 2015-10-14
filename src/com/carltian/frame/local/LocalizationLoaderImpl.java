package com.carltian.frame.local;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 本地化数据加载实现类，允许从Properties文件加载本地化数据。
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class LocalizationLoaderImpl implements LocalizationLoader {

	@Override
	public Map<Locale, Map<String, String>> load() {
		Map<Locale, Map<String, String>> result = new HashMap<Locale, Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		// FIXME 根据构造函数的资源位置参数，从Properties文件加载本地化数据
		result.put(Locale.ROOT, map);
		return result;
	}

}
