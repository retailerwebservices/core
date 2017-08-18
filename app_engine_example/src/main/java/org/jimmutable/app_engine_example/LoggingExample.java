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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;
import org.jimmutable.app_engine_example.util.LogManager;
import org.jimmutable.app_engine_example.util.LogWrapper;
//import org.jimmutable.app_engine_example.util.LogSupplier;
//import org.jimmutable.app_engine_example.util.LogSupplier;

public class LoggingExample extends HttpServlet {
	private static final long serialVersionUID = -4907844515014487767L;

	// private static final LogManager logger = new
	// LogManager(LoggingExample.class.getName());
	private static final LogManager logger = new LogManager(LoggingExample.class.getName());
	private static final Logger old = Logger.getLogger(LoggingExample.class.getName());

	// private static final org.apache.log4j.Logger log4j =
	// org.apache.log4j.Logger.getLogger(LoggingExample.class);
	// private static final Logger logger =
	// Logger.getLogger(LoggingExample.class.getName());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		response.getWriter().println("Test LogManager!");

		// logger.setParent(Logger.getLogger(LoggingExample.class.getName()));

		// log4j.fatal("<<<<<<<<LOG4j>>>>>>");

		// logger.setLevel(Level.ALL);

		// logger.fine(new LogSupplier("%sTHIS IS THE NEW LOGMANAGER", new Object[] {
		// "a" }));

		try {
			Integer.parseInt("boo");
		} catch (NumberFormatException e) {
			// log4j.error(e);
			// logger.severe(e);
			// old.severe("old " + e.getMessage());
			// old.severe("old " + e.getLocalizedMessage());

		}

		logger.warning("This is %s message from the new method.", new Object[] { "a" });

		// old.warning("old " + "old message");
		LogWrapper.warning(old, "do %s mi", new Object[] { "re" });

		// for (int i = 0; i < 10; i++) {
		// logger.finer("Testing finer " + i + "!");
		// logger.fine("Testing fine " + i + "!!");
		//
		// logger.info("Testing info " + i + "!!!");
		//
		// // this.getClass().getCanonicalName()
		//
		// logger.isLoggable(Level.INFO);
		//
		// // LogManager.config("Hey, some bucket does not exist, name %s", bucket_id);
		//
		// logger.warning("Testing warning " + i + "!!!!");
		// logger.severe("Testing severe " + i + "!!!!!");
		// }

	}

}
