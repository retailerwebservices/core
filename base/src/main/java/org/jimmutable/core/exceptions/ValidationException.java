package org.jimmutable.core.exceptions;

/**
 * A standard exception to throw when a "validation" error is encounter (e.g. an
 * parameter is unexpectedly null, etc.)
 * 
 * @author jim.kane
 *
 */
@SuppressWarnings("serial")
public class ValidationException extends RuntimeException
{
	public ValidationException()
	{
		super();
	}
	
	public ValidationException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a "chained" validation exception
	 * 
	 * @param message
	 * @param e
	 */
	public ValidationException(String message, Exception e)
	{
		super(message,e);
	}
}
