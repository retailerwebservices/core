package org.jimmutable.cloud.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Used to set a handler's output format to a nice single line, instead of the
 * default two
 * 
 * @author trevorbox
 *
 */
public class SingleLineFormatter extends Formatter
{
	private static final String format = "%s %-7s %s %s %s\n";
	private final Date date = new Date();
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

	public SingleLineFormatter()
	{
		super();
	}

	public synchronized String format(LogRecord record)
	{
		if (record != null)
		{
			this.date.setTime(record.getMillis());
			String source;
			if (record.getSourceClassName() != null)
			{
				source = record.getSourceClassName();
				if (record.getSourceMethodName() != null)
				{
					source += " " + record.getSourceMethodName();
				}
			} else
			{
				source = record.getLoggerName();
			}
			String message = formatMessage(record);
			String throwable = "";
			if (record.getThrown() != null)
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println();
				record.getThrown().printStackTrace(pw);
				pw.close();
				throwable = sw.toString();
			}
			return String.format(format, dateFormat.format(date), record.getLevel(), source, message, throwable);
		}
		date.setTime(System.currentTimeMillis());
		return String.format(format, dateFormat.format(date), Level.SEVERE,
				SingleLineFormatter.class.getName() + " format", "LogRecord is null!", "");
	}
}
