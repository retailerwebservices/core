package org.jimmutable.platform_test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

//@WebServlet(urlPatterns =
//{ "/*" })
public class IndexServlet extends HttpServlet
{

	private static final Logger logger = LogManager.getLogger(IndexServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4631758373461858582L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{

		logger.fatal(request.getRemoteUser());

		// response.sendRedirect("index.jsp");
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
}