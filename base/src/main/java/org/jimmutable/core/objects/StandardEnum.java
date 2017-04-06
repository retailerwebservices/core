package org.jimmutable.core.objects;

public interface StandardEnum 
{
	public String getSimpleCode();
	
	static abstract public class Converter<T extends StandardEnum>
	{
		abstract public T fromCode(String code, T default_value);
	}
}
