package com.carltian.frame;

import java.util.Locale;
import java.util.Stack;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 当前线程上下文环境。使用{@link ThreadLocal}类型变量保存上下文堆栈变量，描述当前位置的Http上下文
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class CurrentContext {
	private static final ThreadLocal<Stack<ContextData>> threadContextStack = new ThreadLocal<Stack<ContextData>>();

	/**
	 * 初始化新的一层环境，初始化之后将可以对新的环境进行初始化
	 */
	static void createContext() {
		Stack<ContextData> stack = threadContextStack.get();
		if (stack == null) {
			stack = new Stack<ContextData>();
			threadContextStack.set(stack);
		}
		stack.push(new ContextData());
	}

	/**
	 * 销毁最后创建的一层环境，销毁之后上下文环境将恢复到创建该层环境之前的样子
	 */
	static void destroyContext() {
		Stack<ContextData> stack = threadContextStack.get();
		if (stack != null && !stack.empty()) {
			stack.pop();
		} else {
			throw new RuntimeException("Context haven't been created!");
		}
	}

	/**
	 * 获取当前线程的当前层次的环境
	 * 
	 * @return 当前层次的环境数据
	 */
	private static ContextData getContext() {
		Stack<ContextData> stack = threadContextStack.get();
		if (stack != null && !stack.empty()) {
			return stack.peek();
		} else {
			throw new RuntimeException("Context haven't been created!");
		}
	}

	/**
	 * 获取当前环境的{@link ServletContext}对象
	 * 
	 * @return 当前环境的{@link ServletContext}对象
	 */
	public static ServletContext getServletContext() {
		return getContext().servletContext;
	}

	/**
	 * 设置当前环境的{@link ServletContext}对象
	 * 
	 * @param servletContext
	 *           当前环境的{@link ServletContext}对象
	 */
	static void setServletContext(ServletContext servletContext) {
		getContext().servletContext = servletContext;
	}

	/**
	 * 获取当前环境的{@link HttpServletRequest}对象
	 * 
	 * @return 当前环境的{@link HttpServletRequest}对象
	 */
	public static HttpServletRequest getRequest() {
		return getContext().request;
	}

	/**
	 * 设置当前环境的{@link HttpServletRequest}对象
	 * 
	 * @param request
	 *           当前环境的{@link HttpServletRequest}对象
	 */
	static void setRequest(HttpServletRequest request) {
		getContext().request = request;
	}

	/**
	 * 获取当前环境的{@link HttpServletResponse}对象
	 * 
	 * @return 当前环境的{@link HttpServletResponse}对象
	 */
	public static HttpServletResponse getResponse() {
		return getContext().response;
	}

	/**
	 * 设置当前环境的{@link HttpServletResponse}对象
	 * 
	 * @param response
	 *           当前环境的{@link HttpServletResponse}对象
	 */
	static void setResponse(HttpServletResponse response) {
		getContext().response = response;
	}

	/**
	 * 获取当前环境的区域/语言信息
	 * 
	 * @return 当前环境的区域/语言信息
	 */
	static public Locale getLocale() {
		HttpServletRequest req = getRequest();
		if (req != null) {
			HttpSession session = req.getSession(false);
			if (session != null) {
				Object locale = session.getAttribute(FrameContext.getLocaleAttrName());
				if (locale != null && locale instanceof Locale) {
					return (Locale) locale;
				}
			}
		}
		return FrameContext.getDefaultLocale();
	}

	/**
	 * 设置当前环境的区域/语言信息
	 * 
	 * @param locale
	 *           当前环境的区域/语言信息
	 */
	static public void setLocale(Locale locale) {
		HttpServletRequest req = getRequest();
		if (req != null) {
			HttpSession session = req.getSession(true);
			if (session != null) {
				session.setAttribute(FrameContext.getLocaleAttrName(), locale);
			}
		}
	}

	private static class ContextData {
		private ServletContext servletContext = null;
		private HttpServletRequest request = null;
		private HttpServletResponse response = null;
	}
}
