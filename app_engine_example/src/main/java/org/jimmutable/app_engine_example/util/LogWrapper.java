package org.jimmutable.app_engine_example.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.IllegalFormatException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogWrapper {

	// private static String stringFormat(String format, Object... args) {
	// StringBuilder sb = new StringBuilder();
	// try {
	// return sb.append(String.format(format, args)).toString();
	// } catch (IllegalFormatException e) {
	// return sb.append(e.getMessage()).toString();
	// }
	// }

	private static String stringFormat(String format, Object... args) {
		StringBuilder sb = new StringBuilder();
		// TODO This is risky to get the line number, but appears to be the only way to
		// get it that I can find out. Works locally and in prod.
		try {
			sb.append(Thread.currentThread().getStackTrace()[3]).append(": ");
		} catch (Exception e) {

		}
		try {
			return sb.append(String.format(format, args)).toString();
		} catch (IllegalFormatException e) {
			return sb.append(e.getMessage()).toString();
		}
	}

	private static String exceptionFormat(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	private static final SingleLineFormatter singleLineFormatter = new SingleLineFormatter();

	/**
	 * Will only generate the formatted String if Level.WARNING is loggable.
	 * 
	 * @see java.lang.String.format
	 * @param format
	 * @param args
	 */
	public static void warning(Logger logger, String format, Object... args) {
		if (logger.isLoggable(Level.WARNING)) {
			for (Handler h : logger.getParent().getHandlers()) {
				h.setFormatter(singleLineFormatter);
			}
			logger.warning(stringFormat(format, args));
		}
	}

	/**
	 * Will write full Exception stack trace if Level.WARNING is loggable.
	 * 
	 * @param e
	 */
	public static void warning(Logger logger, Exception e) {
		if (logger.isLoggable(Level.WARNING)) {
			for (Handler h : logger.getParent().getHandlers()) {
				h.setFormatter(singleLineFormatter);
			}
			logger.warning(exceptionFormat(e));
		}
	}

}
