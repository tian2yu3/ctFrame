package com.carltian.frame.config;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.carltian.frame.util.FrameLogger;

public class ConfigSchemaResover implements EntityResolver {
	public final String SCHEMA_DIR_URL1 = "http://www.carltian.com/xml/ns/frame/";
	public final String SCHEMA_DIR_URL2 = "https://www.carltian.com/xml/ns/frame/";
	public final String SCHEMA_DIR_PKG = "com/carltian/frame/config/schema/";

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (systemId != null) {
			String pkgPath = null;
			if (systemId.startsWith(SCHEMA_DIR_URL1)) {
				pkgPath = systemId.replace(SCHEMA_DIR_URL1, SCHEMA_DIR_PKG);
			} else if (systemId.startsWith(SCHEMA_DIR_URL2)) {
				pkgPath = systemId.replace(SCHEMA_DIR_URL2, SCHEMA_DIR_PKG);
			}
			if (pkgPath != null) {
				try {
					InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(pkgPath);
					InputSource source = new InputSource(is);
					source.setPublicId(publicId);
					source.setSystemId(systemId);
					return source;
				} catch (Exception e) {
					FrameLogger.warn("无法映射xsd文件！", e);
				}
			}
		}
		FrameLogger.debug("在本地无法找到资源：" + publicId + "[" + systemId + "]");
		return null;
	}
}
