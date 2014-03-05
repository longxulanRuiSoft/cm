package com.ruisoft.cm.rbac.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ruisoft.cm.rbac.util.SysConstants;

public class SecurityFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(SecurityFilter.class);

    public SecurityFilter() {
    }
    
    private String[] excludes = null;
    public void init(FilterConfig fConfig) throws ServletException {
    	String excludeList = fConfig.getInitParameter("exclude-list");
    	excludes = excludeList.split("[,;:|]");
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		LOG.debug("进行安全验证");
		
		HttpServletRequest r = (HttpServletRequest) request;
		if (excludes != null) {
			String rPath = r.getRequestURI();
			String query = r.getQueryString();
			if (query != null && !"".equals(query))
				query = rPath.concat("?").concat(query);
			LOG.debug("uri: " + query);
			
			String context = r.getContextPath();
			for (String l : excludes) {
				if (context.concat(l).equals(rPath)) {
					LOG.info("忽略请求" + rPath);
					chain.doFilter(request, response);
					return;
				}
			}
		}
		
		HttpSession session = r.getSession();
		Object user = session.getAttribute(SysConstants.USER_INFO.toString());

		if (user == null) {
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.sendRedirect(resp.encodeURL(r.getContextPath()
					+ "/error.html?status=0"));
			return;
		}
		
		chain.doFilter(request, response);
	}
}