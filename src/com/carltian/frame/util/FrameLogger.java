package com.carltian.frame.util;

import org.apache.log4j.Logger;

/**
 * 框架日志类，用于输出框架内的日志信息，遵循log4j标准。
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class FrameLogger {
	static private Logger logger = Logger.getLogger(FrameLogger.class);

	private FrameLogger() {
		// 禁用实例化
	}

	static public void error(Object message) {
		logger.error(message);
	}

	static public void warn(Object message) {
		logger.warn(message);
	}

	static public void info(Object message) {
		logger.info(message);
	}

	static public void debug(Object message, Throwable t) {
		logger.debug(message, t);
	}

	static public void error(Object message, Throwable t) {
		logger.error(message, t);
	}

	static public void warn(Object message, Throwable t) {
		logger.warn(message, t);
	}

	static public void info(Object message, Throwable t) {
		logger.info(message, t);
	}

	static public void debug(Object message) {
		logger.debug(message);
	}
}
