package com.carltian.frame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

/**
 * 用于扫描包路径下的资源文件
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ResourceScanner {
	/**
	 * 用于扫描指定包路径下的资源文件，支持Jar包、文件夹、JBoss虚拟文件系统的扫描。<br/>
	 * 如果找到多个相同限定名的资源，则只会遍历第一次遇到的文件，之后遇到的同路径下的同名资源则会被忽略。
	 * 
	 * @param pkg
	 *           需要扫描的包路径
	 * @param traverse
	 *           遍历器对象，当找到资源文件时，该对象中的{@link ResourceTraverse#forEach(String, java.io.InputStream) forEach}函数将被调用。
	 * @param needInputStream
	 *           如果为{@code false}，则调用{@link ResourceTraverse#forEach(String, java.io.InputStream) forEach}
	 *           函数时将不会传入该资源文件的InputStream，这样可以提高遍历速度，降低系统开销。
	 * @throws IOException
	 */
	static public void scan(String pkg, ResourceTraverse traverse, boolean needInputStream) throws IOException {
		if (pkg == null || pkg.contains("/") || pkg.contains("\\") || pkg.startsWith(".") || pkg.endsWith(".")) {
			return;
		} else if (!pkg.isEmpty()) {
			pkg = pkg.replace(".", "/") + "/";
		}
		HashSet<String> nameSet = new HashSet<String>();
		Enumeration<URL> urlList = Thread.currentThread().getContextClassLoader().getResources(pkg);
		while (urlList.hasMoreElements()) {
			URL pkgUrl = urlList.nextElement();
			try {
				URI pkgUri = pkgUrl.toURI();
				String scheme = pkgUri.getScheme();
				if ("jar".equals(scheme)) {
					// jar包内文件（拆解URI）
					String jarFilePath = pkgUri.getSchemeSpecificPart();
					jarFilePath = jarFilePath.substring(0, jarFilePath.lastIndexOf("!"));
					scanJar(pkg, getLocalFile(new URI(jarFilePath)), traverse, needInputStream, nameSet);
				} else if ("file".equals(scheme)) {
					// 本地路径
					scanDir(pkg, new File(pkgUri), traverse, needInputStream, nameSet);
				} else if ("vfs".equals(scheme)) {
					scanVfsDir(pkg, VFS.getChild(pkgUri), traverse, needInputStream, nameSet);
				} else {
					throw new IOException("无法解析的资源类型：" + scheme);
				}
			} catch (URISyntaxException e) {
				FrameLogger.error("正在扫描的URL有误:[" + pkgUrl + "]", e);
			}
		}
	}

	/**
	 * 根据URI定位文件，支持普通文件路径或JBoss虚拟文件路径。
	 * 
	 * @param fileUri
	 *           文件URI
	 * @return 文件对象
	 * @throws IOException
	 */
	static public File getLocalFile(URI fileUri) throws IOException {
		String scheme = fileUri.getScheme();
		File file;
		if ("vfs".equals(scheme)) {
			// jboss vfs路径
			file = VFS.getChild(fileUri).getPhysicalFile();
		} else if ("file".equals(scheme)) {
			file = new File(fileUri);
		} else {
			throw new IOException("无法解析的本地资源类型：" + scheme);
		}
		return file;
	}

	static private void scanJar(String pkgPath, File jarFile, ResourceTraverse traverse, boolean needInputStream,
			HashSet<String> nameSet) throws IOException {
		JarFile jf = new JarFile(jarFile);
		Enumeration<JarEntry> jEntryList = jf.entries();
		while (jEntryList.hasMoreElements()) {
			JarEntry jEntry = jEntryList.nextElement();
			if (!jEntry.isDirectory() && jEntry.getName().startsWith(pkgPath) && !nameSet.contains(jEntry.getName())) {
				nameSet.add(jEntry.getName());
				traverse.forEach(jEntry.getName(), needInputStream ? jf.getInputStream(jEntry) : null);
			}
		}
	}

	static private void scanDir(String curPath, File dir, ResourceTraverse traverse, boolean needInputStream,
			HashSet<String> nameSet) throws IOException {
		File[] fileList = dir.listFiles();
		if (fileList == null) {
			return;
		}
		for (File file : fileList) {
			String newPath = curPath + file.getName();
			if (file.isDirectory()) {
				scanDir(newPath + "/", file, traverse, needInputStream, nameSet);
			} else if (file.isFile() && !nameSet.contains(newPath)) {
				nameSet.add(newPath);
				try {
					traverse.forEach(newPath, needInputStream ? new FileInputStream(file) : null);
				} catch (FileNotFoundException e) {
					throw new IOException(e);
				}
			}
		}
	}

	static private void scanVfsDir(String curPath, VirtualFile vDir, ResourceTraverse traverse, boolean needInputStream,
			HashSet<String> nameSet) throws IOException {
		List<VirtualFile> vfList = vDir.getChildren();
		if (vfList == null) {
			return;
		}
		for (VirtualFile vf : vfList) {
			String newPath = curPath + vf.getName();
			if (vf.isDirectory()) {
				scanVfsDir(newPath + "/", vf, traverse, needInputStream, nameSet);
			} else if (vf.isFile() && !nameSet.contains(newPath)) {
				nameSet.add(newPath);
				traverse.forEach(newPath, needInputStream ? vDir.openStream() : null);
			}
		}
	}
}
