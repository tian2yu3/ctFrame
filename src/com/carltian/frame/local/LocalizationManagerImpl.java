package com.carltian.frame.local;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.carltian.frame.CurrentContext;
import com.carltian.frame.container.annotation.ContainerConstructor;
import com.carltian.frame.container.annotation.InitArg;
import com.carltian.frame.util.FrameLogger;

/**
 * 本地化管理器实现类<br/>
 * 支持本地化字符串中使用占位符，支持根据参数和地区生成本地化字符串。
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class LocalizationManagerImpl implements LocalizationManager {

	private final Map<Locale, MapResourceBundle> bundleMap;
	private final Map<String, MessageFormat> formatterMap = new HashMap<String, MessageFormat>();
	private final ReadWriteLock formatterMapLock = new ReentrantReadWriteLock();

	/**
	 * 被容器调用的构造函数
	 * 
	 * @param loaderClass
	 *           加载本地化数据的类
	 */
	@ContainerConstructor
	public LocalizationManagerImpl(@InitArg("loaderClass") Class<? extends LocalizationLoader> loaderClass) {
		if (loaderClass == null) {
			bundleMap = null;
			return;
		}
		try {
			LocalizationLoader loader = loaderClass.newInstance();
			Map<Locale, Map<String, String>> localizationMap = loader.load();
			// 初始化资源
			bundleMap = new HashMap<Locale, MapResourceBundle>();
			for (Locale locale : localizationMap.keySet()) {
				bundleMap.put(locale, new MapResourceBundle(localizationMap.get(locale)));
			}
			// 初始化父链
			for (Locale locale : bundleMap.keySet()) {
				if (locale == null || locale.equals(Locale.ROOT)) {
					continue;
				}
				String localeStr = locale.toString();
				String parentLocaleStr = "";
				Locale parentLocale = Locale.ROOT;
				for (Locale curLocale : bundleMap.keySet()) {
					if (curLocale == null || curLocale.equals(Locale.ROOT) || curLocale.equals(locale)) {
						continue;
					}
					String curLocaleStr = curLocale.toString();
					if (curLocaleStr.length() > parentLocaleStr.length() && localeStr.startsWith(curLocaleStr)) {
						// 找到更近的父节点
						parentLocale = curLocale;
						parentLocaleStr = curLocaleStr;
					}
				}
				bundleMap.get(locale).setParent(bundleMap.get(parentLocale));
			}
		} catch (Exception e) {
			FrameLogger.error("载入Localization数据异常。");
			throw new RuntimeException(e);
		}
	}

	@Override
	public String get(String code) {
		return get(code, (Object[]) null);
	}

	@Override
	public String get(String code, Object[] params) {
		return get(code, params, null);
	}

	@Override
	public String get(String code, Locale locale) {
		return get(code, null, locale);
	}

	@Override
	public String get(String code, Object[] params, Locale locale) {
		// 修正默认参数
		if (locale == null) {
			locale = CurrentContext.getLocale();
		}
		ResourceBundle msgBundle = getBundleByLocale(locale);
		if (msgBundle != null) {
			String msg = null;
			try {
				msg = msgBundle.getString(code);
			} catch (MissingResourceException e) {
				// 无法找到本地化字符串
			}
			if (msg != null) {
				if (params != null) {
					// 带占位符参数
					MessageFormat formatter = null;
					formatterMapLock.readLock().lock();
					try {
						formatter = formatterMap.get(msg);
					} finally {
						formatterMapLock.readLock().unlock();
					}
					if (formatter == null) {
						formatterMapLock.writeLock().lock();
						try {
							formatter = formatterMap.get(msg);
							if (formatter == null) {
								formatter = new MessageFormat(msg, locale);
								formatterMap.put(msg, formatter);
							}
						} finally {
							formatterMapLock.writeLock().unlock();
						}
					} else {
						formatter.setLocale(locale);
					}
					msg = formatter.format(params);
				}
				return msg;
			}
		}
		return code;
	}

	private ResourceBundle getBundleByLocale(Locale locale) {
		if (bundleMap != null) {
			ResourceBundle bundle = bundleMap.get(locale);
			if (bundle == null) {
				bundle = bundleMap.get(new Locale(locale.getLanguage(), locale.getCountry()));
			}
			if (bundle == null) {
				bundle = bundleMap.get(new Locale(locale.getLanguage()));
			}
			if (bundle == null) {
				bundle = bundleMap.get(Locale.ROOT);
			}
			return bundle;
		}
		return null;
	}
}
