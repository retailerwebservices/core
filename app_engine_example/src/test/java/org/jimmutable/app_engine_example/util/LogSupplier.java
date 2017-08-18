package org.jimmutable.app_engine_example.util;

import java.util.IllegalFormatException;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class LogSupplier implements Supplier<String> {

	private static final Logger logger = Logger.getLogger(LogSupplier.class.getName());

	String format;
	Object[] args;

	public LogSupplier(String format, Object... args) {
		this.format = format;
		this.args = args;
	}

	@Override
	public String get() {
		try {
			return String.format(format, args);
		} catch (IllegalFormatException e) {
			logger.severe(e.getMessage());
			return e.getMessage();
		}
	}

}
