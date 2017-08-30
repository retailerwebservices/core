package org.jimmutable.gcloud.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.IllegalFormatException;
//import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Use this for all java.util.logging.Logger methods with a Supplier<String>
 * constructor
 * 
 * @author trevorbox
 *
 */
public class LogSupplier {

	private static Logger logger = Logger.getLogger(LogSupplier.class.getName());

	private String format;
	private Object[] args;
	private Exception exception;

	/**
	 * Log a String with format and optional object arguments
	 * 
	 * @param format
	 * @param args
	 * @see java.lang.String.format
	 */
	public LogSupplier(String format, Object... args) {
		this.format = format;
		this.args = args;
	}

	/**
	 * Log an Exception and its stacktrace
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

	public String get() {
		if (this.exception != null) {
			return exceptionFormat(this.exception);
		} else {
			return stringFormat(this.format, this.args);
		}
	}
}
