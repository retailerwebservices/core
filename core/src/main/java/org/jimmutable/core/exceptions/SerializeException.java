package org.jimmutable.core.exceptions;

/**
 * A standard exception to throw when a "serialization" error is encounter (e.g.
 * any type of i/o error, invalid serialization structure etc.)
 * 
 * @author jim.kane
 *
 */

@SuppressWarnings("serial")
public class SerializeException extends RuntimeException
{
	public SerializeException()
	{
		super();
	}
	
	public SerializeException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a "chained" serialization exception
	 * 
	 * @param message
	 * @param e
	 */
	public SerializeException(String message, Exception e)
	{
		super(message,e);
	}
}
