package com.carltian.frame.local;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 适用于Map数据结构的资源束
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class MapResourceBundle extends ResourceBundle {

	private final Map<String, ?> lookup;

	/**
	 * @param map
	 *           作为资源数据的Map，如果参数为{@code null}，则构造一个空白的资源束。
	 */
	public MapResourceBundle(Map<String, ?> map) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		lookup = map;
	}

	@Override
	protected Object handleGetObject(String s) {
		return lookup.get(s);
	}

	@Override
	public Enumeration<String> getKeys() {
		return new ResourceBundleEnumeration(lookup.keySet(), parent == null ? null : parent.getKeys());
	}

	/**
	 * 设置父资源束
	 * 
	 * @param resourcebundle
	 *           父资源束
	 */
	@Override
	public void setParent(ResourceBundle resourcebundle) {
		super.setParent(resourcebundle);
	}

	@Override
	protected Set<String> handleKeySet() {
		return lookup.keySet();
	}

	/**
	 * 适用于资源束的key枚举，用于避免使用sun的非标准类
	 * 
	 * @version 1.0
	 * @author Carl Tian
	 */
	private class ResourceBundleEnumeration implements Enumeration<String> {
		private final Set<String> keySet;
		private final Iterator<?> iterator;
		private final Enumeration<String> parentEnum;
		private String next;

		public ResourceBundleEnumeration(Set<String> keySet, Enumeration<String> parentEnum) {
			this.parentEnum = parentEnum;
			this.keySet = keySet;
			iterator = keySet.iterator();
			next = null;
		}

		@Override
		public boolean hasMoreElements() {
			if (next == null) {
				if (iterator.hasNext()) {
					next = (String) iterator.next();
				} else if (parentEnum != null) {
					while (true) {
						if (next != null || !parentEnum.hasMoreElements()) {
							break;
						}
						next = parentEnum.nextElement();
						if (keySet.contains(next)) {
							next = null;
						}
					}
				}
			}
			return next != null;
		}

		@Override
		public String nextElement() {
			if (hasMoreElements()) {
				String s = next;
				next = null;
				return s;
			} else {
				throw new NoSuchElementException();
			}
		}
	}

}
