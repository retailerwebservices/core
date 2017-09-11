package org.jimmutable.aws.servlet_utils.common_objects;

import org.jimmutable.core.objects.StandardImmutableObject;

/**
 * JSONServletResponse
 * Abstraction to standardize JSON servlet responses as an immutable object class
 * 
 * @author Preston McCumber
 */

public abstract class JSONServletResponse extends StandardImmutableObject<JSONServletResponse>
{
	public abstract int getSimpleHTTPResponseCode();

}
