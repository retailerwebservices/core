package org.jimmutable.core.objects;

/**
 * The StandardEnum interface should be implemented by all "standard" enum(s).
 * 
 * A standard enum is easily converted to/from Strings, and supports an UNKNOWN
 * type
 * 
 * Each StandardEnum should implement a Convter, and have a static final Convter
 * as a member of the enum. See BindingType for an exmaple. The converer enables
 * easy serialziation
 * 
 * @author jim.kane
 *
 */
public interface StandardEnum 
{
	public String getSimpleCode();
	
	/**
	 * The job of a StandardEnum Converter is to take a string and make a
	 * StandardEnum (of the right type) out of it
	 * 
	 * This is used to streamline serialization
	 * 
	 * @author jim.kane
	 *
	 * @param <T>
	 */
	static abstract public class Converter<T extends StandardEnum>
	{
		abstract public T fromCode(String code, T default_value);
	}
}
