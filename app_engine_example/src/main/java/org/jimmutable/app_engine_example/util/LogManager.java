package org.jimmutable.app_engine_example.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.IllegalFormatException;
import java.util.logging.Handler;
import java.util.logging.Level;

public class LogManager extends java.util.logging.Logger {

	// private static SimpleFormatter customSimpleFormatter = new SimpleFormatter()
	// {
	// private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
	//
	// @Override
	// public synchronized String format(LogRecord lr) {
	// return String.format(format, new Date(lr.getMillis()),
	// lr.getLevel().getLocalizedName(), lr.getMessage());
	// }
	//
	// };

	private static final SingleLineFormatter singleLineFormatter = new SingleLineFormatter();

	public LogManager(String name) {
		super(name, null);

		super.setParent(super.getLogger(name));
//		for (Handler h : super.getParent().getHandlers()) {
//			h.setFormatter(singleLineFormatter);
//		}
		for (Handler h : super.getHandlers()) {
			h.setFormatter(singleLineFormatter);
		}
		// ConsoleHandler consoleHandler = new ConsoleHandler();
		// consoleHandler.setFormatter(customSimpleFormatter);
		//
		// //
		// super.getParent().addHandler(consoleHandler);
		// super.setUseParentHandlers(false);
		// super.addHandler(consoleHandler);
	}

	private String stringFormat(String format, Object... args) {
		StringBuilder sb = new StringBuilder();
		sb.append(Thread.currentThread().getStackTrace()[3]).append(" ");
		try {
			return sb.append(String.format(format, args)).toString();
		} catch (IllegalFormatException e) {
			return sb.append(e.getMessage()).toString();
		}
	}

	private String exceptionFormat(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * Will only generate the formatted String if Level.FINE is loggable.
	 * 
	 * @see java.lang.String.format
	 * @param format
	 * @param args
	 */
	public void fine(String format, Object... args) {
		if (super.isLoggable(Level.FINE)) {
			super.fine(stringFormat(format, args));
		}
	}

	/**
	 * Will write full Exception stack trace if Level.FINE is loggable.
	 * 
	 * @param e
	 */
	public void fine(Exception e) {
		if (super.isLoggable(Level.FINE)) {
			super.fine(exceptionFormat(e));
		}
	}

	/**
	 * Will only generate the formatted String if Level.INFO is loggable.
	 * 
	 * @see java.lang.String.format
	 * @param format
	 * @param args
	 */
	public void info(String format, Object... args) {
		if (super.isLoggable(Level.INFO)) {
			super.info(stringFormat(format, args));
		}
	}

	/**
	 * Will write full Exception stack trace if Level.INFO is loggable.
	 * 
	 * @param e
	 */
	public void info(Exception e) {
		if (super.isLoggable(Level.INFO)) {
			super.info(exceptionFormat(e));
		}
	}

	/**
	 * Will only generate the formatted String if Level.WARNING is loggable.
	 * 
	 * @see java.lang.String.format
	 * @param format
	 * @param args
	 */
	public void warning(String format, Object... args) {
		if (super.isLoggable(Level.WARNING)) {
			super.warning(stringFormat(format, args));
		}
	}

	/**
	 * Will write full Exception stack trace if Level.WARNING is loggable.
	 * 
	 * @param e
	 */
	public void warning(Exception e) {
		if (super.isLoggable(Level.WARNING)) {
			super.warning(exceptionFormat(e));
		}
	}

	/**
	 * Will only generate the formatted String if Level.SEVERE is loggable.
	 * 
	 * @see java.lang.String.format
	 * @param format
	 * @param args
	 */
	public void severe(String format, Object... args) {
		if (super.isLoggable(Level.SEVERE)) {
			super.severe(stringFormat(format, args));
		}
	}

	/**
	 * Will write full Exception stack trace if Level.SEVERE is loggable.
	 * 
	 * @param e
	 */
	public void severe(Exception e) {
		if (super.isLoggable(Level.FINE)) {
			super.severe(exceptionFormat(e));
		}
	}

	/**
	 * Will only generate the formatted String if Level.CONFIG is loggable.
	 * 
	 * @see java.lang.String.format
	 * @param format
	 * @param args
	 */
	public void config(String format, Object... args) {
		if (super.isLoggable(Level.CONFIG)) {
			super.config(stringFormat(format, args));
		}
	}

	/**
	 * Will write full Exception stack trace if Level.CONFIG is loggable.
	 * 
	 * @param e
	 */
	public void config(Exception e) {
		if (super.isLoggable(Level.CONFIG)) {
			super.config(exceptionFormat(e));
		}
	}

}
