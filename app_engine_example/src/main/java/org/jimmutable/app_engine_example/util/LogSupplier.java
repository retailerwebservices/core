package org.jimmutable.app_engine_example.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.IllegalFormatException;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class LogSupplier implements Supplier<String> {

	private static Logger logger = Logger.getLogger(LogSupplier.class.getName());

	private String format;
	private Object[] args;
	private Exception exception;

	/**
	 * 
	 * @param format
	 * @param args
	 * @see
	 */
	public LogSupplier(String format, Object... args) {
		this.format = format;
		this.args = args;
	}

	/**
	 * Log an Exception
	 * 
	 * @param e
	 *            the Exception
	 */
	public LogSupplier(Exception e) {
		this.exception = e;
	}

	private String exceptionFormat(Exception e) {
		if (e == null) {
			logger.warning("Null Exception!");
			return "null";
		}
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	private String stringFormat(String format, Object... args) {
		if (format == null || args == null) {
			logger.warning("Null format or args!");
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		try {
			 sb.append(String.format(format, args));
		} catch (IllegalFormatException e) {
			logger.warning(exceptionFormat(e));
			return format;
		}
		return sb.toString();
	}

	@Override
	public String get() {
		if (this.exception != null) {
			return exceptionFormat(this.exception);
		} else {
			return stringFormat(this.format, this.args);
		}
	}
}
