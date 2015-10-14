package com.carltian.frame.local;

import java.util.Locale;

/**
 * 本地化管理器接口
 * 
 * @version 1.0
 * @author Carl Tian
 */
public interface LocalizationManager {

	/**
	 * 获取当前Request设定语言的本地化字符串
	 * 
	 * @param code
	 *           本地化字符串编号
	 * @return 本地化字符串
	 */
	public abstract String get(String code);

	/**
	 * 获取当前Request设定语言的本地化字符串
	 * 
	 * @param code
	 *           本地化字符串编号
	 * @param params
	 *           本地化字符串参数列表
	 * @return 本地化字符串
	 */
	public abstract String get(String code, Object[] params);

	/**
	 * 获取指定语言的本地化字符串
	 * 
	 * @param code
	 *           本地化字符串编号
	 * @param lang
	 *           区域
	 * @return 本地化字符串
	 */
	public abstract String get(String code, Locale lang);

	/**
	 * 获取指定语言的本地化字符串
	 * 
	 * @param code
	 *           本地化字符串编号
	 * @param params
	 *           本地化字符串参数列表
	 * @param locale
	 *           区域
	 * @return 本地化字符串
	 */
	public abstract String get(String code, Object[] params, Locale locale);

}
