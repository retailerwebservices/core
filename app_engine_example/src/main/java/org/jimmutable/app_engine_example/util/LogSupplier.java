package org.jimmutable.app_engine_example.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.IllegalFormatException;
import java.util.function.Supplier;

public class LogSupplier implements Supplier<String> {

	private String format;
	private Object[] args;
	private Exception exception;

	public LogSupplier(String format, Object... args) {
		this.format = format;
		this.args = args;
	}

	public LogSupplier(Exception e) {
		this.exception = e;
	}

	private String exceptionFormat(Exception e) {
		if (e == null) {
			throw new RuntimeException("Cannot format a null Exception");
		}
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();

	}

	private String stringFormat(String format, Object... args) {

		if (format == null || args == null) {
			throw new RuntimeException("String format or args are null");
		}

		StringBuilder sb = new StringBuilder();
		try {
			return sb.append(String.format(format, args)).toString();
		} catch (IllegalFormatException e) {
			throw new RuntimeException(e);
		}
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
