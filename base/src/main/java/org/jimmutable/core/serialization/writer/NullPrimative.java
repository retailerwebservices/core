package org.jimmutable.core.serialization.writer;

import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;

/**
 * A StandardWritable that will write a null in complete object encoding
 * 
 * @author jim.kane
 *
 */
public class NullPrimative implements StandardWritable 
{
	static public final NullPrimative NULL_PRIMATIVE = new NullPrimative();
	
	private NullPrimative() {}
	
	public TypeName getTypeName() 
	{
		return TypeName.TYPE_NAME_NULL;
	}

	public void write(ObjectWriter writer) 
	{
		writer.writeNull(FieldName.FIELD_NAME_PRIMITIVE_VALUE);
	}
	
}
