package com.carltian.frame.util;

import java.io.InputStream;

/**
 * 资源遍历器，配合{@link ResourceScanner}使用。
 * 
 * @version 1.0
 * @author Carl Tian
 * @see ResourceScanner
 */
public interface ResourceTraverse {
	/**
	 * 每当遍历到一个资源文件时，该函数就会被调用，并传入被遍历的资源文件路径。<br/>
	 * 完全限定名相同的资源文件只会被遍历一次，之后再次遇到相同包路径下的同名文件则会被忽略。
	 * 
	 * @param pkgFile
	 *           资源文件的包路径和文件名，即资源的完全限定名
	 * @param is
	 *           资源的输入流，依据遍历参数的不同，该参数可能为{@code null}
	 * @see ResourceScanner#scan(String, ResourceTraverse, boolean)
	 */
	public void forEach(String pkgFile, InputStream is);
}
