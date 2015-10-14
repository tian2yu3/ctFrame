package com.carltian.frame.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * 用于扫描包路径下的类
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ClassScanner {

	/**
	 * 用于扫描指定包路径下的指定类型的类。<br/>
	 * 如果在多个资源位置存在相同限定名的类，则只会遍历一次。
	 * 
	 * @param pkg
	 *           需要扫描的包路径
	 * @param traverse
	 *           遍历器对象，用于指定需要遍历的类的基类型，当找到对应类时，该对象中的{@link ClassTraverse#forEach(Class) forEach}函数将被调用。
	 * @throws IOException
	 * @see ClassTraverse
	 */
	static public <T> void scan(String pkg, final ClassTraverse<T> traverse) throws IOException {
		ResourceScanner.scan(pkg, new ResourceTraverse() {
			private final Pattern pattern = Pattern.compile(".*\\$[0-9].*");
			private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			@SuppressWarnings("unchecked")
			@Override
			public void forEach(String pkgFile, InputStream is) {
				if (pkgFile.endsWith(".class") && !pattern.matcher(pkgFile).matches()) {
					String className = pkgFile.substring(0, pkgFile.length() - 6).replace("/", ".").replace("$", ".");
					try {
						Class<?> clazz = Class.forName(className, true, classLoader);
						if (traverse.getType().isAssignableFrom(clazz)) {
							traverse.forEach((Class<? extends T>) clazz);
						}
					} catch (ClassNotFoundException e) {
						// FIXME 找到的类不在ClassLoader有效路径内或没有访问权限
					}
				}
			}
		}, false);
	}
}
