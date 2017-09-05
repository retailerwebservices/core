package org.jimmutable.aws.logging;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.jimmutable.aws.StartupSingleton;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestLogging extends TestCase {

	private static Logger logger = Logger.getLogger(TestLogging.class.getName());
	private static OutputStream logCapturingStream;
	private static StreamHandler customLogHandler;

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public TestLogging(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSetup(new TestSuite(TestLogging.class)) {
			protected void setUp() throws Exception {
				// This loads the LoggingUtil class to setup logging how we want. We will need
				// to instantiate the singleton in our main method as well.
				StartupSingleton.setupOnce();

				logCapturingStream = new ByteArrayOutputStream();
				Formatter rootFormatter = LogManager.getLogManager().getLogger("").getHandlers()[0].getFormatter();

				assertEquals("org.jimmutable.aws.logging.SingleLineFormatter", rootFormatter.getClass().getName());

				customLogHandler = new StreamHandler(logCapturingStream, rootFormatter);
				logger.addHandler(customLogHandler);
			}

			protected void tearDown() throws Exception {
				logger.removeHandler(customLogHandler);
			}
		};
	}

	private String getTestCapturedLog() {
		customLogHandler.flush();
		return logCapturingStream.toString();
	}

	public void testLoggingLevel() {
		final String notExpected = "Info 1";

		LoggingUtil.updateRootLoggingLevel(Level.WARNING);
		logger.info(notExpected); // this should not print
		LoggingUtil.updateRootLoggingLevel(Level.INFO);
		logger.info("Info 2");
		logger.severe("Severe 1");

		String capturedLog = getTestCapturedLog();

		System.out.println(capturedLog);

		assertFalse(capturedLog.contains(notExpected));

		String[] lines = capturedLog.split("\r\n|\r|\n");

		assertTrue(lines.length == 2); // lines are 2 not 4

	}

	public void textExceptionOutput() {

		try {
			Integer.parseInt("foggle");
		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "myMessage", e);
		}

		String capturedLog = getTestCapturedLog();
		String[] lines = capturedLog.split("\r\n|\r|\n");

		assertTrue(lines[1].equals("java.lang.NumberFormatException: For input string: \"foggle\""));

	}

}
