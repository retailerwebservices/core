package org.jimmutable.aws.servlet_utils.upsert;

import java.util.Objects;

import org.jimmutable.aws.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;

/**
 * CODE REVIEW: 
 * 
 * NEEDS JAVA DOC
 * 
 * @author kanej
 *
 */

public class UpsertResponseOK extends JSONServletResponse
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.common_objects.UpsertResponseOK"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.StandardObject FIELD_OBJECT = new FieldDefinition.StandardObject("object", null);
	static public final FieldDefinition.String FIELD_MESSAGE = new FieldDefinition.String("message", null);

	static private final int HTTP_STATUS_CODE_OK = 200; // CODE REVEIW: Put this in JSONServeletResponse as a static public
	
	private String message;  // optional
	private StandardImmutableObject object; // optional
	
	public UpsertResponseOK() {};
	
	public UpsertResponseOK(String message, StandardImmutableObject object)
	{
		this.message = message;
		this.object = object;
		complete();
	}

	public UpsertResponseOK(ObjectParseTree t)
	{
		this.message = t.getString(FIELD_MESSAGE);
		this.object = (StandardImmutableObject)t.getObject(FIELD_OBJECT);
	}
	
	@Override
	public int compareTo(JSONServletResponse obj)
	{
		if ( !(obj instanceof UpsertResponseOK) ) return 0;

		UpsertResponseOK other = (UpsertResponseOK) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getOptionalMessage(null), other.getOptionalMessage(null));
		return ret;
	}

	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeString(FIELD_MESSAGE, getOptionalMessage(null));
	}

	@Override
	public int getSimpleHTTPResponseCode() { return HTTP_STATUS_CODE_OK; }
	public String getOptionalMessage(String default_value) { return message; }  // CODE REVIEW: This implementation is wrong.  It won't return default_value if object is unset
	public StandardImmutableObject getOptionalObject(String default_value) { return object; } // CODE REVIEW: This implementation is wrong.  It won't return default_value if object is unset
	
	@Override
	public void freeze() {}
	@Override
	public void normalize() {}
	@Override
	public void validate() {}

	@Override
	public int hashCode()
	{
		return Objects.hash(getOptionalMessage(null), getOptionalObject(null));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof UpsertResponseOK))
			return false;

		UpsertResponseOK other = (UpsertResponseOK) obj;

		// CODE REVIEW: Use Objects.equals
		if ( !getOptionalMessage(null).equals(other.getOptionalMessage(null)) ) return false;
		if ( getOptionalObject(null) != other.getOptionalObject(null) ) return false;

		return true;
	}

}
