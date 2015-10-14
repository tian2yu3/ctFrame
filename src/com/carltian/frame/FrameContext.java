package com.carltian.frame;

import java.util.Locale;

import org.atmosphere.cpr.AtmosphereServlet;

import com.carltian.frame.container.Container;

/**
 * 保存框架上下文环境，在此类中保存的参数都是与线程无关的全局参数
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class FrameContext {
	static private volatile String localeAttrName = "__APPFRAME_LOCALE__";
	static private volatile Locale defaultLocale = null;
	static private volatile String requestFilterName = null;
	static private volatile boolean supportPushEvent = false;
	static private volatile String remoteServletName = null;
	static private volatile String remotePathName = null;
	static private volatile Container container = null;

	/**
	 * 获取默认区域/语言信息
	 * 
	 * @return 默认区域/语言信息
	 */
	public static Locale getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * 设置默认区域/语言信息
	 * 
	 * @param defaultLocale
	 *           默认区域/语言信息
	 */
	public static void setDefaultLocale(Locale defaultLocale) {
		FrameContext.defaultLocale = defaultLocale;
	}

	/**
	 * 获取{@link RequestFilter}的注册名称
	 * 
	 * @return {@link RequestFilter}的注册名称
	 */
	public static String getRequestFilterName() {
		return requestFilterName;
	}

	/**
	 * 设置{@link RequestFilter}的注册名称
	 * 
	 * @param requestFilterName
	 *           {@link RequestFilter}的注册名称
	 */
	static void setRequestFilterName(String requestFilterName) {
		FrameContext.requestFilterName = requestFilterName;
	}

	/**
	 * 获取远程服务监听器{@link AtmosphereServlet}的注册名称
	 * 
	 * @return 远程服务监听器{@link RequestFilter}的注册名称
	 */
	public static String getRemoteServletName() {
		return remoteServletName;
	}

	/**
	 * 设置远程服务监听器{@link AtmosphereServlet}的注册名称
	 * 
	 * @param remoteServletName
	 *           远程服务监听器{@link AtmosphereServlet}的注册名称
	 */
	static void setRemoteServletName(String remoteServletName) {
		FrameContext.remoteServletName = remoteServletName;
	}

	/**
	 * 获取远程服务的入口URL路径名，所有远程服务将被映射到该路径下
	 * 
	 * @return 远程服务的入口URL相对路径
	 */
	public static String getRemotePathName() {
		return remotePathName;
	}

	/**
	 * 设置远程服务的入口URL路径名，所有远程服务将被映射到该路径下
	 * 
	 * @param remotePathName
	 *           远程服务的入口URL相对路径
	 */
	static void setRemotePathName(String remotePathName) {
		FrameContext.remotePathName = remotePathName;
	}

	/**
	 * 获取远程服务组件是否支持服务器主动推送功能
	 * 
	 * @return 远程服务组件是否支持服务器主动推送功能
	 */
	public static boolean isSupportPushEvent() {
		return supportPushEvent;
	}

	/**
	 * 设置远程服务组件是否支持服务器主动推送功能
	 * 
	 * @param supportPushEvent
	 *           远程服务组件是否支持服务器主动推送功能
	 */
	static void setSupportPushEvent(boolean supportPushEvent) {
		FrameContext.supportPushEvent = supportPushEvent;
	}

	/**
	 * 获取用户区域语言设置存储在Session中时所使用的属性名称
	 * 
	 * @return 用户区域语言设置存储在Session中时所使用的属性名称
	 */
	public static String getLocaleAttrName() {
		return localeAttrName;
	}

	/**
	 * 设置用户区域语言设置存储在Session中时所使用的属性名称
	 * 
	 * @param localeAttrName
	 *           用户区域语言设置存储在Session中时所使用的属性名称
	 */
	static void setLocaleAttrName(String localeAttrName) {
		FrameContext.localeAttrName = localeAttrName;
	}

	/**
	 * 获取{@link Container}容器实例
	 * 
	 * @return {@link Container}容器实例
	 */
	public static Container getContainer() {
		return container;
	}

	/**
	 * 设置{@link Container}容器实例
	 * 
	 * @param container
	 *           {@link Container}容器实例
	 */
	static void setContainer(Container container) {
		FrameContext.container = container;
	}

}
