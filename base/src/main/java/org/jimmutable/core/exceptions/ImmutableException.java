package org.jimmutable.core.exceptions;

/**
 * A RuntimeException with the semantic meaning "hey! you just tried to modify
 * and object that is immutable (i.e. after it has been completed/frozen)"
 * 
 * @author jim.kane
 *
 */
@SuppressWarnings("serial")
public class ImmutableException extends RuntimeException
{
	public ImmutableException()
	{
		super();
	}
	
	public ImmutableException(String message)
	{
		super(message);
	}
}