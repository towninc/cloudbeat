package com.aimluck.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BasicAuthFilter implements Filter {

	Map<String, String> userMap = new HashMap<String, String>();

	private static final Logger log = Logger.getLogger(BasicAuthFilter.class
			.getName());

	private String[] excludes;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			String thisURL = req.getRequestURI();
			HttpSession session = req.getSession();
			if (!isExcludePath(thisURL) && (session == null
					|| session.getAttribute("userId") == null)) {
				HttpServletResponse res = (HttpServletResponse) response;
				res.sendRedirect("/login");
			} else {
				chain.doFilter(request, response);
			}
		} catch (ServletException e) {
			log.severe(e.getMessage());
		} catch (IOException e) {
			log.severe(e.getMessage());
		}
	}

	/**
	 * リクエストされたURLが除外対象か判断する。 除外対象の場合trueを返す
	 * 
	 * @param thisURL
	 * @return
	 */
	private boolean isExcludePath(String thisURL) {
		String[] excludes = this.excludes;
		for (String path : excludes) {
			if (path.indexOf("*") == path.length() - 1) {
				if (thisURL.indexOf(path.substring(0, path.length() - 2)) >= 0) {
					return true;
				}
			} else {
				if (thisURL.equals(path)) {
					return true;
				}
			}
		}
		return false;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		String exclude = filterConfig.getInitParameter("exclude");
		this.excludes = exclude.split(",");
	}

	public void destroy() {
	}
}
