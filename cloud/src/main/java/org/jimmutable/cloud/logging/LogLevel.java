package org.jimmutable.cloud.logging;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.slf4j.event.Level;

/**
 * This is just a wrapper for log4j.Level that extends StandardObject to match
 * ISignal
 * 
 * @author avery.gonzales
 */
public class LogLevel extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
		
	private Level level; //Required
	
	public LogLevel( String value )
	{
		super(value);
	}

	@Override
	public void normalize()
	{
		normalizeTrim();
		normalizeUpperCase();
	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleValue());
		
//		org.apache.logging.log4j.Level.toLevel(getSimpleValue(), null);
		Level cur_level = null;
		try
		{
			 cur_level = Level.valueOf(getSimpleValue());
		}
		catch (Exception e)
		{
			throw new ValidationException(String.format("Could not convert log level " + getSimpleValue() + " to a valid Logging Level"));
		}
//		if (cur_level == null)
//		{
//			throw new ValidationException(String.format("Could not convert log level " + getSimpleValue() + " to a valid Logging Level"));
//		}
		
		this.level = cur_level;
	}
	
	public Level getSimpleLevel()
	{
		return level;
	}

	
	static public class MyConverter extends Stringable.Converter<LogLevel>
	{
		public LogLevel fromString( String str, LogLevel default_value )
		{
			try
			{
				return new LogLevel(str);
			}
			catch ( Exception e )
			{
				return default_value;
			}
		}
	}
}
