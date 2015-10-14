package com.carltian.frame.container;

import com.carltian.frame.container.reg.RegistrationType;

/**
 * 容器接口
 * 
 * @version 1.0
 * @author Carl Tian
 */
public interface Container {

	/**
	 * 查找一个可用的对象
	 * 
	 * @param type
	 *           对象的注册类型
	 * @param name
	 *           对象的唯一注册名称
	 * @return 对象可用实例
	 */
	public abstract <T> T lookup(RegistrationType type, Object name);

	/**
	 * 查找一个可用的对象
	 * 
	 * @param extType
	 *           对象的注册扩展类型
	 * @param name
	 *           对象的唯一注册名称
	 * @return 对象可用实例
	 */
	public abstract <T> T lookup(String extType, Object name);

	/**
	 * 查找一个可用的对象
	 * 
	 * @param type
	 *           对象的注册类型
	 * @param extType
	 *           对象的注册扩展类型
	 * @param name
	 *           对象的唯一注册名称
	 * @return 对象可用实例
	 */
	public abstract <T> T lookup(RegistrationType type, String extType, Object name);

}
