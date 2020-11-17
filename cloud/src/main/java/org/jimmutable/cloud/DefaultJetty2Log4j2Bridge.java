package org.jimmutable.cloud;

import org.eclipse.jetty.util.log.AbstractLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * User: Robert Franz Date: 2015-08-24 Time: 20:35
 */
public class DefaultJetty2Log4j2Bridge extends AbstractLogger
{
	private static  Logger logger;
	private String name;

	public DefaultJetty2Log4j2Bridge(String name)
	{
		this.name = name;
		logger = LoggerFactory.getLogger(name);
	}

	@Override
	protected org.eclipse.jetty.util.log.Logger newLogger(String fullname)
	{
		return new DefaultJetty2Log4j2Bridge(fullname);
	}

	public String getName()
	{
		return name;
	}

	public void warn(String msg, Object... args)
	{
		logger.warn(msg, args);
	}

	public void warn(Throwable thrown)
	{
		logger.warn(thrown.toString());
	}

	public void warn(String msg, Throwable thrown)
	{
		logger.warn(msg, thrown);
	}

	public void info(String msg, Object... args)
	{
		logger.info(msg, args);
	}

	public void info(Throwable thrown)
	{
		logger.info(thrown.toString());
	}

	public void info(String msg, Throwable thrown)
	{
		logger.info(msg, thrown);
	}

	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}

	public void setDebugEnabled(boolean enabled)
	{
		warn("setDebugEnabled not implemented", null, null);
	}

	public void debug(String msg, Object... args)
	{
		logger.debug(msg, args);
	}

	public void debug(Throwable thrown)
	{
		logger.debug(thrown.toString());
	}

	public void debug(String msg, Throwable thrown)
	{
		logger.debug(msg, thrown);
	}

	public void ignore(Throwable ignored)
	{
		logger.trace(ignored.toString());
	}
}
