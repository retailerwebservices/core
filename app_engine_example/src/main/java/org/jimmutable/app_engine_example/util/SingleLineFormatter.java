package org.jimmutable.app_engine_example.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter {

	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String lineSep = System.getProperty("line.separator");

	/**
	 * A Custom format implementation that is designed for brevity.
	 */
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(record.getLevel()).append(": ");
		sb.append(format.format(new Date(record.getMillis()))).append(" ");
		sb.append(record.getMessage()).append(lineSep);
		return sb.toString();
	}

}