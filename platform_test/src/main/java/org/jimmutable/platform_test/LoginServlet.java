package org.jimmutable.platform_test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Authentication.User;

public class LoginServlet extends HttpServlet
{

	private static final Logger logger = LogManager.getLogger(LoginServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -310485784951529000L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{

		logger.fatal(request.getRemoteUser());

		// response.sendRedirect("index.jsp");
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String username = request.getParameter("j_username");
		String password = request.getParameter("j_password");
		Map<String, String> messages = new HashMap<String, String>();

		if (username == null || username.isEmpty())
		{
			messages.put("username", "Please enter username");
		}

		if (password == null || password.isEmpty())
		{
			messages.put("password", "Please enter password");
		}

		Cookie cookie = new Cookie("token", "token_value");

		response.addCookie(cookie);

		response.sendRedirect(request.getContextPath() + "/app/");

		// if (messages.isEmpty())
		// {
		//
		// if (user != null)
		// {
		// request.getSession().setAttribute("user", user);
		// response.sendRedirect(request.getContextPath() + "/protected/");
		// return;
		// } else
		// {
		// messages.put("login", "Unknown login, please try again");
		// }
		// }
		//
		// request.setAttribute("messages", messages);
		// request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request,
		// response);
	}

}
