package org.jimmutable.cloud.logging;

import org.apache.logging.log4j.Level;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

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
	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleValue());
		
		Level cur_level = Level.toLevel(getSimpleValue(), null);
		if (cur_level == null)
		{
			throw new ValidationException(String.format("Could not convert log level " + getSimpleValue() + " to a valid log4j Level"));
		}
		
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
