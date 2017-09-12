package org.jimmutable.cloud.logging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jimmutable.cloud.StartupSingleton;

/**
 * 
 * Creates a special file logging output handler that outputs logs to a new file
 * each day in StartupSingleton.LOG_DIR. Uses SingleLineFormatter.
 * 
 * @author trevorbox
 *
 */
public class DatedFileHandler extends Handler
{

	private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SingleLineFormatter formatter = new SingleLineFormatter();
	private Date date = new Date();

	static
	{
		FILE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("US/Arizona"));
	}

	/**
	 * Hijacks a FileHandler to write to a dated file within
	 * StartupSingleton.LOG_DIR
	 */
	@Override
	public synchronized void publish(LogRecord r)
	{
		if (r != null)
		{
			if (isLoggable(r))
			{
				FileHandler f;
				try
				{
					f = new FileHandler(fileName(r), true);
					f.setFormatter(formatter);
					f.publish(r);
					f.close(); // if this is not closed you will get multiple writes to different logs
				} catch (SecurityException | IOException e)
				{
					e.printStackTrace();
					this.reportError(null, e, ErrorManager.WRITE_FAILURE);
				}
			}
		}
	}

	@Override
	public void flush()
	{
	}

	@Override
	public void close()
	{
		super.setLevel(Level.OFF);
	}

	/**
	 * Get the filename based on the record's timestamp, else will use the current
	 * timestamp
	 * 
	 * @param r
	 * @return
	 */
	private String fileName(LogRecord r)
	{
		if (r != null)
		{
			date.setTime(r.getMillis());
		} else
		{
			date.setTime(System.currentTimeMillis());
		}
		return String.format("%s/%s-%s.log", StartupSingleton.getSimpleLoggingDirectory(),
				StartupSingleton.getSimpleLoggingFileName(), FILE_DATE_FORMAT.format(date));
	}
}