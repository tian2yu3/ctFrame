package com.carltian.frame;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于维护{@link CurrentContext}信息的过滤器
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class RequestFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		CurrentContext.createContext();
		try {
			CurrentContext.setServletContext(req.getServletContext());
			if (req instanceof HttpServletRequest) {
				CurrentContext.setRequest((HttpServletRequest) req);
			}
			if (res instanceof HttpServletResponse) {
				CurrentContext.setResponse((HttpServletResponse) res);
			}
			chain.doFilter(req, res);
		} finally {
			CurrentContext.destroyContext();
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}
