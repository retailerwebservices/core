package org.jimmutable.platform_test;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthFilter implements Filter
{

	private static final Logger logger = LogManager.getLogger(AuthFilter.class);

	@Override
	public void destroy()
	{

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		for (Cookie cookie : request.getCookies())
		{
			if (cookie.getName().equals("token"))
			{
				logger.info(String.format("Token cookie exists: %s", cookie.toString()));
				chain.doFilter(request, response);
				return;
			}
		}

		String loginURI = request.getContextPath() + "/login";
		response.sendRedirect(loginURI);
	}

	@Override
	public void init(FilterConfig config) throws ServletException
	{

	}

}
