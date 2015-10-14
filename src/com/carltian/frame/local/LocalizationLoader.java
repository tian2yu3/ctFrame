package com.carltian.frame.local;

import java.util.Locale;
import java.util.Map;

/**
 * 本地化数据加载接口
 * 
 * @version 1.0
 * @author Carl Tian
 */
public interface LocalizationLoader {
	/**
	 * 加载本地化数据
	 * 
	 * @return 以区域和本地化字符串代码为索引的本地化字符串数据集
	 */
	public abstract Map<Locale, Map<String, String>> load();
}
