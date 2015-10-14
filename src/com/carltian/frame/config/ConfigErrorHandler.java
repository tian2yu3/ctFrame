package com.carltian.frame.config;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.carltian.frame.util.FrameLogger;

/**
 * 解析配置出错时的处理类
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ConfigErrorHandler implements ErrorHandler {

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		FrameLogger.warn("配置文件存在一些问题。", exception);
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		throw exception;
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		throw exception;
	}

}
