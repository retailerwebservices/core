package org.jimmutable.aws.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Code review
 * 
 * Class needs javadoc comments
 * @author kanej
 *
 */
public class SingleLineFormatter extends Formatter
{
	private static final String format = "%s %-7s %s %s %s %s\n";
	private final Date dat = new Date(); // CODE REVIEW: dat -> date
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

	public SingleLineFormatter()
	{
		super();
	}

	public synchronized String format(LogRecord record)
	{
		// CODE REVEIW: What if record is null?
		this.dat.setTime(record.getMillis());
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
		return String.format(format, dateFormat.format(dat), record.getLevel(), source, record.getLoggerName(), message,
				throwable);
	}
}
