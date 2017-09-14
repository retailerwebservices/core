package org.jimmutable.cloud.environment;

import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class HostName extends Stringable
{
	static public final HostName CURRENT_COMPUTER_NAME = getThisComputerName();
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	public HostName(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH, Validator.UNDERSCORE, Validator.DOT);
	}
	
	static public class MyConverter extends Stringable.Converter<HostName>
	{
		public HostName fromString(String str, HostName default_value)
		{
			try
			{
				return new HostName(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
	
	static private HostName getThisComputerName()
	{
		try
		{
			return new HostName(InetAddress.getLocalHost().getHostName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogManager.getRootLogger().error("Unable to determine a valid computer name!");
			System.exit(1); // Too much code depends on this, so you need to stop and fix the issue!
			return null;
		}
	}
}

