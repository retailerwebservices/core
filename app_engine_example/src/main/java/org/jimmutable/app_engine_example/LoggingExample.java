/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jimmutable.app_engine_example;

import java.io.IOException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jimmutable.app_engine_example.util.LogSupplier;

public class LoggingExample extends HttpServlet {
	private static final long serialVersionUID = -4907844515014487767L;

	private static final Logger logger = Logger.getLogger(LoggingExample.class.getName());

	public void init() throws ServletException {
		logger.setLevel(Level.ALL); // all different logging levels can be displayed
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Map<String, ?> params = request.getParameterMap();

		params.forEach((level, value) -> {
			String msg = request.getParameter(level);
			try {
				logger.log(Level.parse(level), new LogSupplier("This is the formatted message: %s", msg));

			} catch (Exception e) {
				logger.severe(new LogSupplier(e));
			}
		});

		if (request.getRequestURL().toString().contains("localhost")) {
			request.setAttribute("logUrl", "See console output...");
		} else {
			request.setAttribute("logUrl",
					"See the logs in <a	href=\"https://console.cloud.google.com/logs/viewer?project=platform-test-174921\">https://console.cloud.google.com/logs/viewer?project=platform-test-174921</a>");
		}

		request.getRequestDispatcher("/logging.jsp").forward(request, response);

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.getRequestDispatcher("/logging.jsp").forward(request, response);

	}

}
