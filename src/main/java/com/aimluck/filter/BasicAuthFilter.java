package com.aimluck.filter;

import java.io.IOException;
import java.util.Enumeration;
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

import org.apache.commons.codec.binary.Base64;

public class BasicAuthFilter implements Filter {

	Map<String, String> userMap = new HashMap<String, String>();

	private static final Logger log = Logger.getLogger(BasicAuthFilter.class
			.getName());

	private String[] excludes;

	private boolean tryAuth(String authHeader) {
		if (authHeader == null)
			return false;
		String[] pair = authHeader.split(" ");
		if (pair.length != 2) {
			log.severe("failed to parse authHeader: " + authHeader);
			return false;
		}
		if (!pair[0].equals("Basic")) { // schema
			log.severe("unsupported login scheme: " + pair[0]);
			return false;
		}
		String decoded = new String(Base64.decodeBase64(pair[1].getBytes()));
		String[] userPass = decoded.split(":");
		if (userPass.length != 2 || !userMap.containsKey(userPass[0])
				|| !userMap.get(userPass[0]).equals(userPass[1])) {
			log.severe("AuthFailure: " + decoded);
			return false;
		}
		log.info("authentication succeeded for user '" + userPass[0] + "'");
		return true;
	}

	private void send401(ServletResponse response, String realm, String message)
			throws IOException {
		HttpServletResponse res = (HttpServletResponse) response;
		res.setStatus(401);
		res.setHeader("WWW-Authenticate", "Basic realm=" + realm);
		res.getWriter().println("<body><h1>" + message + "</h1></body>\n");
		return;
	}

	String REALM = "Basic"; // default

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		try {

			HttpServletRequest req = (HttpServletRequest) request;
			String authHeader = req.getHeader("Authorization");
			String thisURL = req.getRequestURI();
			if (!isExcludePath(thisURL) && !tryAuth(authHeader))
				send401(response, REALM, "realm");
			else
				chain.doFilter(request, response);
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
			// 除外対象パスの最後が「*」の場合、indexOfで含まれるか確認
			if (path.indexOf("*") == path.length() - 1) {
				if (thisURL.indexOf(path.substring(0, path.length() - 2)) >= 0) {
					return true;
				}
			} else {
				// 上記以外は、完全一致
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

		String tmp = filterConfig.getInitParameter("realm");
		if (tmp != null)
			REALM = tmp;
		log.info("realm = " + REALM);
		Enumeration keys = filterConfig.getInitParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith("user.")) { // assumes it is USER:PASS
				String[] userPass = filterConfig.getInitParameter(key).split(
						":");
				if (userPass.length == 2) {
					userMap.put(userPass[0], userPass[1]);
					log.info("new user :" + userPass[0]);
				}
			}
		}
	}

	public void destroy() {
	}
}
