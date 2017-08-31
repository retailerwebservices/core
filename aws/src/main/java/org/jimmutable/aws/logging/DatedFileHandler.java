package org.jimmutable.aws.logging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jimmutable.aws.StartupSingleton;

public class DatedFileHandler extends Handler
{

	private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SingleLineFormatter formatter = new SingleLineFormatter();
	private Date date = new Date();

	static
	{
		FILE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("US/Arizona"));
	}

	@Override
	public synchronized void publish(LogRecord r)
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

	@Override
	public void flush()
	{
	}

	@Override
	public void close()
	{
		super.setLevel(Level.OFF);
	}

	private String fileName(LogRecord r)
	{
		date.setTime(r.getMillis());
		return String.format("%s/%s-%s.log", StartupSingleton.LOG_DIR, StartupSingleton.APP_NAME,
				FILE_DATE_FORMAT.format(date));
	}
}