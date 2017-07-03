package org.jimmutable.aws.environment;

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
		
		char chars[] = getSimpleValue().toCharArray();
		for ( char ch : chars )
		{
			if ( ch >= 'a' && ch <= 'z' ) continue;
			if ( ch >= '0' && ch <= '9' ) continue;
			if ( ch == '_' ) continue;
			if ( ch == '-' ) continue;
			if ( ch == '.' ) continue;
			
			throw new ValidationException(String.format("Illegal character \'%c\' in host name %s.  Only lower case letters, numbers, underscore (_), dash (-) and dot (.) are allowed", ch, getSimpleValue()));
		}
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

