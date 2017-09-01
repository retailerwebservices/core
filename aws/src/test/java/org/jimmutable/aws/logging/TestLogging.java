package org.jimmutable.aws.logging;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.jimmutable.aws.StartupSingleton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogging
{

	private static Logger rootLogger = Logger.getLogger("");
	private static OutputStream logCapturingStream;
	private static StreamHandler customLogHandler;

	@Before
	public void before()
	{
		// This loads the LoggingUtil class to setup logging how we want. We will need
		// to instantiate the singleton in our main method as well.
		StartupSingleton.setupOnce();

		// initialize the root logging level
		// LoggingUtil.updateRootLoggingLevel(Level.INFO);
		logCapturingStream = new ByteArrayOutputStream();
		Formatter rootFormatter = rootLogger.getHandlers()[0].getFormatter();

		assertEquals("org.jimmutable.aws.logging.SingleLineFormatter", rootFormatter.getClass().getName());

		customLogHandler = new StreamHandler(logCapturingStream, rootFormatter);
		rootLogger.addHandler(customLogHandler);
	}

	private String getTestCapturedLog()
	{
		customLogHandler.flush();
		return logCapturingStream.toString();
	}

	@Test
	public void testRootLoggingLevel()
	{
		final String notExpected = "Info 1";

		LoggingUtil.updateRootLoggingLevel(Level.WARNING);
		rootLogger.info(notExpected); // this should not print
		LoggingUtil.updateRootLoggingLevel(Level.INFO);
		rootLogger.info("Info 2");
		rootLogger.severe("Severe 1");

		String capturedLog = getTestCapturedLog();

		System.out.println(capturedLog);

		assertFalse(capturedLog.contains(notExpected));

		String[] lines = capturedLog.split("\r\n|\r|\n");

		assertTrue(lines.length == 2); // lines are 2 not 4

	}

	@Test
	public void textExceptionOutput()
	{

		try
		{
			Integer.parseInt("foggle");
		} catch (NumberFormatException e)
		{
			rootLogger.log(Level.WARNING, "myMessage", e);
		}

		String capturedLog = getTestCapturedLog();
		String[] lines = capturedLog.split("\r\n|\r|\n");

		assertTrue(lines[1].equals("java.lang.NumberFormatException: For input string: \"foggle\""));

	}

	@After
	public void after()
	{
		rootLogger.removeHandler(customLogHandler);
	}

}
