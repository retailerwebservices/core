package org.jimmutable.cloud.cache;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.logging.log4j.util.PropertiesUtil;

public class CacheEventUtils
{
	private static final String SYSTEM_OUT = "system.out";

	private static final String SYSTEM_ERR = "system.err";

	protected static final String SYSTEM_PREFIX = "org.apache.logging.log4j.simplelog.";

	private static final char SPACE = ' ';
	private static PrintStream stream;

	static
	{
		PropertiesUtil props = new PropertiesUtil("log4j2.simplelog.properties");
		final String fileName = props.getStringProperty(SYSTEM_PREFIX + "logFile", SYSTEM_ERR);
		PrintStream ps;
		if ( SYSTEM_ERR.equalsIgnoreCase(fileName) )
		{
			ps = System.err;
		}
		else if ( SYSTEM_OUT.equalsIgnoreCase(fileName) )
		{
			ps = System.out;
		}
		else
		{
			try
			{
				final FileOutputStream os = new FileOutputStream(fileName);
				ps = new PrintStream(os);
			}
			catch ( final FileNotFoundException fnfe )
			{
				ps = System.err;
			}
		}
		stream = ps;
	}

	public static void log( CacheEvent event )
	{
		final StringBuilder sb = new StringBuilder();

		sb.append(event.getSimpleTimestampHumanReadable());
		sb.append(SPACE);

		sb.append("CACHE");
		sb.append(SPACE);

		sb.append(event.getSimpleActivity().getSimpleCode());
		sb.append(SPACE);

		sb.append(event.getSimpleMetric().getSimpleCode());
		sb.append(SPACE);
		
		sb.append(event.getSimpleKey().toString());
		sb.append(SPACE);

		stream.println(sb.toString());
	}
}
