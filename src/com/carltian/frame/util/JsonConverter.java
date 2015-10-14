package com.carltian.frame.util;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSON数据转换器，用于序列化到JSON文本或反序列化为POJO对象。
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class JsonConverter {
	/**
	 * @param str
	 *           需要反序列化的JSON字符串
	 * @param clazz
	 *           需要反序列化的对象类型
	 * @param urlEncode
	 *           如果为{@code true}，则反序列化之前会对JSON字符串按照URL标准解码，前端可以使用encodeURIComponent编码。
	 * @return 反序列化后的POJO对象
	 */
	static public <T> T decode(String str, Class<T> clazz, boolean urlEncode) {
		try {
			if (urlEncode) {
				str = URLDecoder.decode(str, "UTF-8");
			}
			return JSON.parseObject(str, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param str
	 *           需要反序列化的JSON字符串
	 * @param type
	 *           需要反序列化的对象类型
	 * @param urlEncode
	 *           如果为{@code true}，则反序列化之前会对JSON字符串按照URL标准解码，前端可以使用encodeURIComponent编码。
	 * @return 反序列化后的POJO对象
	 */
	static public <T> T decode(String str, Type type, boolean urlEncode) {
		try {
			if (urlEncode) {
				str = URLDecoder.decode(str, "UTF-8");
			}
			return JSON.parseObject(str, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param str
	 *           需要反序列化的JSON字符串
	 * @param type
	 *           需要反序列化的对象类型
	 * @param urlEncode
	 *           如果为{@code true}，则反序列化之前会对JSON字符串按照URL标准解码，前端可以使用encodeURIComponent编码。
	 * @return 反序列化后的POJO对象
	 */
	static public <T> T decode(String str, TypeReference<T> type, boolean urlEncode) {
		try {
			if (urlEncode) {
				str = URLDecoder.decode(str, "UTF-8");
			}
			return JSON.parseObject(str, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将POJO对象序列化为JSON字符串
	 * 
	 * @param object
	 *           需要序列化的对象
	 * @param urlEncode
	 *           如果为{@code true}，则序列化的字符串会被按照URL标准编码，前端可以使用decodeURIComponent解码。
	 * @return 序列化后的JSON字符串
	 */
	static public String encode(Object object, boolean urlEncode) {
		try {
			String dataStr = JSON.toJSONString(object, SerializerFeature.WriteEnumUsingToString);
			return urlEncode ? URLEncoder.encode(dataStr, "UTF-8").replace("+", "%20") : dataStr;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
