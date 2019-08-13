package org.jimmutable.cloud.servlets.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HeartbeatServlet extends HttpServlet
{
	public static final String PATH_SPEC = "/public/heartbeat";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4332485446424268588L;

	private static final String content_body = 
			"  <body>" + 
			"    <p>If you are reading this, my web service is running.</p>" + 
			"  </body>";
	
	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(content_body);
	}
}
