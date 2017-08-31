package org.jimmutable.aws.servlet_utils.common_objects;

import org.jimmutable.core.objects.StandardImmutableObject;

/**
 * CODE REVIEW
 * 
 * Needs javadoc comments
 * @author kanej
 *
 */

public abstract class JSONServletResponse extends StandardImmutableObject<JSONServletResponse>
{
	public abstract int getSimpleHTTPResponseCode();

}
