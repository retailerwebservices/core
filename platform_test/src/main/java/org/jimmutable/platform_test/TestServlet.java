package org.jimmutable.platform_test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet
{
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html");
		
		
		
		response.getWriter().println("Hello World");
	}
}
