package com.carltian.frame.util;

/**
 * 类遍历器，配合{@link ClassScanner}使用。
 * 
 * @version 1.0
 * @author Carl Tian
 * @see ClassScanner
 */
public abstract class ClassTraverse<T> {
	private final Class<T> type;

	/**
	 * 根据提供的类对象，构造用于指定类的遍历器。
	 * 
	 * @param type
	 *           指定一个遍历类型，该遍历器只会被用于遍历指定类型的类。
	 */
	public ClassTraverse(Class<T> type) {
		this.type = type;
	}

	/**
	 * 获取当前遍历器的遍历类型，该类遍历器只会被用于遍历指定类型的类。
	 * 
	 * @return 当前类遍历器的遍历类型
	 */
	public Class<T> getType() {
		return type;
	}

	/**
	 * 每当遍历到一个符合遍历类型的类对象，该函数就会被调用，并传入被遍历到的类对象。
	 * 
	 * @param clazz
	 *           被遍历到的类对象
	 * @see ClassScanner#scan(String, ClassTraverse)
	 */
	public abstract void forEach(Class<? extends T> clazz);
}
